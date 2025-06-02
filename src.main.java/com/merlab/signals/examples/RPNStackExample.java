package com.merlab.signals.examples;

import com.merlab.signals.rpn.RPNStack;

public class RPNStackExample {
    public static void main(String[] args) {
        RPNStack stack = new RPNStack();
        stack.push(10);
        stack.push(20);
        System.out.println("Top: " + stack.peek());
        System.out.println("Second: " + stack.peekSecond());
        System.out.println("Pop: " + stack.pop());
        System.out.println("Now Top: " + stack.peek());
    }
}
