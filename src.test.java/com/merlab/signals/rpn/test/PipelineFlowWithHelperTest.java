package com.merlab.signals.rpn.test;

import com.merlab.signals.rpn.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PipelineFlowWithHelperTest {
	
	@Test
	void testFullPipelineWithHelper() {
	    RPNEngine engine = TestUtil.createEngineWithBasicOps();
	    RPNParser parser = new RPNParser(engine);
	    RPNStack stack = new RPNStack();
	
	    // Ejemplo: genera sin, normal, suma, decima, normaliza, media
	    parser.parseAndExecute("gsin gnorm + 2 dec norm mean", Map.of(), stack);
	
	    // Verifica el resultado final (por ejemplo, media ≈ 0 para sin + normal)
	    Object result = stack.pop();
	    assertTrue(result instanceof Double);
	    double mean = (Double) result;
	    // Depende del caso, aquí puedes ajustar la tolerancia
	    assertEquals(0.0, mean, 0.6);
	}

    /*
    @Test
    void testPipelineFullFlow() {
        // 1. Setup: Engine with registered ops
        RPNEngine engine = TestUtil.createEngineWithBasicOps(); // Tu helper: registra gsin, gnorm, +, dec, norm, mean, etc.
        RPNParser parser = new RPNParser(engine);
        RPNStack stack = new RPNStack();

        // 2. Define pipeline expression
        String expr = "gsin 16 1.0 0.2 0.0 gnorm 16 0.0 0.2 + dec norm mean";

        // 3. Ejecutar el pipeline
        parser.parseAndExecute(expr, Map.of(), stack);

        // 4. Comprobar resultado
        Object result = stack.pop();
        assertTrue(result instanceof Double, "The result should be a Double (mean of processed signal)");
        double mean = (Double) result;

        // 5. Chequeo básico de rango
        assertTrue(mean >= 0 && mean <= 1, "Mean should be normalized between 0 and 1 after normalization");

        // 6. Output para inspección manual
        System.out.println("Pipeline mean result: " + mean);
    }
    */
}

