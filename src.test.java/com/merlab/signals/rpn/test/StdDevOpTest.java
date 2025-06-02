package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.StdDevOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StdDevOpTest {

    private final StdDevOp op = new StdDevOp();

    @Test
    void testStdDev() {
        Signal s = new Signal(Arrays.asList(1.0,2.0,3.0));
        double σ = (Double) op.apply(List.of(s));
        assertEquals(Math.sqrt(2.0/3.0), σ, 1e-9);
    }
}
