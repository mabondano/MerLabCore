/*
 * Copyright 2025 Merly Abondano
 *
 * Created:   2025-06-07
 * Author:    Merly Abondano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package com.merlab.signals.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.merlab.signals.core.SignalManager.RPNOp;
import com.merlab.signals.core.SignalProcessor.LengthMode;
import com.merlab.signals.features.FeatureExtractor;
import com.merlab.signals.nn.manager.NeuralNetworkManager;
import com.merlab.signals.nn.processor.DefaultNeuralNetworkProcessor;
import com.merlab.signals.persistence.DatabaseManager;
import com.merlab.signals.rpn.AddOp;
import com.merlab.signals.rpn.AddPadWithZerosOp;
import com.merlab.signals.rpn.AutocorrelationOp;
import com.merlab.signals.rpn.BandPassFilterOp;
import com.merlab.signals.rpn.BlendOp;
import com.merlab.signals.rpn.ClampOp;
import com.merlab.signals.rpn.ConvolveOp;
import com.merlab.signals.rpn.ConvolveReversedOp;
import com.merlab.signals.rpn.ConvolveReversedWithStrideOp;
import com.merlab.signals.rpn.DecimateOp;
import com.merlab.signals.rpn.DerivativeOp;
import com.merlab.signals.rpn.DivideOp;
import com.merlab.signals.rpn.FFTOp;
import com.merlab.signals.rpn.GaussianSmoothingOp;
import com.merlab.signals.rpn.GenerateNormalOp;
import com.merlab.signals.rpn.GenerateSineAllOp;
import com.merlab.signals.rpn.HighPassFilterOp;
import com.merlab.signals.rpn.IntegrateOp;
import com.merlab.signals.rpn.InterpolateOp;
import com.merlab.signals.rpn.KurtosisOp;
import com.merlab.signals.rpn.LQROp;
import com.merlab.signals.rpn.LowPassFilterOp;
import com.merlab.signals.rpn.MaxOp;
import com.merlab.signals.rpn.MeanOp;
import com.merlab.signals.rpn.MedianOp;
import com.merlab.signals.rpn.MinOp;
import com.merlab.signals.rpn.MovingAverageOp;
import com.merlab.signals.rpn.MultiplyOp;
import com.merlab.signals.rpn.NormalizeOp;
import com.merlab.signals.rpn.RPNEngine;
import com.merlab.signals.rpn.RPNParser;
import com.merlab.signals.rpn.RPNStack;
import com.merlab.signals.rpn.RangeOp;
import com.merlab.signals.rpn.ScaleOp;
import com.merlab.signals.rpn.SkewnessOp;
import com.merlab.signals.rpn.StdDevOp;
import com.merlab.signals.rpn.SubtractOp;
import com.merlab.signals.rpn.VarianceOp;
import com.merlab.signals.rpn.WeightedMovingAverageOp;
import com.merlab.signals.rpn.ZeroCrossingRateOp;

/**
 * Orquesta el flujo de procesamiento de señales usando SignalStack.
 */
public class SignalManager {
	
	public enum RPNOp {
	    ADD, SUBTRACT, MULTIPLY, DIVIDE; 
	}
	
    private final SignalProvider provider;
    private final SignalStack signalStack;
    private final DatabaseManager databaseManager;
    private final boolean doStats;
    private final boolean doFeatures;
    private final boolean doNN;
    
    // — nuevo: campos para el motor genérico —
    private final RPNEngine   rpnEngine;
    private final RPNParser   rpnParser;
    private final RPNStack    rpnStack;
    
    

    /**
     * Constructor principal.
     * 
     * @param provider       fuente de señales
     * @param signalStack    stack para almacenar pasos intermedios
     * @param databaseManager gestor de persistencia en BD
     * @param doStats        flag para calcular estadísticas
     * @param doFeatures     flag para extraer características
     * @param doNN           flag para ejecutar red neuronal
     */
    public SignalManager(SignalProvider provider,
                         SignalStack signalStack,
                         DatabaseManager databaseManager,
                         boolean doStats,
                         boolean doFeatures,
                         boolean doNN) {
        this.provider = provider;
        this.signalStack = signalStack;
        this.databaseManager = databaseManager;
        this.doStats = doStats;
        this.doFeatures = doFeatures;
        this.doNN = doNN;
        
        // Inicializar RPN genérico
        this.rpnEngine = new RPNEngine();
        // Registrar operaciones básicas
        rpnEngine.register("+",    new AddOp());
        rpnEngine.register("+pwz",    new AddPadWithZerosOp());
        rpnEngine.register("-",    new SubtractOp());
        rpnEngine.register("*",    new MultiplyOp());
        rpnEngine.register("/",    new DivideOp());
        rpnEngine.register("norm", new NormalizeOp());
        
        // … registrar aquí todas las demás ops …
        rpnEngine.register("dec", new DecimateOp());
        rpnEngine.register("ip", new InterpolateOp());       
        rpnEngine.register("scale",     new ScaleOp());
        rpnEngine.register("deriv",     new DerivativeOp());
        rpnEngine.register("intg",      new IntegrateOp());
        rpnEngine.register("conv",      new ConvolveOp());
        rpnEngine.register("convR",     new ConvolveReversedOp());
        rpnEngine.register("convRS",     new ConvolveReversedWithStrideOp());
        rpnEngine.register("lpf",       new LowPassFilterOp());
        rpnEngine.register("hpf",       new HighPassFilterOp());
        rpnEngine.register("bpf",       new BandPassFilterOp());
        rpnEngine.register("fft",       new FFTOp());
        rpnEngine.register("mean",      new MeanOp());
        rpnEngine.register("var",       new VarianceOp());
        rpnEngine.register("std",       new StdDevOp());
        rpnEngine.register("median",    new MedianOp());
        rpnEngine.register("min",       new MinOp());
        rpnEngine.register("max",       new MaxOp());
        rpnEngine.register("range",     new RangeOp());
        rpnEngine.register("acor",      new AutocorrelationOp());
        rpnEngine.register("movavg",    new MovingAverageOp());
        rpnEngine.register("wma",       new WeightedMovingAverageOp());
        rpnEngine.register("gsmooth",   new GaussianSmoothingOp());
        rpnEngine.register("zcr",       new ZeroCrossingRateOp());
        rpnEngine.register("lqr",       new LQROp());
        rpnEngine.register("skew",      new SkewnessOp());
        rpnEngine.register("kurt",      new KurtosisOp());
        rpnEngine.register("blend",     new BlendOp());
        rpnEngine.register("clamp",      new ClampOp());
        
        rpnEngine.register("gsin",      new GenerateSineAllOp());
        rpnEngine.register("gnorm",      new GenerateNormalOp());

        this.rpnParser = new RPNParser(rpnEngine);
        this.rpnStack  = new RPNStack();
    }
    
    /**
     * Ejecuta una expresión RPN genérica.
     * @param tokens     lista de tokens postfix, p. ej. ["sigA","sigB","+"]
     * @param variables  mapa de nombres→Signal para resolver "sigA","sigB",…
     * @return la señal resultante (tope de rpnStack)
     */
    public Signal operateWithRPN(List<String> tokens, Map<String,Signal> variables) {
        // Limpio el RPNStack para cada llamada
        rpnStack.clear();
        // Ejecuto todos los tokens
        rpnParser.parseAndExecute(tokens, variables, rpnStack);
        // El resultado final debe ser un Signal
        return (Signal) rpnStack.peek();
    }
    
    // — legado RPNOp/LengthMode —
    /**
     * Aplica una operación binaria RPN a las dos señales del stack.
     * @param op    operación (ADD, SUBTRACT, MULTIPLY, DIVIDE)
     * @param mode  cómo alinear longitudes (REQUIRE_EQUAL, PAD_WITH_ZEROS, THROW_ERROR)
     */
    public void operateRPN(RPNOp op, SignalProcessor.LengthMode mode) {
        // 1) Verificar al menos 2 señales
        if (signalStack.size() < 2) {
            throw new IllegalStateException("Se requieren ≥2 señales en el stack para operar");
        }

        // 2) Pre-peek para ver si las longitudes son compatibles
        //    (sin poppear aún)
        Signal second = signalStack.peekSecond(); // hipotético método peekSecond()
        Signal top = signalStack.peek();
        // Llama directamente a la validación del processor
        //SignalProcessor.validateAlignment(second.getValues(), top.getValues(), mode); // arroja IllegalArgumentException si no pasan

        // 3) Ahora sí, pop y pop
        //Signal a = signalStack.pop();
        //Signal b = signalStack.pop();

        // 3. Elegir la función de SignalProcessor
        List<Double> resultValues;
        switch (op) {
            case ADD:
                resultValues = SignalProcessor.addSignals(second.getValues(), top.getValues(), mode);
                break;
            case SUBTRACT:
                resultValues = SignalProcessor.subtractSignals(second.getValues(), top.getValues(), mode);
                break;
            case MULTIPLY:
                resultValues = SignalProcessor.multiplySignals(second.getValues(), top.getValues(), mode);
                break;
            case DIVIDE:
                resultValues = SignalProcessor.divideSignals(second.getValues(), top.getValues(), mode);
                break;
            default:
                throw new UnsupportedOperationException("Operación RPN no soportada: " + op);
        }
        
        // 4) Sólo **después** de un cálculo exitoso, eliminamos las dos señales
        signalStack.pop();  // quita 'top'
        signalStack.pop();  // quita 'second'

        // 4. Apilar el resultado
        signalStack.push(new Signal(resultValues));
    }    
    
    
    /**
     * Ejecuta todo el pipeline de señal de principio a fin.
     */
    public void runPipeline() {
        // 1. Cargar y apilar señal inicial
        // 1. Raw
        Signal raw = provider.getSignal();
        addSignal(raw);
        SignalPlotter.plotSignal("Señal Original", raw);

        // 2. Procesamiento básico
        normalizeLastSignal();
        Signal processed = signalStack.peek();
        SignalPlotter.plotSignal("Señal Procesada", processed);

        // 3. Estadísticas (opcional)
        if (doStats) {
            statsLastSignal();
            Signal stats = signalStack.peek();  
            SignalPlotter.plotSignal("Media & Varianza", stats);
        }

        // 4. Extracción de características (opcional)
        if (doFeatures) {
            featuresLastSignal();
            Signal feats = signalStack.peek(); 
            SignalPlotter.plotSignal("Características", feats);
        }

        // 5. Inferencia de red neuronal (opcional)
        if (doNN) {
            nnLastSignal();
            Signal nn    = signalStack.peek();
            SignalPlotter.plotSignal("Salida NN", nn);
        }
        

        // 6. Guardar resultado final
        saveLastSignal();
    }

    /**
     * Agrega una señal al stack.
     */
    public void addSignal(Signal signal) {
        signalStack.push(signal);
    }

    /**
     * Normaliza la última señal en el stack.
     */
    public void normalizeLastSignal2() {
        Signal last = signalStack.peek();
        List<Double> normalized = SignalProcessor.normalizeTo(last.getValues(), 1);
        last.setValues(normalized);
    }
    
    public void normalizeLastSignal3() {
        // 1) Verificamos que haya al menos una señal en el stack
        if (signalStack == null || signalStack.size() < 1) {
            throw new IllegalStateException("No hay señales en el stack para normalizar");
        }

        // 2) Ahora sí podemos obtener la señal con seguridad
        Signal last = signalStack.peek();
        if (last == null) {
            throw new IllegalStateException("La señal en el tope es nula");
        }

        // 3) Realizamos la normalización
        List<Double> normalized = SignalProcessor.normalizeTo(last.getValues(), 1.0);
        last.setValues(normalized);
    }
    
    public void normalizeLastSignal() {
        // 1) Comprobar stack no vacío
        if (signalStack == null || signalStack.size() < 1) {
            throw new IllegalStateException("No hay señales en el stack para normalizar");
        }
        Signal last = signalStack.peek();
        if (last == null) {
            throw new IllegalStateException("La señal en el tope es nula");
        }

        // 2) Obtengo los valores originales
        List<Double> orig = new ArrayList<>(last.getValues());
        // 3) Calculo el máximo (para dividir)
        double max = orig.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        if (max == 0.0) max = 1.0;

        // 4) Construyo la lista normalizada
        List<Double> norm = new ArrayList<>(orig.size());
        // Primer elemento siempre 0
        norm.add(0.0);
        // Resto: valor dividido por el máximo
        for (int i = 1; i < orig.size(); i++) {
            norm.add(orig.get(i) / max);
        }

        // 5) Actualizo la señal en el stack
        last.setValues(norm);
    }

    

    /**
     * Calcula estadísticas de la última señal en el stack y la apila.
     */
    public void statsLastSignal() {
        if (signalStack.isEmpty()) {
            throw new IllegalStateException("No hay señal en el stack para calcular estadísticas");
        }
        Signal last = signalStack.peek();
        Signal stats = StatisticalProcessor.extractStats(last);
        signalStack.push(stats);
    }

    /**
     * Extrae características de la última señal en el stack y la apila.
     */
    public void featuresLastSignal() {
        Signal last = signalStack.peek();
        Signal feats = FeatureExtractor.extractFeatures(last);
        signalStack.push(feats);
    }

    /**
     * Aplica la red neuronal a la última señal en el stack y la apila.
     */
    public void nnLastSignal() {
        Signal last = signalStack.peek();
        Signal pred = null;
		try {
			pred = DefaultNeuralNetworkProcessor.predict2(last);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        signalStack.push(pred);
    }

    /**
     * Aplica la red neuronal a la última señal en el stack y la apila,
     * usando un NeuralNetworkManager separado.
     */
    public void nnLastSignal2() {
        // 1. Instancia aquí tu NeuralNetworkManager
        NeuralNetworkManager nnManager =
            new NeuralNetworkManager(
                signalStack,
                new DefaultNeuralNetworkProcessor(), // o tu implementación real
                databaseManager
            );

        // 2. Ejecuta la inferencia (internamente empuja al stack)
        nnManager.runInference();
    }
    
    /**
     * Guarda la última señal en la base de datos.
     */
    public void saveLastSignal() {
        Signal last = signalStack.peek();
        databaseManager.saveSignal(last);
    }

    /**
     * Muestra todas las señales en el stack por consola.
     */
    public void showStack() {
        for (Signal s : signalStack.getStack()) {
            s.println();
        }
    }

    /**
     * Retorna la última señal sin modificar el stack.
     */
    public Signal getLastSignal() {
        return signalStack.peek();
    }    
    
    // Mostrar todas las señales en el stack
    public void showStack2() {
        // Verifica que signalStack no sea null
        if (signalStack != null) {
            for (Signal signal : signalStack.getStack()) {
                signal.println(); // Muestra los valores de la señal
            }
        } else {
            System.out.println("El stack está vacío o no ha sido inicializado.");
        }
    }
    
    /**
     * Suma las dos últimas señales del stack y apila el resultado.
     * @throws IllegalStateException si no hay al menos 2 señales en el stack
     */
    public void sumLastTwoSignals() {
        // 1) Validar tamaño del stack
        if (signalStack.size() < 2) {
            throw new IllegalStateException(
                "Se requieren al menos 2 señales en el stack para sumar"
            );
        }

        // 2) Sacar las dos señales más recientes
        Signal s2 = signalStack.pop();
        Signal s1 = signalStack.pop();

        // 3) Operar con el processor (requiere igual longitud o PAD_WITH_ZEROS)
        List<Double> summed = SignalProcessor.addSignals(
            s1.getValues(),
            s2.getValues(),
            SignalProcessor.LengthMode.REQUIRE_EQUAL
        );

        // 4) Crear nueva Signal y apilarla
        signalStack.push(new Signal(summed));
    }    
}
