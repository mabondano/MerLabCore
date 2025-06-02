package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.MedianOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MedianOpTest {

    private final MedianOp op = new MedianOp();

    @Test
    void testMedianOdd() {
        Signal s = new Signal(Arrays.asList(5.0,1.0,9.0));
        double med = (Double) op.apply(List.of(s));
        assertEquals(5.0, med, 1e-9);
    }

    @Test
    void testMedianEven() {
        Signal s = new Signal(Arrays.asList(1.0,2.0,3.0,4.0));
        double med = (Double) op.apply(List.of(s));
        assertEquals(2.5, med, 1e-9);
    }
}
