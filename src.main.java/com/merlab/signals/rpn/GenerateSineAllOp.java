package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.SignalGenerator;

public class GenerateSineAllOp implements RPNOperation {
    @Override
    public int arity() { return 5; } // size, amplitude, frequency, phase

    @Override
    public Object apply(List<Object> args) {
        // Los argumentos llegan en orden [size, amplitude, frequency, phase]
        int size = ((Double) args.get(0)).intValue();
        double amplitude = (Double) args.get(1);
        double frequency = (Double) args.get(2);
        double phase = (Double) args.get(3);
        boolean normalizeToPercent = false;
        return SignalGenerator.generateSineAll(size, amplitude, frequency, phase, normalizeToPercent);
    }
    
    // GenerateSineAllOp.java
    @Override public String getName() { return "gsinall"; }
    @Override public String getDescription() { return "Generates a sine signal. Args: [size, amplitude, freq, phase, normalize]"; }
    @Override public String getExample() { return "size amplitude freq phase norm gsin"; }
    @Override public String getCategory() { return "Generator"; }

}
