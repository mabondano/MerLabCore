package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.MaxOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MaxOpTest {

    private final MaxOp op = new MaxOp();

    @Test
    void testMax() {
        Signal s = new Signal(Arrays.asList(7.0,3.0,5.0));
        double mx = (Double) op.apply(List.of(s));
        assertEquals(7.0, mx, 1e-9);
    }
}
