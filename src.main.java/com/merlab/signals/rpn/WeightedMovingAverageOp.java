package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.StatisticalProcessor;

/** Pops [Signal, Double(windowSize)], pushes weighted moving average. */
public class WeightedMovingAverageOp implements RPNOperation {
    @Override public int arity() { return 2; }
    
    @Override
    public Object apply(List<Object> args) {
        Signal s          = (Signal) args.get(0);
        @SuppressWarnings("unchecked")
        double[] window        = ((double[]) args.get(1));
        Signal wmaSignal  = StatisticalProcessor.weightedMovingAverage(s, window);

        return wmaSignal;
    }
    
    // WeightedMovingAverageOp.java
    @Override public String getName() { return "wma"; }
    @Override public String getDescription() { return "Computes the weighted moving average of a signal."; }
    @Override public String getExample() { return "sig1 weights wma"; }
    @Override public String getCategory() { return "Filter"; }
}
