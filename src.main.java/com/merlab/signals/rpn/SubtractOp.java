package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;
import com.merlab.signals.core.SignalProcessor.LengthMode;

/** Binary RPN op that subtracts B from A */
public class SubtractOp implements RPNOperation {

    @Override public int arity() { return 2; }

    @Override
    public Object apply(List<Object> args) {
        Signal a = (Signal) args.get(0);
        Signal b = (Signal) args.get(1);
        List<Double> diff = SignalProcessor.subtractSignals(
            a.getValues(), b.getValues(), LengthMode.REQUIRE_EQUAL
        );
        return new Signal(diff);
    }
    
    // SubtractOp.java
    @Override public String getName() { return "-"; }
    @Override public String getDescription() { return "Subtracts two signals element-wise."; }
    @Override public String getExample() { return "sig1 sig2 -"; }
    @Override public String getCategory() { return "Arithmetic"; }
}
