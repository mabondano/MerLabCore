package com.merlab.signals.experimental.rpn;

import java.util.ArrayList;
import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.RPNOperation;

/**
 * Alternative AddPadWithZerosOp design kept for comparison.
 * This version performs the padding locally instead of delegating to SignalProcessor.
 */
public class AddPadWithZerosManualAlternative implements RPNOperation {
    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Object apply(List<Object> args) {
        Signal a = (Signal) args.get(0);
        Signal b = (Signal) args.get(1);
        List<Double> av = a.getValues();
        List<Double> bv = b.getValues();
        int maxLen = Math.max(av.size(), bv.size());
        av = padWithZeros(av, maxLen);
        bv = padWithZeros(bv, maxLen);

        List<Double> result = new ArrayList<>(maxLen);
        for (int i = 0; i < maxLen; i++) {
            result.add(av.get(i) + bv.get(i));
        }
        return new Signal(result);
    }

    public static List<Double> padWithZeros(List<Double> input, int newLength) {
        List<Double> output = new ArrayList<>(input);
        while (output.size() < newLength) {
            output.add(0.0);
        }
        return output;
    }

    @Override
    public String getName() {
        return "+pwz";
    }

    @Override
    public String getDescription() {
        return "Adds two signals, padding the shorter one with zeros.";
    }

    @Override
    public String getExample() {
        return "sig1 sig2 +pwz";
    }

    @Override
    public String getCategory() {
        return "Arithmetic";
    }
}
