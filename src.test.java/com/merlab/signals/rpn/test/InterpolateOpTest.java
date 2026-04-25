package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.InterpolateOp;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InterpolateOpTest {

    private final InterpolateOp op = new InterpolateOp();

    @Test
    void testInterpolateFactor2() {
        Signal s = new Signal(Arrays.asList(0.0, 2.0, 4.0));
        Signal ip = (Signal) op.apply(List.of(s, 2.0));
        // expect: 0, 1, 2, 3, 4
        assertEquals(Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0), ip.getValues());
    }
}
