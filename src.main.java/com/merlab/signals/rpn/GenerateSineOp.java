package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalGenerator;

public class GenerateSineOp implements RPNOperation {
    @Override
    public int arity() { return 0; } // No toma argumentos del stack

    @Override
    public Object apply(List<Object> args) {
        // Aquí puedes fijar los parámetros o recibirlos desde args
        Signal signal = new SignalGenerator(
            SignalGenerator.Type.SINE,
            16, 1.0, 0.2, 0.0, false, 0, 0.0, null
        ).getSignal();
        return signal;
    }
    
    // GenerateSineAllOp.java
    @Override public String getName() { return "gsin"; }
    @Override public String getDescription() { return "Generates a sine signal. Args: [size, amplitude, freq]"; }
    @Override public String getExample() { return "size amplitude freq gsin"; }
    @Override public String getCategory() { return "Generator"; }
}
