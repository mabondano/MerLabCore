package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.DecimateOp;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DecimateOpTest {

    private final DecimateOp op = new DecimateOp();

    @Test
    void testDecimateFactor2() {
        Signal s = new Signal(Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0));
        Signal dec = (Signal) op.apply(List.of(s, 2.0));
        assertEquals(Arrays.asList(0.0, 2.0, 4.0), dec.getValues());
    }

    @Test
    void testDecimateInvalidFactor() {
        Signal s = new Signal(Arrays.asList(0.0, 1.0, 2.0));
        assertThrows(IllegalArgumentException.class, () ->
            op.apply(List.of(s, 0.0))
        );
    }
}
