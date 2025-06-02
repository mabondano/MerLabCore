package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.BlendOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlendOpTest {

    private final BlendOp op = new BlendOp();

    @Test
    void testBlend50_50() {
        Signal a = new Signal(Arrays.asList(1.0,3.0));
        Signal b = new Signal(Arrays.asList(5.0,7.0));
        // alpha=0.5 → 0.5*A + 0.5*B
        Signal out = (Signal) op.apply(List.of(a, b, 0.5));
        assertEquals(Arrays.asList(3.0,5.0), out.getValues());
    }
}
