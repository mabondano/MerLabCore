package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.SignalProcessor;

/** Pops 1 Signal, pushes its linear‐quadratic ratio (Double). */
public class LQROp implements RPNOperation {
    @Override public int arity() { return 1; }
    
    @Override
    public Object apply(List<Object> args) {
        @SuppressWarnings("unchecked")
        List<Double> s = (List<Double>) args.get(0);
        double threshold = (Double) args.get(1);
        
        return SignalProcessor.lqr(s, threshold);
    }
    
    // LQROp.java
    @Override public String getName() { return "lqr"; }
    @Override public String getDescription() { return "Computes the Linear-Quadratic Regulator (placeholder)."; }
    @Override public String getExample() { return "sig1 lqr"; }
    @Override public String getCategory() { return "Statistics"; }
}
