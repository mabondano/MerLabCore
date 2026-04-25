package com.merlab.signals.rpn.test;

import org.junit.jupiter.api.*;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.GenerateSineAllOp;
import com.merlab.signals.rpn.RPNEngine;
import com.merlab.signals.rpn.RPNParser;
import com.merlab.signals.rpn.RPNStack;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class RPNParserGenerateSineAllOpTest {

    private RPNEngine engine;
    private RPNParser parser;
    private RPNStack stack;

    @BeforeEach
    void setUp() {
        engine = new RPNEngine();
        engine.register("gsinall", new GenerateSineAllOp());
        // Puedes agregar más operaciones si deseas encadenar (ej: "mean", "norm"…)
        parser = new RPNParser(engine);
        stack = new RPNStack();
    }

    @Test
    void testGenerateSineAll() {
        // gsinall 8 2.0 0.25 0.0 0.0   → tamaño 8, amplitud 2.0, frecuencia 0.25, fase 0.0, normalize false
        String expr = "8 2.0 0.25 0.0 0.0 gsinall";
        parser.parseAndExecute(expr, Collections.emptyMap(), stack);

        Object result = stack.pop();
        assertTrue(result instanceof Signal, "Result should be a Signal");

        Signal sig = (Signal) result;
        List<Double> values = sig.getValues();
        assertEquals(8, values.size(), "Signal size should be 8");

        // Verifica algunos valores esperados (ajusta según tu lógica de generación)
        // Por ejemplo, si tu señal debería empezar en 0.0:
        // assertEquals(0.0, values.get(0), 1e-9);
        // assertEquals(2.0, values.get(2), 1e-9); // si sabes los valores esperados

        // Simplemente imprime para depurar si necesitas:
        System.out.println("Sine values: " + values);
    }
}
