package com.merlab.signals;

import java.util.ArrayList;
import java.util.List;

import com.merlab.signals.SignalManager.RPNOp;
import com.merlab.signals.SignalProcessor.LengthMode;

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
    }
    
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
        Signal pred = NeuralNetworkProcessor.predict(last);
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
                new NeuralNetworkProcessor(), // o tu implementación real
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
