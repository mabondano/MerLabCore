package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.BandPassFilterOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BandPassFilterOpTest {

    private final BandPassFilterOp op = new BandPassFilterOp();

    @Test
    void testBandPassFilterKeepsLength() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        Signal out = (Signal) op.apply(List.of(s, 0.2, 0.8));
        assertEquals(s.getValues().size(), out.getValues().size());
        // values should remain bounded between original min/max
        assertTrue(out.getValues().stream().allMatch(v -> v >= 1.0 && v <= 4.0));
    }
}
