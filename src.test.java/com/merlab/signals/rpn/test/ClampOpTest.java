package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.ClampOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClampOpTest {

    private final ClampOp op = new ClampOp();

    @Test
    void testClamp() {
        Signal s = new Signal(Arrays.asList(-1.0, 0.5, 2.0));
        // clamp to [0.0,1.0]
        Signal out = (Signal) op.apply(List.of(s, 0.0, 1.0));
        assertEquals(Arrays.asList(0.0,0.5,1.0), out.getValues());
    }
}
