package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor.LengthMode;
import com.merlab.signals.rpn.AddOp;
import com.merlab.signals.rpn.RPNStack;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AddOpTest {

    
    @Test
    void testAddEqualLength() {
        RPNStack stack = new RPNStack();
        stack.push(new Signal(List.of(1.0, 2.0)));
        stack.push(new Signal(List.of(3.0, 4.0)));
        AddOp add = new AddOp();
        // Ahora llamamos a apply(stack, mode)
        Signal result = (Signal) add.apply(stack, LengthMode.REQUIRE_EQUAL);
        assertEquals(List.of(4.0, 6.0), result.getValues());
    }

    @Test
    void testAddUnequalLengthThrows() {
        RPNStack stack = new RPNStack();
        stack.push(new Signal(List.of(1.0, 2.0, 3.0)));
        stack.push(new Signal(List.of(4.0, 5.0)));
        AddOp add = new AddOp();
        assertThrows(IllegalArgumentException.class, () -> 
            add.apply(stack, LengthMode.REQUIRE_EQUAL));
    }

    // Opcional: testea la excepción de apply(args)
    @Test
    void testApplyListArgsThrows() {
        AddOp add = new AddOp();
        assertThrows(UnsupportedOperationException.class, () -> 
            add.apply(List.of(new Signal(List.of(1.0)), new Signal(List.of(2.0)))));
    }
    

	/*
    private final AddOp op = new AddOp();

    @Test
    void testAddEqualLength_() {
        Signal a = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        Signal b = new Signal(Arrays.asList(4.0, 5.0, 6.0));
        Object result = op.apply(List.of(a, b));
        assertTrue(result instanceof Signal);
        List<Double> vals = ((Signal) result).getValues();
        assertEquals(Arrays.asList(5.0, 7.0, 9.0), vals);
    }

    @Test
    void testAddUnequalLengthThrows_() {
        Signal a = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        Signal b = new Signal(Arrays.asList(4.0, 5.0));
        assertThrows(IllegalArgumentException.class, () ->
            op.apply(List.of(a, b))
        );
    }
    */
    
}
