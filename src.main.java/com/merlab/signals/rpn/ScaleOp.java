package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;

/**
 * RPN operation “scale”:
 *   Pops [ Signal, factor, divideFlag ]
 *   If divideFlag==false → multiplies each sample by factor
 *   If divideFlag==true  → divides each sample by factor
 */
public class ScaleOp implements RPNOperation {

    @Override
    public int arity() {
        return 3;
    }

    @Override
    public Object apply(List<Object> args) {
        Signal s       = (Signal) args.get(0);
        double factor  = ((Double) args.get(1)).doubleValue();
        boolean divide = ((Boolean) args.get(2)).booleanValue();

        List<Double> out = SignalProcessor.scale(
            s.getValues(),
            factor,
            divide
        );
        return new Signal(out);
    }
    
    // ScaleOp.java
    @Override public String getName() { return "scale"; }
    @Override public String getDescription() { return "Scales a signal by a given factor."; }
    @Override public String getExample() { return "sig1 2.0 scale"; }
    @Override public String getCategory() { return "Transform"; }
}

//Register
//engine.register("scale", new ScaleOp());

//Use
//stack.push(mySignal);
//stack.push(2.0);         // factor
//stack.push(false);       // false = multiplica
//engine.execute("scale", stack);

//Read
//Signal scaled = (Signal) stack.peek();