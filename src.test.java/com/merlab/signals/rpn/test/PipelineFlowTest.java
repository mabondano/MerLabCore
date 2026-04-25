package com.merlab.signals.rpn.test;

import com.merlab.signals.core.CustomSignalGenerator;
import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalGenerator;
//import com.merlab.signals.DistributionGenerator;
import com.merlab.signals.rpn.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PipelineFlowTest {

    @Test
    void testPipelineFullFlow() {
        // Registro de operaciones necesarias en el RPNEngine
        RPNEngine engine = new RPNEngine();
        engine.register("+", new AddOp());
        engine.register("dec", new DecimateOp());
        engine.register("norm", new NormalizeOp());
        engine.register("mean", new MeanOp());

        RPNParser parser = new RPNParser(engine);
        RPNStack stack = new RPNStack();

        // 1. Generar señales
        // Usando SignalGenerator para seno
        SignalGenerator sineGen = new SignalGenerator(
                SignalGenerator.Type.SINE, // Tipo
                16,                        // size
                1.0,                       // amplitude
                0.2,                       // frequency
                0.0,                       // phase
                false,                     // normalizeToPercent
                0,                         // deltaPos
                0.0,                       // level
                null                       // filePath
        );
        Signal signalSine = sineGen.getSignal();

        // Usando CustomSignalGenerator para distribución normal (manual)
        CustomSignalGenerator normalGen = new CustomSignalGenerator(
                CustomSignalGenerator.DistType.NORMAL, // tipo
                16,    // size
                1.0,   // amplitude
                0.0,   // frequency
                0.0,   // phase
                0.0,   // p1 = media
                0.2,   // p2 = stddev
                42L    // seed
        );
        Signal signalNormal = normalGen.getSignal();

        // 2. Push al stack para pipeline
        stack.push(signalSine);
        stack.push(signalNormal);

        // 3. Ejecutar pipeline con parser (solo las ops, las señales ya las tienes en stack)
        parser.parseAndExecute("+ 2 dec norm mean", Map.of(), stack);

        // 4. Comprobar resultado
        Object result = stack.pop();
        assertTrue(result instanceof Double, "El resultado final debe ser Double (media de la señal procesada)");
        double mean = (Double) result;
        assertTrue(mean >= 0 && mean <= 1, "La media debe estar normalizada entre 0 y 1");
        System.out.println("Resultado pipeline mean: " + mean);
    }
}


/*

parser.parseAndExecute("+ 2 dec norm mean", Map.of(), stack);

El pipeline sería:

	Suma las señales
	
	Decima el resultado por 2
	
	Normaliza
	
	Calcula la media
*/