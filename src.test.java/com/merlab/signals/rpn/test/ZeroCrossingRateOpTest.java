package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.ZeroCrossingRateOp;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZeroCrossingRateOpTest {
    private final ZeroCrossingRateOp op = new ZeroCrossingRateOp();
    private static final double TOL = 1e-9;

    @Test
    void testZeroCrossingRateNoCross() {
        Signal s = new Signal(Arrays.asList(1.0, 1.0, 1.0));
        // op.apply(...) now returns a Double
        double zcr = (Double) op.apply(List.of(s));
        assertEquals(0.0, zcr, TOL);
    }
}
