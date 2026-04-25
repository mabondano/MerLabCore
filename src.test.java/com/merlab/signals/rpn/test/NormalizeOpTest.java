package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.NormalizeOp;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class NormalizeOpTest {

    private final NormalizeOp op = new NormalizeOp();

    @Test
    void testNormalize() {
        Signal s = new Signal(Arrays.asList(2.0, 4.0, 6.0));
        Object result = op.apply(List.of(s));
        List<Double> vals = ((Signal) result).getValues();
        assertEquals(Arrays.asList(0.0, 0.5, 1.0), vals);
    }
}
