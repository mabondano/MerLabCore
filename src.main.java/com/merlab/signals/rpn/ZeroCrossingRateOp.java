package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.StatisticalProcessor;

/**
 * Pops 1 Signal, computes its zero‐crossing rate, and pushes back a Double.
 */
public class ZeroCrossingRateOp implements RPNOperation {
    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object apply(List<Object> args) {
        // 1) Cast to Signal, not List
        Signal sig = (Signal) args.get(0);
        // 2) Compute ZCR on its internal list
        double zcr = StatisticalProcessor.zeroCrossingRate(sig);
        // 3) Return the Double
        return zcr;
    }
    
    // ZeroCrossingRateOp.java
    @Override public String getName() { return "zcr"; }
    @Override public String getDescription() { return "Computes the zero-crossing rate of a signal."; }
    @Override public String getExample() { return "sig1 zcr"; }
    @Override public String getCategory() { return "Statistics"; }
}
