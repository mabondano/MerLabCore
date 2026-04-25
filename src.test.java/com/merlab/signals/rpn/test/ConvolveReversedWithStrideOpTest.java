package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.ConvolveReversedWithStrideOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConvolveReversedWithStrideOpTest {

    private final ConvolveReversedWithStrideOp op = new ConvolveReversedWithStrideOp();

    @Test
    void testConvolveReversedStride1() {
        Signal a = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        Signal b = new Signal(Arrays.asList(4.0, 5.0));
        Signal result = (Signal) op.apply(List.of(a, b, 1.0));
        assertEquals(List.of(5.0, 14.0, 23.0, 12.0), result.getValues());
    }

    @Test
    void testConvolveReversedStride2() {
        Signal a = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        Signal b = new Signal(Arrays.asList(4.0, 5.0));
        Signal result = (Signal) op.apply(List.of(a, b, 2.0));
        assertEquals(List.of(5.0, 23.0), result.getValues());
    }

    @Test
    void testConvolveReversedInvalidStride() {
        Signal a = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        Signal b = new Signal(Arrays.asList(4.0, 5.0));
        // now the Number‐based cast will succeed, then your
        // stride<1 check will throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () ->
            op.apply(List.of(a, b, 0.0))
        );
    }
}
