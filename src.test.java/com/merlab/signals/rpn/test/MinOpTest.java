package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.MinOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MinOpTest {

    private final MinOp op = new MinOp();

    @Test
    void testMin() {
        Signal s = new Signal(Arrays.asList(7.0,3.0,5.0));
        double mn = (Double) op.apply(List.of(s));
        assertEquals(3.0, mn, 1e-9);
    }
}
