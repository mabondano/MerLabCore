package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;

/** Unary RPN op that normalizes a Signal to [0,1] */
public class NormalizeOp implements RPNOperation {

    @Override 
    public int arity() { 
    	return 1; 
    }

    @Override
    public Object apply(List<Object> args) {
        Signal s = (Signal) args.get(0);
        List<Double> norm = SignalProcessor.normalizeTo(s.getValues(), 1.0);
        return new Signal(norm);
    }
    

	 // NormalizeOp.java
	 @Override public String getName() { return "norm"; }
	 @Override public String getDescription() { return "Normalizes a signal to [0,1]."; }
	 @Override public String getExample() { return "sig1 norm"; }
	 @Override public String getCategory() { return "Transform"; }
}
