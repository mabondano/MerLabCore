package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.StatisticalProcessor;

/**
 * Pops 1 Signal, pushes its [max–min] as a Double.
 */
public class RangeOp implements RPNOperation {

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object apply(List<Object> args) {
        // 1) Cast al tipo Signal
        Signal s = (Signal) args.get(0);

        // 2) Llamada a tu método de StatisticalProcessor
        //    (si tu método signature es range(Signal))
        double r = StatisticalProcessor.range(s);

        // 3) Devuelve el resultado como Double
        return r;
    }
    
    // RangeOp.java
    @Override public String getName() { return "range"; }
    @Override public String getDescription() { return "Computes the range (max-min) of a signal."; }
    @Override public String getExample() { return "sig1 range"; }
    @Override public String getCategory() { return "Statistics"; }
}
