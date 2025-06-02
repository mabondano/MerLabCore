package com.merlab.signals.rpn;

import java.util.ArrayList;
import java.util.List;

import com.merlab.signals.core.Signal;

/**
 * Pops [Signal, Number window], pushes Signal of same length:
 *  - out[0..w-2] = input[0..w-2]
 *  - out[i] = average(input[i-w+1 .. i]) for i>=w-1
 */
public class MovingAverageOp implements RPNOperation {

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Object apply(List<Object> args) {
        Signal s       = (Signal) args.get(0);
        int window     = ((Number) args.get(1)).intValue();
        if (window < 1) {
            throw new IllegalArgumentException("Window must be >= 1, was " + window);
        }

        List<Double> in = s.getValues();
        List<Double> out = new ArrayList<>(in.size());

        for (int i = 0; i < in.size(); i++) {
            // for the first window-1 samples, just pass through
            if (i < window - 1) {
                out.add(in.get(i));
            } else {
                // sliding average over the last 'window' samples
                double sum = 0;
                for (int j = i - window + 1; j <= i; j++) {
                    sum += in.get(j);
                }
                out.add(sum / window);
            }
        }

        return new Signal(out);
    }
    
    // MovingAverageOp.java
    @Override public String getName() { return "movavg"; }
    @Override public String getDescription() { return "Computes the moving average of a signal (window)."; }
    @Override public String getExample() { return "sig1 3 movavg"; }
    @Override public String getCategory() { return "Filter"; }
}
