package com.merlab.signals.rpn.test;

import com.merlab.signals.core.Complex;
import com.merlab.signals.core.Signal;
import com.merlab.signals.rpn.FFTOp;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FFTTest {

    private final FFTOp op = new FFTOp();
    private static final double TOL = 1e-9;

    @Test
    void testDeltaFFT() {
        // delta signal: [1,0,0,0] → FFT = all (1 + 0i)
        Signal s = new Signal(Arrays.asList(1.0, 0.0, 0.0, 0.0));

        @SuppressWarnings("unchecked")
        List<Complex> X = (List<Complex>) op.apply(List.of(s));

        assertEquals(4, X.size(),
            "FFT of length-4 signal must produce 4 bins");

        for (Complex c : X) {
            assertEquals(1.0, c.re, TOL, "Real part should be 1.0");
            assertEquals(0.0, c.im, TOL, "Imag part should be 0.0");
        }
    }
    
    @Test
    void testDeltaFFT2() {
        // delta signal: [1,0,0,0] → FFT = all (1 + 0i)
        Signal s = new Signal(Arrays.asList(1.0, 0.0, 0.0, 0.0));

        @SuppressWarnings("unchecked")
        List<Complex> X = (List<Complex>) op.apply(List.of(s));

        // Expect exactly 4 bins of (1 + 0i)
        assertEquals(4, X.size(),
            "FFT of length-4 signal must produce 4 output bins");

        Complex one = new Complex(1.0, 0.0);
        for (Complex c : X) {
            assertEquals(one, c,
                () -> "Each FFT bin should be 1+0i, but was " + c);
        }
    }

    @Test
    void testNonPowerOfTwoThrows() {
        // length=3 is not a power of two
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        assertThrows(IllegalArgumentException.class, () ->
            op.apply(List.of(s))
        );
    }

    @Test
    void testSmallFFT() {
        // FFT of [a,b] should be [a+b, a−b]
        Signal s = new Signal(Arrays.asList(2.0, 5.0));

        @SuppressWarnings("unchecked")
        List<Complex> X = (List<Complex>) op.apply(List.of(s));

        assertEquals(2, X.size(), "Length‐2 FFT must output 2 bins");

        Complex c0 = X.get(0);
        Complex c1 = X.get(1);

        assertEquals(7.0, c0.re, TOL, "Bin0 real = 2+5");
        assertEquals(0.0, c0.im, TOL, "Bin0 imag = 0");
        assertEquals(-3.0, c1.re, TOL, "Bin1 real = 2-5");
        assertEquals(0.0, c1.im, TOL, "Bin1 imag = 0");
    }  
    
    @Test
    void testSmallFFT2() {
        // FFT of [a,b] = [a+b, a−b]
        Signal s = new Signal(Arrays.asList(2.0, 5.0));
        @SuppressWarnings("unchecked")
        List<Complex> X = (List<Complex>) op.apply(List.of(s));

        assertEquals(2, X.size());
        assertEquals(new Complex(7.0, 0.0),    X.get(0));
        assertEquals(new Complex(-3.0, 0.0),   X.get(1));
    }
    

}
