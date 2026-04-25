package com.merlab.signals.rpn.test;

import com.merlab.signals.rpn.RPNStack;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class RPNStackTest {

    @Test
    void testPushPopPeek() {
        RPNStack stack = new RPNStack();
        stack.push("A");
        stack.push("B");
        assertEquals("B", stack.peek());
        assertEquals("B", stack.pop());
        assertEquals("A", stack.peek());
    }

    @Test
    void testPeekSecond() {
        RPNStack stack = new RPNStack();
        stack.push("first");
        stack.push("second");
        assertEquals("first", stack.peekSecond());
        // ensure top remains unchanged
        assertEquals("second", stack.peek());
    }

    @Test
    void testPopUnderflowThrows() {
        RPNStack stack = new RPNStack();
        assertThrows(NoSuchElementException.class, stack::pop);
    }

    @Test
    void testPeekSecondUnderflowThrows() {
        RPNStack stack = new RPNStack();
        stack.push("only");
        assertThrows(IllegalStateException.class, stack::peekSecond);
    }
}
