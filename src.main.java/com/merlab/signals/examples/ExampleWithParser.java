package com.merlab.signals.examples;


import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ExampleWithParser {
    public static void main(String[] args) {
        // 1) Preparamos motor y parser
        RPNEngine engine = new RPNEngine();
        RPNStack stack   = new RPNStack();
        RPNParser parser = new RPNParser(engine);

        // 2) Registramos operaciones
        engine.register("+",   new AddOp());
        engine.register("dec", new DecimateOp());
        engine.register("norm",new NormalizeOp());
        // … otras ops …

        // 3) Creamos dos señales
        Signal sig1 = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        Signal sig2 = new Signal(Arrays.asList(0.5, 0.5, 0.5, 0.5));

        // 4) Definimos variables RPN
        Map<String,Signal> vars = new HashMap<>();
        vars.put("sig1", sig1);
        vars.put("sig2", sig2);

        // 5) Expresión postfix
        String expr = "sig1 2 dec norm sig2 +";

        // 6) Parse & execute
        parser.parseAndExecute(expr, vars, stack);

        // 7) Recuperamos resultado
        Signal result = (Signal) stack.peek();
        System.out.println("Resultado RPN: " + result.getValues());
    }
}

//O, si prefieres una lista de tokens:
//List<String> tokens = List.of("sig1","2","dec","norm","sig2","+");
//parser.parseAndExecute(tokens, vars, stack);
