package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.CustomSignalGenerator;
import com.merlab.signals.core.Signal;

public class GenerateNormalOp implements RPNOperation {
    @Override
    public int arity() { return 0; }

    @Override
    public Object apply(List<Object> args) {
        Signal signal = new CustomSignalGenerator(
            CustomSignalGenerator.DistType.NORMAL,
            16, 1.0, 0.0, 0.0, 0.0, 0.2, 42L
        ).getSignal();
        return signal;
    }
    
    // GenerateNormalOp.java
    @Override public String getName() { return "gnorm"; }
    @Override public String getDescription() { return "Generates a normal (Gaussian) random signal."; }
    @Override public String getExample() { return "size mean stddev gnorm"; }
    @Override public String getCategory() { return "Generator"; }
}