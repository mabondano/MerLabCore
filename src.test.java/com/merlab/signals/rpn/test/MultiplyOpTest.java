package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.MultiplyOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MultiplyOpTest {

    private final MultiplyOp op = new MultiplyOp();

    @Test
    void testMultiplySignalSignal() {
        Signal a = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        Signal b = new Signal(Arrays.asList(4.0, 5.0, 6.0));
        Signal result = (Signal) op.apply(Arrays.asList(a, b));
        assertEquals(Arrays.asList(4.0, 10.0, 18.0), result.getValues());
    }

    @Test
    void testMultiplySignalScalar() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        // signal * scalar
        Signal result1 = (Signal) op.apply(Arrays.asList(s, 3.0));
        assertEquals(Arrays.asList(3.0, 6.0, 9.0), result1.getValues());
    }

    @Test
    void testMultiplyScalarSignal() {
        Signal s = new Signal(Arrays.asList(2.0, 4.0));
        // scalar * signal
        Signal result2 = (Signal) op.apply(Arrays.asList(5.0, s));
        assertEquals(Arrays.asList(10.0, 20.0), result2.getValues());
    }

    @Test
    void testMultiplySignalSignalUnequalLengthThrows() {
        Signal a = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        Signal b = new Signal(Arrays.asList(4.0, 5.0));
        assertThrows(IllegalArgumentException.class, () ->
            op.apply(Arrays.asList(a, b))
        );
    }

    @Test
    void testMultiplyInvalidTypesThrows() {
        // two scalars only → invalid for MultiplyOp
        assertThrows(IllegalArgumentException.class, () ->
            op.apply(Arrays.asList(2.0, 3.0))
        );
    }
}
