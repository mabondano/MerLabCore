package com.merlab.signals.rpn;

import java.util.ArrayList;
import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;

/**
 * Pops [ Signal A, Signal B, Number stride ], pushes Signal(
 *   every‐stride sample of reverse‐kernel convolution of A and B
 * ).
 */
public class ConvolveReversedWithStrideOp implements RPNOperation {

    @Override
    public int arity() {
        return 3;
    }

    @Override
    public Object apply(List<Object> args) {
        // 1) cast inputs
        Signal a = (Signal)   args.get(0);
        Signal b = (Signal)   args.get(1);
        Number n = (Number)   args.get(2);
        int stride = n.intValue();

        // 2) validate
        if (stride < 1) {
            throw new IllegalArgumentException("Stride must be >= 1, was: " + stride);
        }

        // 3) full reversed‐kernel convolution
        List<Double> full = SignalProcessor.convolveReversed(
            a.getValues(),
            b.getValues()
        );

        // 4) subsample every 'stride'th element
        List<Double> out = new ArrayList<>();
        for (int i = 0; i < full.size(); i += stride) {
            out.add(full.get(i));
        }

        // 5) wrap and return
        return new Signal(out);
    }
    
    // ConvolveReversedWithStrideOp.java
    @Override public String getName() { return "convRS"; }
    @Override public String getDescription() { return "Convolves with reversed kernel and stride."; }
    @Override public String getExample() { return "sig1 kernel stride convRS"; }
    @Override public String getCategory() { return "Transform"; }
}
