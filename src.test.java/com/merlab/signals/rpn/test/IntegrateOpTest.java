package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.IntegrateOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrateOpTest {

    private final IntegrateOp op = new IntegrateOp();

    @Test
    void testIntegrateBasic() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        Signal i = (Signal) op.apply(List.of(s));
        // cumulative sums: [1, 1+2, 1+2+3]
        assertEquals(Arrays.asList(1.0, 3.0, 6.0), i.getValues());
    }
}
