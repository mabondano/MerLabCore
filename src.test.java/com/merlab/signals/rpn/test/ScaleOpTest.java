package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.ScaleOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScaleOpTest {

    private final ScaleOp op = new ScaleOp();

    @Test
    void testMultiply() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        // factor=2, divide=false
        Signal scaled = (Signal) op.apply(List.of(s, 2.0, false));
        assertEquals(Arrays.asList(2.0, 4.0, 6.0), scaled.getValues());
    }

    @Test
    void testDivide() {
        Signal s = new Signal(Arrays.asList(2.0, 4.0, 8.0));
        // factor=2, divide=true
        Signal scaled = (Signal) op.apply(List.of(s, 2.0, true));
        assertEquals(Arrays.asList(1.0, 2.0, 4.0), scaled.getValues());
    }
}
