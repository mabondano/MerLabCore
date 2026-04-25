package com.merlab.signals.rpn.test;


import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.SubtractOp;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SubtractOpTest {

    private final SubtractOp op = new SubtractOp();

    @Test
    void testSubtractEqualLength() {
        Signal a = new Signal(Arrays.asList(5.0, 7.0, 9.0));
        Signal b = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        Object result = op.apply(List.of(a, b));
        List<Double> vals = ((Signal) result).getValues();
        assertEquals(Arrays.asList(4.0, 5.0, 6.0), vals);
    }

    @Test
    void testSubtractUnequalLengthThrows() {
        Signal a = new Signal(Arrays.asList(5.0, 7.0));
        Signal b = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        assertThrows(IllegalArgumentException.class, () ->
            op.apply(List.of(a, b))
        );
    }
}
