package com.merlab.signals.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalManager;
import com.merlab.signals.core.SignalStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SignalManagerRPNChainTest {

    private SignalManager mgr;
    private Signal A, B, C;
    private Map<String, Signal> vars;

    @BeforeEach
    void setUp() {
        mgr = new SignalManager(
            null,
            new SignalStack(),
            null,
            false, false, false
        );
        A = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        B = new Signal(Arrays.asList(4.0, 3.0, 2.0, 1.0));
        C = new Signal(Arrays.asList(1.0, 1.0, 1.0, 1.0));
        vars = Map.of("A", A, "B", B, "C", C);
    }

    @Test
    void testChain1_addThenSubtract() {
        // (A + B) - C = [5,5,5,5] - [1,1,1,1] = [4,4,4,4]
        Signal result = mgr.operateWithRPN(
            List.of("A", "B", "+", "C", "-"),
            vars
        );
        assertEquals(List.of(4.0, 4.0, 4.0, 4.0), result.getValues());
    }

    @Test
    void testChain2_addThenMultiply() {
        // (A + B) * C = [5,5,5,5] * [1,1,1,1] = [5,5,5,5]
        Signal result = mgr.operateWithRPN(
            List.of("A", "B", "+", "C", "*"),
            vars
        );
        assertEquals(List.of(5.0, 5.0, 5.0, 5.0), result.getValues());
    }

    @Test
    void testChain3_scaleOnly() {
        // A * 2 = [2,4,6,8]
        Signal result = mgr.operateWithRPN(
            List.of("A", "2", "*"),
            vars
        );
        assertEquals(List.of(2.0, 4.0, 6.0, 8.0), result.getValues());
    }

    @Test
    void testChain4_scaleThenAdd() {
        // (A * 2) + B = [2,4,6,8] + [4,3,2,1] = [6,7,8,9]
        Signal result = mgr.operateWithRPN(
            List.of("A", "2", "*", "B", "+"),
            vars
        );
        assertEquals(List.of(6.0, 7.0, 8.0, 9.0), result.getValues());
    }

    @Test
    void testChain5_addThenNormalize() {
        // normalize(A + B) = normalize([5,5,5,5]) = [0,0,0,0]
        Signal result = mgr.operateWithRPN(
            List.of("A", "B", "+", "norm"),
            vars
        );
        assertEquals(List.of(0.0, 0.0, 0.0, 0.0), result.getValues());
    }

    @Test
    void testChain6_blend() {
        // blend α=0.25: 0.25*A + 0.75*B 
        // = [3.25,2.75,2.25,1.75]
        Signal result = mgr.operateWithRPN(
            List.of("A", "B", "0.25", "blend"),
            vars
        );
        assertEquals(
            List.of(3.25, 2.75, 2.25, 1.75),
            result.getValues(),
            "BlendOp did not produce expected weighted sum"
        );
    }

    @Test
    void testChain7_subtractThenAdd() {
        // (A - B) + C = [-3,-1,1,3] + [1,1,1,1] = [-2,0,2,4]
        Signal result = mgr.operateWithRPN(
            List.of("A", "B", "-", "C", "+"),
            vars
        );
        assertEquals(List.of(-2.0, 0.0, 2.0, 4.0), result.getValues());
    }

    @Test
    void testChain8_movavgThenScale() {
        // movavg window=2: [1,1.5,2.5,3.5] *2 = [2,3,5,7]
        Signal result = mgr.operateWithRPN(
            List.of("A", "2", "movavg", "2", "*"),
            vars
        );
        assertEquals(List.of(2.0, 3.0, 5.0, 7.0), result.getValues());
    }

    @Test
    void testChain9_movavgThenAdd() {
        // movavg window=2: [1,1.5,2.5,3.5] + B = [5,4.5,4.5,4.5]
        Signal result = mgr.operateWithRPN(
            List.of("A", "2", "movavg", "B", "+"),
            vars
        );
        assertEquals(List.of(5.0, 4.5, 4.5, 4.5), result.getValues());
    }

    @Test
    void testChain10_clampAndNormalize() {
        // clamp A to [2,3]: [1,2,3,4]→[2,2,3,3], then norm → [0,0,1,1]
        vars = Map.of("A", A);  // only A needed
        Signal result = mgr.operateWithRPN(
            List.of("A", "2", "3", "clamp", "norm"),
            vars
        );
        assertEquals(List.of(0.0, 0.0, 1.0, 1.0), result.getValues());
    }
}
