package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.StatisticalProcessor;

/** Pops [Signal, Double(windowSize)], pushes Gaussian‐smoothed signal. */
public class GaussianSmoothingOp implements RPNOperation {
    @Override public int arity() { return 2; }
    
    @Override
    public Object apply(List<Object> args) {
        Signal s         = (Signal) args.get(0);
        double window       = ((double) args.get(1));
        Signal gs  = StatisticalProcessor.gaussianSmoothing(s, window);
        return gs;
    }
    
    // GaussianSmoothingOp.java
    @Override public String getName() { return "gsmooth"; }
    @Override public String getDescription() { return "Smooths the signal with a Gaussian filter."; }
    @Override public String getExample() { return "sig1 sigma gsmooth"; }
    @Override public String getCategory() { return "Filter"; }
}
