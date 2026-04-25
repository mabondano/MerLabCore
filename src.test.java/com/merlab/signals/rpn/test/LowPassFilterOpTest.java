package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.LowPassFilterOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LowPassFilterOpTest {

    private final LowPassFilterOp op = new LowPassFilterOp();

    @Test
    void testLowPassFilter() {
        Signal s = new Signal(Arrays.asList(0.0, 1.0, 0.0, 1.0));
        // alpha=0.5
        Signal out = (Signal) op.apply(List.of(s, 0.5));
        // y0=0; y1=0.5*1+0.5*0=0.5; y2=0.5*0+0.5*0.5=0.25; y3=0.5*1+0.5*0.25=0.625
        assertEquals(Arrays.asList(0.0, 0.5, 0.25, 0.625), out.getValues());
    }
}
