package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.MovingAverageOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovingAverageOpTest {

    private final MovingAverageOp op = new MovingAverageOp();

    @Test
    void testMovingAverageWindow2() {
        Signal s = new Signal(Arrays.asList(1.0,2.0,3.0,4.0));
        Signal ma = (Signal) op.apply(List.of(s, 2.0));
        // [1.0, (1+2)/2, (2+3)/2, (3+4)/2]
        assertEquals(Arrays.asList(1.0,1.5,2.5,3.5), ma.getValues());
    }
}
