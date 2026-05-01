package com.merlab.signals.experimental.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;
import com.merlab.signals.core.SignalProcessor.LengthMode;
import com.merlab.signals.rpn.RPNOperation;

/**
 * Alternative MultiplyOp design kept for comparison.
 * This version only supports Signal * Signal multiplication.
 */
public class MultiplyOpSignalOnlyAlternative implements RPNOperation {

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Object apply(List<Object> args) {
        Signal a = (Signal) args.get(0);
        Signal b = (Signal) args.get(1);
        List<Double> prod = SignalProcessor.multiplySignals(
            a.getValues(), b.getValues(), LengthMode.REQUIRE_EQUAL
        );
        return new Signal(prod);
    }

    @Override
    public String getName() {
        return "*";
    }

    @Override
    public String getDescription() {
        return "Multiplies two equal-length signals element-wise.";
    }

    @Override
    public String getExample() {
        return "sig1 sig2 *";
    }

    @Override
    public String getCategory() {
        return "Arithmetic";
    }
}
