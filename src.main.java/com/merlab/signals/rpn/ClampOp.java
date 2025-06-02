package com.merlab.signals.rpn;

import java.util.ArrayList;
import java.util.List;

import com.merlab.signals.core.Signal;

/**
 * Pops [Signal, Double minVal, Double maxVal], then pushes
 * a clamped Signal where each sample x is:
 *   x < minVal ? minVal
 * : x > maxVal ? maxVal
 * : x
 */
public class ClampOp implements RPNOperation {
    @Override public int arity() { return 3; }

    @Override
    public Object apply(List<Object> args) {
        Signal s        = (Signal) args.get(0);
        double minVal   = ((Double) args.get(1)).doubleValue();
        double maxVal   = ((Double) args.get(2)).doubleValue();

        List<Double> vals = s.getValues();
        List<Double> out  = new ArrayList<>(vals.size());
        for (double x : vals) {
            if (x < minVal)      out.add(minVal);
            else if (x > maxVal) out.add(maxVal);
            else                  out.add(x);
        }
        return new Signal(out);
    }
    
    // ClampOp.java
    @Override public String getName() { return "clamp"; }
    @Override public String getDescription() { return "Clamps signal values between min and max."; }
    @Override public String getExample() { return "sig1 min max clamp"; }
    @Override public String getCategory() { return "Transform"; }
}

//stack: [ sigC, -1.0, 1.0 ]
//engine.execute("clamp", stack);
//now top = sigC with all values clamped to [-1,1]