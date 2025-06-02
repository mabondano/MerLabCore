package com.merlab.signals.rpn;

import java.util.ArrayList;
import java.util.List;
/*
public class AddPadWithZerosOp implements RPNOperation {
    @Override public int arity() { return 2; }
    @Override
    public Object apply(List<Object> args) {
        Signal a = (Signal) args.get(0);
        Signal b = (Signal) args.get(1);
        List<Double> av = a.getValues(), bv = b.getValues();
        int maxLen = Math.max(av.size(), bv.size());
        av = padWithZeros(av, maxLen);
        bv = padWithZeros(bv, maxLen);
        List<Double> result = new ArrayList<>(maxLen);
        for (int i = 0; i < maxLen; i++) result.add(av.get(i) + bv.get(i));
        return new Signal(result);
    }
    
    public static List<Double> padWithZeros(List<Double> input, int newLength) {
        List<Double> output = new ArrayList<>(input);
        while (output.size() < newLength) {
            output.add(0.0); // PADDING AL FINAL
        }
        return output;
    }

}
*/

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

