package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.ConvolveOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConvolveOpTest {

    private final ConvolveOp op = new ConvolveOp();

    @Test
    void testConvolveSimple() {
        Signal a = new Signal(Arrays.asList(1.0, 2.0));
        Signal b = new Signal(Arrays.asList(3.0, 4.0));
        Signal c = (Signal) op.apply(List.of(a, b));
        // convolution: [1*3, 1*4+2*3, 2*4] = [3, 10, 8]
        assertEquals(Arrays.asList(3.0, 10.0, 8.0), c.getValues());
    }
}
