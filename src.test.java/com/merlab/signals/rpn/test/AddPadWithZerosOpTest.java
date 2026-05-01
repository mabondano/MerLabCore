package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.AddPadWithZerosOp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddPadWithZerosOpTest {

    @Test
    void testAddsSignalsPaddingSecondWithZeros() {
        AddPadWithZerosOp op = new AddPadWithZerosOp();

        Signal a = new Signal(List.of(1.0, 2.0, 3.0));
        Signal b = new Signal(List.of(10.0, 20.0));

        Signal result = (Signal) op.apply(List.of(a, b));

        assertEquals(List.of(11.0, 22.0, 3.0), result.getValues());
    }

    @Test
    void testAddsSignalsPaddingFirstWithZeros() {
        AddPadWithZerosOp op = new AddPadWithZerosOp();

        Signal a = new Signal(List.of(1.0));
        Signal b = new Signal(List.of(10.0, 20.0, 30.0));

        Signal result = (Signal) op.apply(List.of(a, b));

        assertEquals(List.of(11.0, 20.0, 30.0), result.getValues());
    }
}