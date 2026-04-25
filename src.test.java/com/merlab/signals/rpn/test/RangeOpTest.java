package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.RangeOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RangeOpTest {

    private final RangeOp op = new RangeOp();

    @Test
    void testRange() {
        Signal s = new Signal(Arrays.asList(2.0,5.0,1.0));
        double r = (Double) op.apply(List.of(s));
        assertEquals(5.0 - 1.0, r, 1e-9);
    }
}
