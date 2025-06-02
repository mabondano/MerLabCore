package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;
import com.merlab.signals.core.SignalProcessor.LengthMode;

/**
 * Binary RPN op that adds two Signals element‐wise.
 */
public class AddOp implements BinaryLengthModeOp {
    @Override
    public Object apply(RPNStack stack, LengthMode mode) {
        Signal b = (Signal) stack.pop();
        Signal a = (Signal) stack.pop();
        List<Double> out = SignalProcessor.addSignals(a.getValues(), b.getValues(), mode);
        Signal result = new Signal(out);
        stack.push(result);
        return result;
    }

    @Override
    public int arity() { return 2; }

    // Si tu engine lo requiere:
    
    @Override
    public Object apply(List<Object> args) {
        throw new UnsupportedOperationException("Use apply(stack, mode)");
    }

    // AddOp.java
    @Override public String getName() { return "+"; }
    @Override public String getDescription() { return "Adds two signals element-wise."; }
    @Override public String getExample() { return "sig1 sig2 +"; }
    @Override public String getCategory() { return "Arithmetic"; }
    
}
/*
public class AddOp implements RPNOperation {

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Object apply(List<Object> args) {
        // args: [ signalA, signalB ]
        Signal a = (Signal) args.get(0);
        Signal b = (Signal) args.get(1);
        List<Double> sum = SignalProcessor.addSignals(
            a.getValues(),
            b.getValues(), 
            LengthMode.REQUIRE_EQUAL 
            //LengthMode.PAD_WITH_ZEROS
        );
        return new Signal(sum);
    }
}
*/

