package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.HighPassFilterOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HighPassFilterOpTest {

    private final HighPassFilterOp op = new HighPassFilterOp();

    @Test
    void testHighPassFilter() {
        Signal s = new Signal(Arrays.asList(1.0, 1.0, 1.0, 1.0));
        // alpha=0.5
        Signal out = (Signal) op.apply(List.of(s, 0.5));
        // y0=1; y1=0.5*(1 +1 -1)=0.5; y2=0.5*(0.5+1-1)=0.25; y3=0.5*(0.25+1-1)=0.125
        assertEquals(Arrays.asList(1.0, 0.5, 0.25, 0.125), out.getValues());
    }
}
