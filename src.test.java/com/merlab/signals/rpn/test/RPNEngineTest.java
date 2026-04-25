package com.merlab.signals.rpn.test;

import com.merlab.signals.rpn.RPNEngine;
import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor.LengthMode;
import com.merlab.signals.rpn.AddOp;
import com.merlab.signals.rpn.RPNStack;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RPNEngineTest {

	@Test
	void testRegisterAndExecute() {
	    RPNEngine engine = new RPNEngine();
	    engine.register("+", new AddOp());
	    RPNStack stack = new RPNStack();
	    stack.push(new Signal(List.of(2.0, 3.0)));
	    stack.push(new Signal(List.of(5.0, 1.0)));
	    engine.execute("+", stack, LengthMode.REQUIRE_EQUAL); // <-- usa la versión con modo
	    Signal result = (Signal) stack.pop();
	    assertEquals(List.of(7.0, 4.0), result.getValues());
	}

    @Test
    void testUnknownTokenThrows() {
        RPNEngine engine = new RPNEngine();
        RPNStack stack = new RPNStack();
        assertThrows(IllegalArgumentException.class, () ->
            engine.execute("??", stack)
        );
    }

    @Test
    void testInsufficientArgsThrows() {
        RPNEngine engine = new RPNEngine();
        engine.register("+", new AddOp());
        RPNStack stack = new RPNStack();
        stack.push(new Signal(Arrays.asList(1.0, 2.0)));
        assertThrows(IllegalStateException.class, () ->
            engine.execute("+", stack)
        );
    }
    /*
        @Test
    void testRegisterAndExecute_() {
        RPNEngine engine = new RPNEngine();
        engine.register("+", new AddOp());
        RPNStack stack = new RPNStack();

        Signal a = new Signal(Arrays.asList(1.0, 2.0));
        Signal b = new Signal(Arrays.asList(3.0, 4.0));
        stack.push(a);
        stack.push(b);

        engine.execute("+", stack);
        @SuppressWarnings("unchecked")
        Signal sum = (Signal) stack.peek();
        assertEquals(Arrays.asList(4.0, 6.0), sum.getValues());
    }
     */
}
