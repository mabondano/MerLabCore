package com.merlab.signals.rpn.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.ConvolveReversedOp;

public class ConvolveReversedOpTest {
    private final ConvolveReversedOp op = new ConvolveReversedOp();

    @Test
    void testConvolveReversed() {
        Signal a = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        Signal b = new Signal(Arrays.asList(4.0, 5.0));

        // apply() returns a Signal, so cast it:
        Signal resultSignal = (Signal) op.apply(List.of(a, b));
        List<Double> c = resultSignal.getValues();

        // reverse b → [5,4], conv: [1*5,1*4+2*5,2*4+3*5,3*4] = [5,14,23,12]
        assertEquals(Arrays.asList(5.0, 14.0, 23.0, 12.0), c);
    }

}
