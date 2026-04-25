package com.merlab.signals.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalGenerator;
import com.merlab.signals.rpn.*;

public class RPNEngineExample2 {
    public static void main(String[] args) {
        // 1) Setup
        RPNEngine engine = new RPNEngine();
        RPNStack stack   = new RPNStack();
        engine.register("+", new AddOp());
        engine.register("norm", new NormalizeOp());
        engine.register("dec", new DecimateOp());

        // 2) Generate and normalize a sine wave
        Signal sine = SignalGenerator.generateSine(16, 1.0, 1.0);
        stack.push(sine);
        engine.execute("norm", stack);

        // 3) Decimate by factor 2 and then add to itself
        stack.push(2.0);
        engine.execute("dec", stack);
        stack.push(stack.peek());   // duplicate result
        engine.execute("+", stack);

        // 4) Show final
        Signal result = (Signal) stack.peek();
        System.out.println("RPN result: " + result.getValues());
    }
}
