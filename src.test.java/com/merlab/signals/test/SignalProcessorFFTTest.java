package com.merlab.signals.test;

import com.merlab.signals.core.Complex;
import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalGenerator;
import com.merlab.signals.core.SignalProcessor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SignalProcessorFFTTest {

    // tolerancia relativa para el pico y absoluta para bins cero
    private static final double PEAK_TOL_FACTOR = 1e-6;
    private static final double ZERO_TOL         = 1e-6;

    private void checkFFT(int n) {
        // 1) Generar un ciclo de seno sin normalizar (±1)
        Signal sin = SignalGenerator.generateSineAll(n, 1.0, 1.0, 0.0, false);
        List<Double> data = sin.getValues();

        // 2) Calcular FFT
        List<Complex> spectrum = SignalProcessor.fft(data);

        // 3) Extraer magnitudes
        double[] mags = spectrum.stream()
                                .mapToDouble(Complex::abs)
                                .toArray();

        // 4) Pico esperado en bins 1 y n-1 => N/2
        double expectedPeak = n / 2.0;
        double tol = expectedPeak * PEAK_TOL_FACTOR;
        assertEquals(expectedPeak, mags[1], tol,
            "FFT pico en bin 1 para n=" + n);
        assertEquals(expectedPeak, mags[n-1], tol,
            "FFT pico en bin n-1 para n=" + n);

        // 5) Resto de bins cerca de cero
        for (int k = 0; k < mags.length; k++) {
            if (k == 1 || k == n-1) continue;
            assertTrue(mags[k] < ZERO_TOL,
                String.format("FFT mag[%d]=%f no es ~0 para n=%d", k, mags[k], n));
        }
    }

    @Test
    void testFFTSize32() {
        checkFFT(32);
    }

    @Test
    void testFFTSize64() {
        checkFFT(64);
    }

    @Test
    void testFFTSize128() {
        checkFFT(128);
    }
    
    @Test
    void testFFTSize256() {
        checkFFT(256);
    }
    
    @Test
    void testFFTSize512() {
        checkFFT(512);
    }
    
    @Test
    void testFFTSize1024() {
        checkFFT(1024);
    }    
}
