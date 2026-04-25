package com.merlab.signals.rpn;

import java.util.ArrayList;
import java.util.List;

import com.merlab.signals.core.Signal;

/**
 * Pops [Signal A, Signal B, Double α], then pushes
 * a blended Signal:  α·A + (1−α)·B  element-wise.
 */
public class BlendOp implements RPNOperation {
    @Override public int arity() { return 3; }

    @Override
    public Object apply(List<Object> args) {
        Signal A      = (Signal) args.get(0);
        Signal B      = (Signal) args.get(1);
        double alpha  = ((Double) args.get(2)).doubleValue();

        List<Double> aVals = A.getValues();
        List<Double> bVals = B.getValues();
        int n = Math.min(aVals.size(), bVals.size());
        List<Double> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            double v = alpha * aVals.get(i) + (1 - alpha) * bVals.get(i);
            out.add(v);
        }
        return new Signal(out);
    }
    
    // BlendOp.java
    @Override public String getName() { return "blend"; }
    @Override public String getDescription() { return "Blends two signals with a weight."; }
    @Override public String getExample() { return "sig1 sig2 weight blend"; }
    @Override public String getCategory() { return "Arithmetic"; }
}

//stack: [ sigA, sigB, 0.25 ]
//engine.execute("blend", stack);
//now top = 0.25·sigA + 0.75·sigB