package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;
import com.merlab.signals.core.SignalProcessor.LengthMode;


public class AddPadWithZerosOp implements RPNOperation{

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
            LengthMode.PAD_WITH_ZEROS
        );
        return new Signal(sum);
    }
    
    // AddPadWithZerosOp.java
    @Override public String getName() { return "+pwz"; }
    @Override public String getDescription() { return "Adds two signals, padding shorter one with zeros."; }
    @Override public String getExample() { return "sig1 sig2 +pwz"; }
    @Override public String getCategory() { return "Arithmetic"; }
}

