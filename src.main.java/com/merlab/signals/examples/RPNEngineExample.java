package com.merlab.signals.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalGenerator;
import com.merlab.signals.rpn.*;

import java.util.Arrays;

public class RPNEngineExample {
    public static void main(String[] args) {
        // 1) Setup
        RPNEngine engine = new RPNEngine();
        RPNStack stack   = new RPNStack();
        engine.register("+", new AddOp());
        engine.register("norm", new NormalizeOp());
        engine.register("dec", new DecimateOp());

        // 2) Demo: normalize a sine, then decimate by 4 and add to itself
        Signal sine = SignalGenerator.generateSine(32, 1.0, 1.0);
        stack.push(sine);
        engine.execute("norm", stack);
        stack.push(4.0);
        engine.execute("dec", stack);
        stack.push(stack.peek());     // duplicate for addition
        engine.execute("+", stack);

        // 3) Show result
        Signal result = (Signal) stack.peek();
        System.out.println("RPN result: " + result.getValues());
    }
}
