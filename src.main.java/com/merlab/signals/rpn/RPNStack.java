package com.merlab.signals.rpn;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A simple stack that can hold Signals, Doubles, Integers, etc.
 */
public class RPNStack {
    private final Deque<Object> stack = new ArrayDeque<>();

    /** Push any object (Signal or scalar) */
    public void push(Object obj) {
        stack.push(obj);
    }

    /** Pop the top object */
    public Object pop() {
        return stack.pop();
    }

    /** Peek at the top object without removing */
    public Object peek() {
        return stack.peek();
    }

    /** Peek at the second‐from‐top object without removing */
    public Object peekSecond() {
        if (stack.size() < 2) {
            throw new IllegalStateException("Need at least two elements to peekSecond()");
        }
        Object top = stack.pop();
        Object second = stack.peek();
        stack.push(top);
        return second;
    }

    /** Number of elements on the stack */
    public int size() {
        return stack.size();
    }
    
    /** Clear all elements from the stack */
    public void clear() {
        stack.clear();
    }
}
