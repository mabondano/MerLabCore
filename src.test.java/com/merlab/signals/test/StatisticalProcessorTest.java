package com.merlab.signals.test;

import org.junit.jupiter.api.Test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.StatisticalProcessor;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.Arrays;
import java.util.List;

class StatisticalProcessorTest {

    @Test
    void testMeanAndVariance() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        double mean = StatisticalProcessor.mean(s);
        double var  = StatisticalProcessor.variance(s);

        assertEquals(2.5, mean, 1e-9, "La media debería ser 2.5");
        assertEquals(1.25, var,  1e-9, "La varianza debería ser 1.25");
    }   

    @Test
    void testMedianEven() {
        Signal s = new Signal(Arrays.asList(1.0, 3.0, 2.0, 4.0));
        double med = StatisticalProcessor.median(s);
        assertEquals(2.5, med, 1e-9, "Mediana de lista par debería ser promedio de dos centrales");
    }

    @Test
    void testMedianOdd() {
        Signal s = new Signal(Arrays.asList(5.0, 1.0, 3.0));
        double med = StatisticalProcessor.median(s);
        assertEquals(3.0, med, 1e-9, "Mediana de lista impar debería ser el elemento central");
    }

    @Test
    void testPercentile() {
        Signal s = new Signal(Arrays.asList(10.0, 20.0, 30.0, 40.0));
        // 25% está entre 10 y 20 => 15
        assertEquals(15.0, StatisticalProcessor.percentile(s, 25.0), 1e-9);
        // 75% está entre 30 y 40 => 35
        assertEquals(35.0, StatisticalProcessor.percentile(s, 75.0), 1e-9);
    }

    @Test
    void testMovingAverage() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        Signal ma3 = StatisticalProcessor.movingAverage(s, 3);
        List<Double> expected = Arrays.asList(2.0, 3.0, 4.0);
        assertEquals(expected, ma3.getValues(), "Media móvil ventana 3");
    }

    @Test
    void testStdDev() {
        Signal s = new Signal(Arrays.asList(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0));
        // Varianza poblacional = 4, stdDev = 2
        assertEquals(2.0, StatisticalProcessor.stdDev(s), 1e-9, "Desviación estándar");
    }

    @Test
    void testMinMaxRange() {
        Signal s = new Signal(Arrays.asList(3.0, 1.0, 4.0, 2.0));
        assertEquals(1.0, StatisticalProcessor.min(s), "Mínimo");
        assertEquals(4.0, StatisticalProcessor.max(s), "Máximo");
        assertEquals(3.0, StatisticalProcessor.range(s), "Rango");
    }    
    
    @Test
    void testCountAndSum() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        assertEquals(4, StatisticalProcessor.count(s), "Count");
        assertEquals(10.0, StatisticalProcessor.sum(s), 1e-9, "Sum");
    }

    @Test
    void testSumOfSquaresAndRms() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        // Σ x² = 1 + 4 + 9 = 14
        assertEquals(14.0, StatisticalProcessor.sumOfSquares(s), 1e-9, "Sum of squares");
        // RMS = sqrt(14/3)
        assertEquals(Math.sqrt(14.0/3.0), StatisticalProcessor.rms(s), 1e-9, "RMS");
    }

    @Test
    void testSkewnessAndKurtosis() {
        // Señal con sesgo y curtosis conocidos, p.ej. [0, 1, 0, 0, 0]
        Signal s = new Signal(Arrays.asList(0.0, 1.0, 0.0, 0.0, 0.0));
        double skew = StatisticalProcessor.skewness(s);
        double kurt = StatisticalProcessor.kurtosis(s);
        // Calcular manualmente:
        // media=0.2, std=~0.4, skewness=(1-0.2)^3/5 / 0.4^3 ≈ ? skewness = (1/5)·[4·(–0.5)³ + 2³] = 1.5 kurtosis = (1/5)·[4·(0.5)⁴ + 2⁴] = 3.25
        assertEquals(1.5,  StatisticalProcessor.skewness(s), 1e-9, "Skewness");
        assertEquals(3.25, StatisticalProcessor.kurtosis(s), 1e-9, "Kurtosis");
    }
    

    @Test
    void testMode() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 2.0, 3.0, 2.0));
        assertEquals(2.0, StatisticalProcessor.mode(s), 1e-9, "Mode debería ser 2.0");
    }

    @Test
    void testIqr() {
        Signal s = new Signal(Arrays.asList(4.0, 1.0, 3.0, 2.0));
        // Lista ordenada: [1,2,3,4]; Q1=1.5, Q3=3.5 => IQR = 2.0
        assertEquals(2.0, StatisticalProcessor.iqr(s), 1e-9, "IQR debería ser 2.0");
    }

    @Test
    void testCoefficientOfVariation() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        double mean = StatisticalProcessor.mean(s);        // =2.0
        double std  = StatisticalProcessor.stdDev(s);      // = sqrt(2/3)
        double expected = std / mean;
        assertEquals(expected, StatisticalProcessor.coefficientOfVariation(s), 1e-9,
                     "CV = stdDev / mean");
    }

    @Test
    void testStandardError() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        double sem = StatisticalProcessor.stdDev(s) / Math.sqrt(s.size());
        assertEquals(sem, StatisticalProcessor.standardError(s), 1e-9, "SEM = σ / √n");
    }

    @Test
    void testMeanAbsoluteDeviation() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        // mean = 2.0, MAD = (1+0+1)/3 = 0.666...
        assertEquals(2.0/3.0, StatisticalProcessor.meanAbsoluteDeviation(s), 1e-9,
                     "MAD = media(|x–μ|)");
    }

    @Test
    void testMedianAbsoluteDeviation() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        // median=3, absDevs=[2,1,0,1,2], mediana => 1.0
        assertEquals(1.0, StatisticalProcessor.medianAbsoluteDeviation(s), 1e-9,
                     "Median Absolute Deviation");
    }

    @Test
    void testEnergy() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        // energy alias de sumOfSquares: 1+4+9 = 14
        assertEquals(14.0, StatisticalProcessor.energy(s), 1e-9, "Energy = Σ x²");
    }

    @Test
    void testEntropy() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        // distribución uniforme p=0.25 => H = -4*0.25*ln(0.25) = -ln(0.25)
        double expected = - Math.log(0.25);
        assertEquals(expected, StatisticalProcessor.entropy(s), 1e-9,
                     "Entropy uniforme = -ln(1/4)");
    }    
    
    @Test
    void testThirdAndFourthCentralMoment() {
        // Señal de ejemplo: [1,2,3,4], media = 2.5
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));

        // Tercer momento central:
        // Σ(x-μ)³ = (-1.5)³ + (-0.5)³ + 0.5³ + 1.5³ = -3.375 -0.125 +0.125 +3.375 = 0
        double third = StatisticalProcessor.thirdCentralMoment(s);
        assertEquals(0.0, third, 1e-9, "Tercer momento central debe ser 0");

        // Cuarto momento central:
        // Σ(x-μ)⁴ = 1.5⁴ + 0.5⁴ + 0.5⁴ + 1.5⁴ = 5.0625 + 0.0625 + 0.0625 + 5.0625 = 10.25
        // dividido por n=4 ⇒ 10.25/4 = 2.5625
        double fourth = StatisticalProcessor.fourthCentralMoment(s);
        assertEquals(2.5625, fourth, 1e-9, "Cuarto momento central debe ser 2.5625");
    } 
    
    @Test
    void testPeakToPeak() {
        Signal s = new Signal(Arrays.asList(1.0, 3.0, 2.0));
        assertEquals(2.0, StatisticalProcessor.peakToPeak(s), 1e-9, "Peak-to-Peak = max–min");
    }

    @Test
    void testCrestFactor() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        double peak = 3.0;
        double rms  = Math.sqrt((1 + 4 + 9) / 3.0);
        assertEquals(peak / rms, StatisticalProcessor.crestFactor(s), 1e-9, "Crest Factor = peak/RMS");
    }

    @Test
    void testImpulseFactor() {
        Signal s = new Signal(Arrays.asList(1.0, -2.0, 3.0));
        double peak = 3.0;
        double meanAbs = (1 + 2 + 3) / 3.0;
        assertEquals(peak / meanAbs, StatisticalProcessor.impulseFactor(s), 1e-9, 
                     "Impulse Factor = peak/mean(|x|)");
    }

    @Test
    void testShapeFactor() {
        Signal s = new Signal(Arrays.asList(1.0, -2.0, 3.0));
        double rms     = Math.sqrt((1 + 4 + 9) / 3.0);
        double meanAbs = (1 + 2 + 3) / 3.0;
        assertEquals(rms / meanAbs, StatisticalProcessor.shapeFactor(s), 1e-9, 
                     "Shape Factor = RMS/mean(|x|)");
    }

    @Test
    void testClearanceFactor() {
        Signal s = new Signal(Arrays.asList(0.0, 4.0));
        double peak     = 4.0;
        double meanSqrt = (Math.sqrt(0) + Math.sqrt(4)) / 2.0; // =1.0
        assertEquals(peak / meanSqrt, StatisticalProcessor.clearanceFactor(s), 1e-9, 
                     "Clearance Factor = peak/mean(√|x|)");
    }

    @Test
    void testMarginFactor() {
        Signal s = new Signal(Arrays.asList(0.0, 16.0));
        double peak        = 16.0;
        double meanFourth = (Math.pow(0, 0.25) + Math.pow(16, 0.25)) / 2.0; // =1.0
        assertEquals(peak / meanFourth, StatisticalProcessor.marginFactor(s), 1e-9, 
                     "Margin Factor = peak/mean(|x|^(1/4))");
    }

    @Test
    void testZeroCrossingRate() {
        Signal s = new Signal(Arrays.asList(-1.0, 1.0, -1.0, 1.0));
        // Cruces en cada transición: 3 crossings over (n-1)=3 => rate=1.0
        assertEquals(1.0, StatisticalProcessor.zeroCrossingRate(s), 1e-9, 
                     "Zero-Crossing Rate completo");
    }

    @Test
    void testAutocorrelationAndAutocorrelations() {
        // Señal [1,2,3,4], media=2.5, varianzaden=5.0
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        // r[0] = 1.0
        assertEquals(1.0, StatisticalProcessor.autocorrelation(s, 0), 1e-9);
        // r[1] = 0.25
        assertEquals(0.25, StatisticalProcessor.autocorrelation(s, 1), 1e-9);
        // r[2] = -0.30
        assertEquals(-0.30, StatisticalProcessor.autocorrelation(s, 2), 1e-9);
        // Array r[0..3]
        double[] ac = StatisticalProcessor.autocorrelations(s, 3);
        assertArrayEquals(new double[]{1.0, 0.25, -0.30, -0.45}, ac, 1e-9);
    }

    @Test
    void testPartialAutocorrelation() {
        // Para p=1, PACF φ11 = r1/r0 = 0.25/1 = 0.25
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        assertEquals(0.25, StatisticalProcessor.partialAutocorrelation(s, 1), 1e-9);
    }

    @Test
    void testCrossCorrelation() {
        // Dos señales idénticas [1,2,3], mean=2.0
        Signal x = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        Signal y = new Signal(Arrays.asList(1.0, 2.0, 3.0));
        // cross-corr lag=0 => 1.0
        assertEquals(1.0, StatisticalProcessor.crossCorrelation(x, y, 0), 1e-9);
        // lag=1 => numerador es cero
        assertEquals(0.0, StatisticalProcessor.crossCorrelation(x, y, 1), 1e-9);
    }
    
/*
    @Test
    void testExponentialMovingAverage() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        // α = 0.5 → [1.0,
        //            0.5*2 + 0.5*1   = 1.5,
        //            0.5*3 + 0.5*1.5 = 2.25,
        //            0.5*4 + 0.5*2.25 = 3.125]
        Signal ema = StatisticalProcessor.exponentialMovingAverage(s, 0.5);
        List<Double> expectedEma = Arrays.asList(1.0, 1.5, 2.25, 3.125);
        assertIterableEquals(expectedEma, ema.getValues(), 1e-9,
            "EMA α=0.5 debería producir [1.0, 1.5, 2.25, 3.125]");
    }

    @Test
    void testMovingStdDev() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        // Ventana = 2 → ventanas: [1,2], [2,3], [3,4]
        // cada stdDev = sqrt(((x-mean)^2+(y-mean)^2)/2) = sqrt((0.25+0.25)/2)=0.5
        Signal msd = StatisticalProcessor.movingStdDev(s, 2);
        List<Double> expectedMsd = Arrays.asList(0.5, 0.5, 0.5);
        assertIterableEquals(expectedMsd, msd.getValues(), 1e-9,
            "MovingStdDev ventana 2 debería producir [0.5, 0.5, 0.5]");
    }   
*/    
    @Test
    void testExponentialMovingAverage() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        Signal ema = StatisticalProcessor.exponentialMovingAverage(s, 0.5);
        List<Double> expectedEma = Arrays.asList(1.0, 1.5, 2.25, 3.125);
        // Sin delta: usa la versión de assertIterableEquals con mensaje
        assertIterableEquals(
            expectedEma,
            ema.getValues(),
            "EMA α=0.5 debería producir [1.0, 1.5, 2.25, 3.125]"
        );
    }

    @Test
    void testMovingStdDev() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        Signal msd = StatisticalProcessor.movingStdDev(s, 2);
        List<Double> expectedMsd = Arrays.asList(0.5, 0.5, 0.5);
        assertIterableEquals(
            expectedMsd,
            msd.getValues(),
            "MovingStdDev ventana 2 debería producir [0.5, 0.5, 0.5]"
        );
    }
    
    @Test
    void testExponentialMovingAverage2() {
        Signal s = new Signal(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        Signal ema = StatisticalProcessor.exponentialMovingAverage(s, 0.5);
        double[] expected = {1.0, 1.5, 2.25, 3.125};
        double[] actual   = ema.getValues().stream().mapToDouble(d -> d).toArray();
        assertArrayEquals(expected, actual, 1e-9, "EMA α=0.5");
    }    
    

    @Test
    void testMovingMedian() {
        // Ventana 3 sobre [5,3,1,4,2] → medias [3,3,2]
        Signal s = new Signal(Arrays.asList(5.0, 3.0, 1.0, 4.0, 2.0));
        Signal result = StatisticalProcessor.movingMedian(s, 3);
        List<Double> expected = Arrays.asList(3.0, 3.0, 2.0);
        assertEquals(expected, result.getValues(), "Mediana móvil ventana 3");
    }


	@Test
	void testMovingMedian2() {
	    Signal s = new Signal(Arrays.asList(5.0, 3.0, 1.0, 4.0, 2.0));
	    Signal result = StatisticalProcessor.movingMedian(s, 3);
	    List<Double> expected = Arrays.asList(3.0, 3.0, 2.0);
	    assertIterableEquals(expected, result.getValues(),
	        "Mediana móvil ventana 3");
	}
	
	@Test
	void testWeightedMovingAverage() {
	    Signal s = new Signal(Arrays.asList(1.0,2.0,3.0,4.0,5.0));
	    double[] weights = {1,2,3};
	    Signal wma = StatisticalProcessor.weightedMovingAverage(s, weights);
	    List<Double> expected = Arrays.asList(14.0/6, 20.0/6, 26.0/6);
	    assertIterableEquals(expected, wma.getValues(),
	        "WMA pesos [1,2,3]");
	}
	
	@Test
	void testTriangularMovingAverage() {
	    Signal s = new Signal(Arrays.asList(1.0,2.0,3.0,4.0));
	    Signal tma = StatisticalProcessor.triangularMovingAverage(s, 3);
	    List<Double> expected = Arrays.asList(2.0, 3.0);
	    assertIterableEquals(expected, tma.getValues(),
	        "TMA ventana 3");
	}
	
	@Test
	void testGaussianSmoothingConstant() {
	    List<Double> data = Arrays.asList(5.0,5.0,5.0,5.0,5.0);
	    Signal s = new Signal(data);
	    Signal g  = StatisticalProcessor.gaussianSmoothing(s, 1.0);
	    assertIterableEquals(data, g.getValues(),
	        "Suavizado gaussiano de señal constante");
	}
	
	@Test
	void testSavitzkyGolay5Constant() {
	    List<Double> data = Arrays.asList(1.0,1.0,1.0,1.0,1.0,1.0,1.0);
	    Signal s = new Signal(data);
	    Signal sg = StatisticalProcessor.savitzkyGolay5(s);
	    List<Double> expected = Arrays.asList(1.0,1.0,1.0);
	    assertIterableEquals(expected, sg.getValues(),
	        "Savitzky–Golay de señal constante");
	}
	
	@Test
	void testWeightedMovingAverageWithDelta() {
	    Signal s = new Signal(Arrays.asList(1.0,2.0,3.0,4.0,5.0));
	    double[] weights = {1,2,3};
	    Signal wma = StatisticalProcessor.weightedMovingAverage(s, weights);
	    double[] expected = {14.0/6, 20.0/6, 26.0/6};
	    double[] actual   = wma.getValues().stream().mapToDouble(d -> d).toArray();
	    assertArrayEquals(expected, actual, 1e-9,
	        "WMA pesos [1,2,3] con tolerancia");
	}

}
