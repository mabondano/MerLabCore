package com.merlab.signals.rpn.test;


import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.DerivativeOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DerivativeOpTest {

    private final DerivativeOp op = new DerivativeOp();

    @Test
    void testDerivativeBasic() {
        Signal s = new Signal(Arrays.asList(1.0, 3.0, 6.0, 10.0));
        Signal d = (Signal) op.apply(List.of(s));
        // diffs: [3-1, 6-3, 10-6]
        assertEquals(Arrays.asList(2.0, 3.0, 4.0), d.getValues());
    }

    @Test
    void testDerivativeSingleOrEmpty() {
        Signal a = new Signal(Arrays.asList(5.0));
        assertEquals(0, ((Signal)op.apply(List.of(a))).getValues().size());
    }
}
