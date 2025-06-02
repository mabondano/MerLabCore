package com.merlab.signals.rpn;

import java.util.List;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;
import com.merlab.signals.core.SignalProcessor.LengthMode;


/**
 * RPN “*” operation:
 * - Si recibe [Signal,Signal] → producto elemento–a–elemento
 * - Si recibe [Signal,Number] o [Number,Signal] → escala la señal
 */
public class MultiplyOp implements RPNOperation {

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Object apply(List<Object> args) {
        Object o1 = args.get(0);
        Object o2 = args.get(1);

        // 1) Signal * Signal
        if (o1 instanceof Signal && o2 instanceof Signal) {
            Signal a = (Signal) o1;
            Signal b = (Signal) o2;
            List<Double> prod = SignalProcessor.multiplySignals(
                a.getValues(), b.getValues(), LengthMode.REQUIRE_EQUAL
            );
            return new Signal(prod);
        }

        // 2) Signal * scalar  ó  scalar * Signal
        if (o1 instanceof Signal && o2 instanceof Number) {
            Signal a = (Signal) o1;
            double factor = ((Number) o2).doubleValue();
            List<Double> scaled = SignalProcessor.scale(
                a.getValues(),
                factor,
                false   // divide=false → multiplicar
            );
            return new Signal(scaled);
        }
        if (o1 instanceof Number && o2 instanceof Signal) {
            double factor = ((Number) o1).doubleValue();
            Signal a = (Signal) o2;
            List<Double> scaled = SignalProcessor.scale(
                a.getValues(),
                factor,
                false
            );
            return new Signal(scaled);
        }

        throw new IllegalArgumentException(
            "MultiplyOp: tipos inválidos: "
            + o1.getClass().getSimpleName() + ", "
            + o2.getClass().getSimpleName()
        );
    }
    
    // MultiplyOp.java
    @Override public String getName() { return "*"; }
    @Override public String getDescription() { return "Multiplies two signals element-wise."; }
    @Override public String getExample() { return "sig1 sig2 *"; }
    @Override public String getCategory() { return "Arithmetic"; }
}


/**
 * Pops two Signals, multiplies element-wise, pushes the result.
 */
/*
public class MultiplyOp implements RPNOperation {

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Object apply(List<Object> args) {
        Signal a = (Signal) args.get(0);
        Signal b = (Signal) args.get(1);
        List<Double> prod = SignalProcessor.multiplySignals(
            a.getValues(), b.getValues(), LengthMode.REQUIRE_EQUAL
        );
        return new Signal(prod);
    }
}
*/
