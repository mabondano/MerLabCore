package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.VarianceOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VarianceOpTest {

    private final VarianceOp op = new VarianceOp();

    @Test
    void testVariance() {
        Signal s = new Signal(Arrays.asList(1.0,2.0,3.0));
        // population variance = ((1-2)^2+(2-2)^2+(3-2)^2)/3 = 0.6666667
        double v = (Double) op.apply(List.of(s));
        assertEquals(2.0/3.0, v, 1e-9);
    }
}
