package com.merlab.signals.experimental.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;
import com.merlab.signals.core.SignalProcessor.LengthMode;
import com.merlab.signals.rpn.RPNOperation;

/**
 * Alternative AddOp design kept for comparison.
 * This version uses the simple RPNOperation API and always requires equal lengths.
 */
public class AddOpSimpleAlternative implements RPNOperation {

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Object apply(List<Object> args) {
        Signal a = (Signal) args.get(0);
        Signal b = (Signal) args.get(1);
        List<Double> sum = SignalProcessor.addSignals(
            a.getValues(),
            b.getValues(),
            LengthMode.REQUIRE_EQUAL
        );
        return new Signal(sum);
    }

    @Override
    public String getName() {
        return "+";
    }

    @Override
    public String getDescription() {
        return "Adds two equal-length signals element-wise.";
    }

    @Override
    public String getExample() {
        return "sig1 sig2 +";
    }

    @Override
    public String getCategory() {
        return "Arithmetic";
    }
}
