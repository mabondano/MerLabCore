package com.merlab.signals.examples;

import java.util.Arrays;
import java.util.List;

import com.merlab.signals.core.Complex;
import com.merlab.signals.core.DistributionGenerator;
import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalGenerator;
import com.merlab.signals.core.SignalManager;
import com.merlab.signals.core.SignalProcessor;
import com.merlab.signals.core.SignalProvider;
import com.merlab.signals.core.SignalStack;
import com.merlab.signals.core.DistributionGenerator.DistType;
import com.merlab.signals.core.SignalGenerator.Type;
import com.merlab.signals.core.SignalManager.RPNOp;
import com.merlab.signals.core.SignalProcessor.LengthMode;
import com.merlab.signals.persistence.DatabaseManager;

public class Main {
    public static void main(String[] args) {
    	
    	//*****************************************************************
        // Parámetros JDBC (aunque aquí usemos generator)
        String url      = "jdbc:mariadb://localhost:3306/test";
        String user     = "root";
        String password = "root";

        // 1. Fuente de señal
        SignalProvider provider = new SignalGenerator(
            SignalGenerator.Type.SINE, 100, 1.0, 1.0, 0.0, true, 0, 1, null
        );
        // SignalProvider provider = new DatabaseLoader(url, user, password);

        // 2. Componentes del pipeline
        SignalStack     stack   = new SignalStack();
        DatabaseManager db      = new DatabaseManager(url, user, password);

        // 3. Flags de etapa
        boolean doStats    = true;
        boolean doFeatures = true;
        boolean doNN       = false;

        // 4. Construir manager
        SignalManager manager = new SignalManager(
            provider, stack, db, doStats, doFeatures, doNN
        );

        // 5. Ejecutar TODO el pipeline (incluye generación, procesado, gráficas y guardado)
        manager.runPipeline();
        
        //*****************************************************************
        Signal dc   = SignalGenerator.generateDC(10, 5.0);
        Signal saw  = SignalGenerator.generateSawtooth(10, 1.0);
      
        System.out.println("DC:      " + dc.getValues());
        System.out.println("Sawtooth:" + saw.getValues());
        
        //*****************************************************************        
        // 1 ciclo, fase 0, ±1 de amplitud
        Signal raw = SignalGenerator.generateSineAll(100, 1.0, 1.0, 0.0, false);
        System.out.println("raw:" + raw.getValues());

        // 2 ciclos, fase π/2 (coseno), ±2 de amplitud
        Signal cos = SignalGenerator.generateSineAll(100, 2.0, 2.0, Math.PI/2, false);
        System.out.println("cos:" + cos.getValues());

        // 1 ciclo, fase 0, normalizado a [0–100]
        Signal norm = SignalGenerator.generateSineAll(100, 1.0, 1.0, 0.0, true);
        System.out.println("norm:" + norm.getValues());

        //*****************************************************************       
        // 1) Prueba del método que opera sobre List<Double>
        List<Double> original = Arrays.asList(1.0, 2.0, 3.0, 4.0);
        System.out.println("Original: " + original);

        // Multiplicar por 2
        List<Double> multiplied = SignalProcessor.scale(original, 2.0, false);
        System.out.println("Multiplicada ×2: " + multiplied);

        // Dividir por 2
        List<Double> divided = SignalProcessor.scale(original, 2.0, true);
        System.out.println("Dividida ÷2:     " + divided);


        // 2) Prueba del método que opera sobre Signal
        Signal sig = new Signal(original);
        System.out.println("Signal cruda:");
        sig.println();  // tu método println() debería imprimir los valores

        // Escalamos la señal dentro de un nuevo Signal
        Signal sigScaled = SignalProcessor.scaleSignal(sig, 0.5, false);
        System.out.println("Signal escalada ×0.5:");
        sigScaled.println();        
        
        //*****************************************************************  
        List<Double> sig2 = List.of(1.0, 4.0, 9.0, 16.0); // cuadrados
        System.out.println("Derivada dt=1: " + SignalProcessor.derivative(sig2));     // [3.0,5.0,7.0]
        System.out.println("Derivada dt=2: " + SignalProcessor.derivative(sig2, 2));  // [(9-1)/2=4.0, (16-4)/2=6.0]

        
        //*****************************************************************  
        List<Double> s1 = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> s2 = Arrays.asList(4.0, 5.0);

        System.out.println("s1: " + s1);
        System.out.println("s2: " + s2);

        // 1) Suma sólo si miden igual (lanza error)
        try {
            System.out.println("add REQUIRE_EQUAL: " +
                SignalProcessor.addSignals(s1, s2, SignalProcessor.LengthMode.REQUIRE_EQUAL));
        } catch (Exception e) {
            System.out.println("Error esperado: " + e.getMessage());
        }

        // 2) Suma rellenando con ceros
        System.out.println("add PAD_WITH_ZEROS: " +
            SignalProcessor.addSignals(s1, s2, SignalProcessor.LengthMode.PAD_WITH_ZEROS));

        // 3) Resta igual
        System.out.println("subtract PAD_WITH_ZEROS: " +
            SignalProcessor.subtractSignals(s1, s2, SignalProcessor.LengthMode.PAD_WITH_ZEROS));

        // 4) Multiplicación igual
        System.out.println("multiply PAD_WITH_ZEROS: " +
            SignalProcessor.multiplySignals(s1, s2, SignalProcessor.LengthMode.PAD_WITH_ZEROS));

        // 5) Convolución (s1 ⊛ s2)
        System.out.println("convolve: " +
            SignalProcessor.convolve(s1, s2, SignalProcessor.LengthMode.REQUIRE_EQUAL));        
        
        //*****************************************************************  
        // Señal de ejemplo
        List<Double> noisy = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        System.out.println("Señal original: " + noisy);

        // Filtro pasobajo con alpha = 0.5
        List<Double> filteredHalf = SignalProcessor.lowPassFilter(noisy, 0.5);
        System.out.println("LowPass α=0.5:    " + filteredHalf);

        // alpha = 1 ⇒ sin filtrado
        List<Double> filteredOne = SignalProcessor.lowPassFilter(noisy, 1.0);
        System.out.println("LowPass α=1.0:    " + filteredOne);

        // alpha = 0 ⇒ señal constante (sólo primer valor)
        List<Double> filteredZero = SignalProcessor.lowPassFilter(noisy, 0.0);
        System.out.println("LowPass α=0.0:    " + filteredZero);
        
        //*****************************************************************
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        System.out.println("Original:      " + data);

        List<Double> hp0 = SignalProcessor.highPassFilter(data, 0.0);
        System.out.println("HP α=0.0 →     " + hp0);

        List<Double> hp05 = SignalProcessor.highPassFilter(data, 0.5);
        System.out.println("HP α=0.5 →     " + hp05);

        List<Double> hp1 = SignalProcessor.highPassFilter(data, 1.0);
        System.out.println("HP α=1.0 →     " + hp1);
        
        //***************************************************************** 
        List<Double> data2 = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        System.out.println("Original:            " + data2);

        // Band-pass: eliminar DC y alto espectro
        List<Double> bp = SignalProcessor.bandPassFilter(data2, 0.5, 0.5);
        System.out.println("Band-pass αHP=0.5, αLP=0.5: " + bp);

        // Caso trivial: αHP=1 y αLP=1 → salida = señal original
        List<Double> bpId = SignalProcessor.bandPassFilter(data2, 1.0, 1.0);
        System.out.println("Band-pass αHP=1, αLP=1:     " + bpId);
        
        //***************************************************************** 
        List<Double> sig3    = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        List<Double> kernel = Arrays.asList(1.0, 0.5);

        // Stride = 1
        List<Double> conv1 = SignalProcessor.convolveReversedWithStride(sig3, kernel, 1);
        System.out.println("ConvolveReversed stride=1: " + conv1);
        // cálculo manual: 
        // rev(kernel)=[0.5,1], salidas en i=0..3: [1*0.5+2*1, 2*0.5+3*1, 3*0.5+4*1, 4*0.5+5*1] 
        // = [2.5, 3.5, 4.5, 5.5]

        // Stride = 2
        List<Double> conv2 = SignalProcessor.convolveReversedWithStride(sig3, kernel, 2);
        System.out.println("ConvolveReversed stride=2: " + conv2);
        // solo i=0 y i=2: [2.5, 4.5]
        
        //***************************************************************** 
        // Generar un seno de 8 muestras (un ciclo)
        int size = 8;
        Signal sin = SignalGenerator.generateSineAll(size, 1.0, 1.0, 0.0, false);
        System.out.println("Señal seno (8 muestras): " + sin.getValues());

        // Calcular FFT
        List<Complex> spectrum = SignalProcessor.fft(sin.getValues());
        System.out.println("FFT (coeficientes complejos):");
        for (int i = 0; i < spectrum.size(); i++) {
            Complex c = spectrum.get(i);
            System.out.printf(" bin %2d: %s | magn=%f%n", i, c, c.abs());
        }

        // Si quieres sólo magnitudes:
        System.out.println("FFT magnitudes:");
        for (Complex c : spectrum) {
            System.out.printf("%f ", c.abs());
        }
        System.out.println();
        
        //***************************************************************** 
        SignalManager mgr = new SignalManager(
                new SignalGenerator(), 
                new SignalStack(), 
                new DatabaseManager("jdbc:mariadb://localhost:3306/test", "root", "root") ,
                /*doStats*/ false,
                /*doFeatures*/ false,
                /*doNN*/ false
            );

            // 1) Dos periodos, fase 0
            Signal ss1 = SignalGenerator.generateSineAll(128, 1.0, 2.0, 0.0, false);
            mgr.addSignal(ss1);

            // 2) Tres periodos, fase π/2
            Signal ss2 = SignalGenerator.generateSineAll(128, 1.0, 3.0, Math.PI/2, false);
            mgr.addSignal(ss2);

            // 3) Sumar y mostrar
            mgr.sumLastTwoSignals();
            Signal suma = mgr.getLastSignal();
            System.out.println("Resultado de la suma: " + suma.getValues());
            
        //***************************************************************** 
        SignalManager mgr2 = new SignalManager(
                new SignalGenerator(), 
                new SignalStack(), 
                new DatabaseManager("jdbc:mariadb://localhost:3306/test", "root", "root") ,
                /*doStats*/ false,
                /*doFeatures*/ false,
                /*doNN*/ false
            );

        	// señales A y B de 128 muestras
        	Signal A = SignalGenerator.generateSineAll(128,1,2,0,false);
        	Signal B = SignalGenerator.generateSineAll(128,1,3,Math.PI/2,false);
        	Signal C = SignalGenerator.generateTriangle(128,1,1);

        	// Paso 1: A + B
        	mgr2.addSignal(A);
        	mgr2.addSignal(B);
        	mgr2.operateRPN(RPNOp.ADD, LengthMode.REQUIRE_EQUAL);
        	System.out.println("A+B → " + mgr.getLastSignal().getValues());

        	// Paso 2: (A+B) · C
        	mgr2.addSignal(C);
        	mgr2.operateRPN(RPNOp.MULTIPLY, LengthMode.REQUIRE_EQUAL);
        	System.out.println("(A+B)*C → " + mgr2.getLastSignal().getValues());

            	
        //***************************************************************** 
            // 1) Generador normal (media=0, sigma=1)
            SignalProvider normalGen = 
                new DistributionGenerator(
                    DistributionGenerator.DistType.NORMAL,
                    128,    // tamaño
                    0.0,    // media
                    1.0     // desviación estándar
                );
            Signal normalSignal = normalGen.getSignal();
            System.out.println("Señal Normal (128 muestras):");
            normalSignal.println();

            // 2) Generador uniforme (0 a 10)
            SignalProvider uniformGen =
                new DistributionGenerator(
                    DistributionGenerator.DistType.UNIFORM,
                    128,    // tamaño
                    0.0,    // min
                    10.0    // max
                );
            Signal uniformSignal = uniformGen.getSignal();
            System.out.println("Señal Uniforme (128 muestras):");
            uniformSignal.println();	
    }
}
