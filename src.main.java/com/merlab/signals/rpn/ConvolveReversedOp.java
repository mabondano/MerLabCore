package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;

/**
 * Pops [Signal A, Signal B], pushes Signal( convolveReverse(A,B) ).
 */
public class ConvolveReversedOp implements RPNOperation {

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Object apply(List<Object> args) {
        Signal a = (Signal) args.get(0);
        Signal b = (Signal) args.get(1);
        // call the List<List> version internally:
        List<Double> conv = SignalProcessor.convolveReversed(
          a.getValues(),
          b.getValues()
        );
        // wrap in a Signal before returning
        return new Signal(conv);
    }
    
    // ConvolveReversedOp.java
    @Override public String getName() { return "convR"; }
    @Override public String getDescription() { return "Convolves signal with reversed kernel (classic conv)."; }
    @Override public String getExample() { return "sig1 kernel convR"; }
    @Override public String getCategory() { return "Transform"; }
}
