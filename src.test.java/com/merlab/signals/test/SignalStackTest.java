package com.merlab.signals.test;

import org.junit.jupiter.api.Test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalStack;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SignalStackTest {

    @Test
    void testEmptyStack() {
        SignalStack stack = new SignalStack();
        // sin elementos, peek() debe devolver null
        assertNull(stack.peek(), "peek en stack vacío debe ser null");
        // pop() en vacío debe lanzar NoSuchElementException
        assertThrows(NoSuchElementException.class, stack::pop,
            "pop en stack vacío debe lanzar excepción");
        // getStack() inicial debe ser lista vacía e inmodificable
        List<Signal> list = stack.getStack();
        assertTrue(list.isEmpty(), "getStack en vacío debe estar vacío");
        assertThrows(UnsupportedOperationException.class, () -> list.add(null),
            "La lista devuelta por getStack debe ser inmodificable");
    }

    @Test
    void testPushPeekPop() {
        SignalStack stack = new SignalStack();
        Signal s1 = new Signal(Arrays.asList(1.0, 2.0));
        Signal s2 = new Signal(Arrays.asList(3.0));

        stack.push(s1);
        // peek no elimina, devuelve s1
        assertEquals(s1, stack.peek(), "peek tras push(s1) debe ser s1");

        stack.push(s2);
        // ahora peek devuelve s2
        assertEquals(s2, stack.peek(), "peek tras push(s2) debe ser s2");

        // pop devuelve s2 y lo elimina
        Signal popped = stack.pop();
        assertEquals(s2, popped, "pop debe devolver s2");
        // ahora peek vuelve a s1
        assertEquals(s1, stack.peek(), "peek tras pop debe ser s1");

        // pop de nuevo devuelve s1
        assertEquals(s1, stack.pop(), "pop debe devolver s1");
        // y deja el stack vacío
        assertNull(stack.peek(), "peek tras vaciar stack debe ser null");
    }

    @Test
    void testGetStackOrder() {
        SignalStack stack = new SignalStack();
        Signal a = new Signal(Arrays.asList(0.0));
        Signal b = new Signal(Arrays.asList(1.0));
        Signal c = new Signal(Arrays.asList(2.0));

        stack.push(a);
        stack.push(b);
        stack.push(c);
        // getStack devuelve snapshots en orden LIFO: [c, b, a]
        List<Signal> snapshot = stack.getStack();
        assertEquals(3, snapshot.size());
        assertEquals(c, snapshot.get(0));
        assertEquals(b, snapshot.get(1));
        assertEquals(a, snapshot.get(2));

        // operaciones posteriores no afectan al snapshot anterior
        stack.pop();
        assertEquals(3, snapshot.size(), "Snapshot no debe cambiar tras pop en el stack");
    }
}
