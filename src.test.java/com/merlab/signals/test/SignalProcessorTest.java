package com.merlab.signals.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProcessor;

public class SignalProcessorTest {
	
	 @Test
	    void testScaleListMultiply() {
	        List<Double> orig = Arrays.asList(1.0, 2.0, 3.0);
	        List<Double> scaled = SignalProcessor.scale(orig, 3.0, false);
	        double[] expected = {3.0, 6.0, 9.0};
	        double[] actual   = scaled.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "Multiplicar lista ×3");
	    }

	    @Test
	    void testScaleListDivide() {
	        List<Double> orig = Arrays.asList(2.0, 4.0, 8.0);
	        List<Double> scaled = SignalProcessor.scale(orig, 2.0, true);
	        double[] expected = {1.0, 2.0, 4.0};
	        double[] actual   = scaled.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "Dividir lista ÷2");
	    }

	    @Test
	    void testScaleSignalMultiply() {
	        Signal sig = new Signal(Arrays.asList(1.5, 2.5, 3.5));
	        Signal out = SignalProcessor.scaleSignal(sig, 2.0, false);
	        double[] expected = {3.0, 5.0, 7.0};
	        double[] actual   = out.getValues().stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "Multiplicar Signal ×2");
	    }

	    @Test
	    void testScaleSignalDivide() {
	        Signal sig = new Signal(Arrays.asList(2.0, 5.0, 10.0));
	        Signal out = SignalProcessor.scaleSignal(sig, 2.0, true);
	        double[] expected = {1.0, 2.5, 5.0};
	        double[] actual   = out.getValues().stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "Dividir Signal ÷2");
	    }
	    
	    @Test
	    void testNormalizeTo() {
	        List<Double> in = Arrays.asList(2.0, 4.0, 6.0);
	        // min=2, max=6, scale=1 → [0, .5, 1]
	        List<Double> out = SignalProcessor.normalizeTo(in, 1.0);
	        double[] expected = {0.0, 0.5, 1.0};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "normalizeTo escala de 0 a 1");
	    }

	    @Test
	    void testDecimate() {
	        List<Double> in = Arrays.asList(10.0, 20.0, 30.0, 40.0, 50.0, 60.0);
	        // factor = 2 ⇒ tomamos índices 0,2,4
	        List<Double> out = SignalProcessor.decimate(in, 2);
	        double[] expected = {10.0, 30.0, 50.0};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "decimate cada 2 muestras");
	    }

	    @Test
	    void testDecimateByTwo() {
	        List<Double> in = Arrays.asList(1.0, 2.0, 3.0, 4.0);
	        // equivalente a decimate(in, 2)
	        List<Double> out = SignalProcessor.decimateByTwo(in);
	        double[] expected = {1.0, 3.0};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "decimateByTwo equivale a factor 2");
	    }

	    @Test
	    void testInterpolateFactor2() {
	        List<Double> in = Arrays.asList(1.0, 5.0);
	        // factor 2: interpola un punto en medio → [1,3,5]
	        List<Double> out = SignalProcessor.interpolate(in, 2);
	        double[] expected = {1.0, 3.0, 5.0};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "interpolate factor 2");
	    }

	    @Test
	    void testInterpolateFactor3() {
	        List<Double> in = Arrays.asList(1.0, 7.0);
	        // factor 3: interpola 2 puntos → [1, 3.0, 5.0, 7]
	        List<Double> out = SignalProcessor.interpolate(in, 3);
	        double[] expected = {1.0, 3.0, 5.0, 7.0};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "interpolate factor 3");
	    }

	    @Test
	    void testDerivative() {
	        List<Double> in = Arrays.asList(1.0, 4.0, 9.0);
	        // dt = 1 ⇒ derivadas: [4-1, 9-4] = [3,5]
	        List<Double> out = SignalProcessor.derivative(in);
	        double[] expected = {3.0, 5.0};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "derivative con dt=1");
	    }

	    @Test
	    void testIntegrate() {
	        List<Double> in = Arrays.asList(1.0, 2.0, 3.0);
	        // integración (sumas acumuladas): [1,1+2,1+2+3] = [1,3,6]
	        List<Double> out = SignalProcessor.integrate(in);
	        double[] expected = {1.0, 3.0, 6.0};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "integrate suma acumulada");
	    }	 
	    
	    @Test
	    void testDerivativeDt1() {
	        List<Double> in = Arrays.asList(1.0, 4.0, 9.0, 16.0);
	        // derivada normal dt=1: [4-1, 9-4, 16-9] = [3,5,7]
	        double[] expected = {3.0, 5.0, 7.0};
	        double[] actual   = SignalProcessor.derivative(in)
	                                          .stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "Derivative dt=1");
	    }

	    @Test
	    void testDerivativeDt2() {
	        List<Double> in = Arrays.asList(1.0, 4.0, 9.0, 16.0);
	        // derivada con dt=2: [(9-1)/2, (16-4)/2] = [4.0,6.0]
	        double[] expected = {4.0, 6.0};
	        double[] actual   = SignalProcessor.derivative(in, 2)
	                                          .stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "Derivative dt=2");
	    }

	    @Test
	    void testDerivativeInvalidDt() {
	        List<Double> in = Arrays.asList(1.0, 2.0, 3.0);
	        assertThrows(IllegalArgumentException.class, () -> 
	            SignalProcessor.derivative(in, 0),
	            "dt<1 debe lanzar IllegalArgumentException"
	        );	    
	    }	    

	    @Test
	    void testAddSignalsRequireEqualThrows() {
	        List<Double> a = Arrays.asList(1.0, 2.0, 3.0);
	        List<Double> b = Arrays.asList(4.0, 5.0);
	        assertThrows(IllegalArgumentException.class, () ->
	            SignalProcessor.addSignals(a, b, SignalProcessor.LengthMode.REQUIRE_EQUAL),
	            "Debe lanzar error si longitudes difieren y REQUIRE_EQUAL"
	        );
	    }

	    @Test
	    void testAddSignalsPadWithZeros() {
	        List<Double> a = Arrays.asList(1.0, 2.0, 3.0);
	        List<Double> b = Arrays.asList(4.0, 5.0);
	        List<Double> sum = SignalProcessor.addSignals(a, b, SignalProcessor.LengthMode.PAD_WITH_ZEROS);
	        double[] expected = {5.0, 7.0, 3.0};  // [1+4, 2+5, 3+0]
	        double[] actual   = sum.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "addSignals PAD_WITH_ZEROS");
	    }

	    @Test
	    void testSubtractSignalsPadWithZeros() {
	        List<Double> a = Arrays.asList(5.0, 6.0, 7.0);
	        List<Double> b = Arrays.asList(2.0, 3.0);
	        List<Double> diff = SignalProcessor.subtractSignals(a, b, SignalProcessor.LengthMode.PAD_WITH_ZEROS);
	        double[] expected = {3.0, 3.0, 7.0};  // [5–2,6–3,7–0]
	        double[] actual   = diff.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "subtractSignals PAD_WITH_ZEROS");
	    }

	    @Test
	    void testMultiplySignalsPadWithZeros() {
	        List<Double> a = Arrays.asList(2.0, 3.0, 4.0);
	        List<Double> b = Arrays.asList(5.0, 6.0);
	        List<Double> prod = SignalProcessor.multiplySignals(a, b, SignalProcessor.LengthMode.PAD_WITH_ZEROS);
	        double[] expected = {10.0, 18.0, 0.0};  // [2*5,3*6,4*0]
	        double[] actual   = prod.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "multiplySignals PAD_WITH_ZEROS");
	    }
	    
	    @Test
	    void testDivideSignalsEqual() {
	        List<Double> a = Arrays.asList(10.0, 20.0, 30.0);
	        List<Double> b = Arrays.asList(2.0, 4.0, 5.0);
	        List<Double> out = SignalProcessor.divideSignals(
	            a, b, SignalProcessor.LengthMode.REQUIRE_EQUAL
	        );
	        double[] expected = {5.0, 5.0, 6.0};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "divideSignals básico");
	    }

	    @Test
	    void testDivideSignalsRequireEqualThrows() {
	        List<Double> a = Arrays.asList(1.0, 2.0);
	        List<Double> b = Arrays.asList(1.0);
	        assertThrows(IllegalArgumentException.class, () ->
	            SignalProcessor.divideSignals(a, b, SignalProcessor.LengthMode.REQUIRE_EQUAL),
	            "Debe lanzar si las longitudes difieren y REQUIRE_EQUAL"
	        );
	    }	    

	    @Test
	    void testConvolveBasic() {
	        List<Double> a = Arrays.asList(1.0, 2.0, 3.0);
	        List<Double> b = Arrays.asList(4.0, 5.0);
	        // Convolución clásica: [1*4,
	        //                       1*5+2*4,
	        //                       2*5+3*4,
	        //                       3*5]
	        List<Double> conv = SignalProcessor.convolve(a, b, SignalProcessor.LengthMode.REQUIRE_EQUAL);
	        double[] expected = {4.0, 13.0, 22.0, 15.0};
	        double[] actual   = conv.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "convolve básica");
	    }
	    
	    @Test
	    void testLowPassFilterAlphaHalf() {
	        List<Double> input = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
	        List<Double> out   = SignalProcessor.lowPassFilter(input, 0.5);

	        double[] expected = {1.0, 1.5, 2.25, 3.125, 4.0625};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();

	        assertArrayEquals(expected, actual, 1e-9, "LowPass α=0.5");
	    }

	    @Test
	    void testLowPassFilterAlphaOne() {
	        List<Double> input = Arrays.asList(1.0, 2.0, 3.0);
	        List<Double> out   = SignalProcessor.lowPassFilter(input, 1.0);

	        // α=1 → salida = entrada
	        double[] expected = {1.0, 2.0, 3.0};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();

	        assertArrayEquals(expected, actual, 1e-9, "LowPass α=1.0 sin cambio");
	    }

	    @Test
	    void testLowPassFilterAlphaZero() {
	        List<Double> input = Arrays.asList(7.0, 8.0, 9.0);
	        List<Double> out   = SignalProcessor.lowPassFilter(input, 0.0);

	        // α=0 → salida constante igual al primer valor
	        double[] expected = {7.0, 7.0, 7.0};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();

	        assertArrayEquals(expected, actual, 1e-9, "LowPass α=0.0 constante");
	    }
	    
	    @Test
	    void testHighPassFilterAlphaZero() {
	        List<Double> in = Arrays.asList(1.0, 2.0, 3.0);
	        List<Double> out = SignalProcessor.highPassFilter(in, 0.0);
	        double[] expected = {1.0, 0.0, 0.0};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "HP α=0 → sólo primer valor");
	    }

	    @Test
	    void testHighPassFilterAlphaHalf() {
	        List<Double> in = Arrays.asList(1.0, 2.0, 3.0);
	        List<Double> out = SignalProcessor.highPassFilter(in, 0.5);
	        // α=0.5: [1.0, 0.5*(1+2–1)=1.0, 0.5*(1+3–2)=1.0]
	        double[] expected = {1.0, 1.0, 1.0};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "HP α=0.5 → valores constantes");
	    }

	    @Test
	    void testHighPassFilterAlphaOne() {
	        List<Double> in = Arrays.asList(1.0, 2.0, 3.0);
	        List<Double> out = SignalProcessor.highPassFilter(in, 1.0);
	        // α=1: y[n] = y[n-1] + x[n] - x[n-1] ⇒ reproduce la señal original
	        double[] expected = {1.0, 2.0, 3.0};
	        double[] actual   = out.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9, "HP α=1 → señal original");
	    }

	    @Test
	    void testHighPassFilterEmpty() {
	        List<Double> in = Arrays.asList();
	        List<Double> out = SignalProcessor.highPassFilter(in, 0.5);
	        assertTrue(out.isEmpty(), "HP sobre lista vacía debe devolver lista vacía");
	    }	
	
	    @Test
	    void testBandPassFilterTrivial() {
	        List<Double> data = Arrays.asList(1.0, 2.0, 3.0);
	        // αHP=1 y αLP=1 deben reproducir la señal original
	        double[] expected = {1.0, 2.0, 3.0};
	        double[] actual   = SignalProcessor.bandPassFilter(data, 1.0, 1.0)
	                                            .stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9,
	            "Band-pass identity para αHP=1, αLP=1");
	    }

	    @Test
	    void testBandPassFilterComposition() {
	        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0);
	        double alphaHP = 0.6;
	        double alphaLP = 0.4;
	        // Composición manual
	        List<Double> manual = SignalProcessor.lowPassFilter(
	                                  SignalProcessor.highPassFilter(data, alphaHP),
	                                  alphaLP);
	        List<Double> viaBP  = SignalProcessor.bandPassFilter(data, alphaHP, alphaLP);
	        double[] exp = manual.stream().mapToDouble(d -> d).toArray();
	        double[] act = viaBP.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(exp, act, 1e-9,
	            "Band-pass debe igualar lowPass(highPass(...))");
	    }
	    

	    @Test
	    void testConvolveReversedStride1() {
	        List<Double> sig    = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
	        List<Double> kernel = Arrays.asList(1.0, 0.5);
	        List<Double> out    = SignalProcessor.convolveReversedWithStride(sig, kernel, 1);
	        double[] expected   = {2.5, 4.0, 5.5, 7.0};  // <--- corregido
	        double[] actual     = out.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9,
	            "ConvolveReversed stride=1");
	    }

	    @Test
	    void testConvolveReversedStride2() {
	        List<Double> sig    = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
	        List<Double> kernel = Arrays.asList(1.0, 0.5);
	        List<Double> out    = SignalProcessor.convolveReversedWithStride(sig, kernel, 2);
	        double[] expected   = {2.5, 5.5};            // <--- corregido
	        double[] actual     = out.stream().mapToDouble(d -> d).toArray();
	        assertArrayEquals(expected, actual, 1e-9,
	            "ConvolveReversed stride=2");
	    }

	    @Test
	    void testConvolveReversedInvalidStride() {
	        List<Double> sig    = Arrays.asList(1.0, 2.0, 3.0);
	        List<Double> kernel = Arrays.asList(1.0);
	        assertThrows(IllegalArgumentException.class, () -> 
	            SignalProcessor.convolveReversedWithStride(sig, kernel, 0),
	            "Stride <1 debe lanzar IllegalArgumentException"
	        );
	    }

	    @Test
	    void testConvolveReversedShortSignalOrKernel() {
	        // señal más corta que kernel
	        List<Double> sig    = Arrays.asList(1.0, 2.0);
	        List<Double> kernel = Arrays.asList(1.0, 0.5, 0.25);
	        List<Double> out    = SignalProcessor.convolveReversedWithStride(sig, kernel, 1);
	        assertEquals(Collections.emptyList(), out, 
	            "Señal corta < kernel debe devolver lista vacía");
	    }	    

}
