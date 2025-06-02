package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;

/**
 * Pops [Signal, lowAlpha, highAlpha],  
 * applies high–pass with highAlpha,  
 * then low–pass with lowAlpha,  
 * and pushes Signal of the same length.
 */
public class BandPassFilterOp implements RPNOperation {

    @Override
    public int arity() {
        return 3;
    }

    @Override
    public Object apply(List<Object> args) {
        Signal s        = (Signal) args.get(0);
        double lowAlpha = ((Number) args.get(1)).doubleValue();
        double highAlpha= ((Number) args.get(2)).doubleValue();

        // 1) high–pass first (removes low‐freq)
        List<Double> highPassed = SignalProcessor.highPassFilter(
            s.getValues(), highAlpha
        );

        // 2) low–pass next (removes high‐freq)
        List<Double> bandPassed = SignalProcessor.lowPassFilter(
            highPassed, lowAlpha
        );

        // 3) wrap back into a Signal (same length guaranteed)
        return new Signal(bandPassed);
    }
    
    // BandPassFilterOp.java
    @Override public String getName() { return "bpf"; }
    @Override public String getDescription() { return "Applies a band-pass filter to a signal."; }
    @Override public String getExample() { return "sig1 low high bpf"; }
    @Override public String getCategory() { return "Filter"; }
}
