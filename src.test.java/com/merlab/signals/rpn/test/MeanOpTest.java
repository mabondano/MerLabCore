package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.MeanOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeanOpTest {

    private final MeanOp op = new MeanOp();

    @Test
    void testMean() {
        Signal s = new Signal(Arrays.asList(1.0,2.0,3.0,4.0));
        double m = (Double) op.apply(List.of(s));
        assertEquals(2.5, m, 1e-9);
    }
}
