package com.merlab.signals.test;

import org.junit.jupiter.api.Test;

import com.merlab.signals.core.DistributionGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DistributionGeneratorTest {

    private static final double TOL = 1e-6;
    
    @Test
    void testUniform() {
        long seed = 123L;
        double min = -5.0, max = 5.0;
        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.UNIFORM, 100, min, max, seed
        );
        DistributionGenerator g2 = new DistributionGenerator(
            DistributionGenerator.DistType.UNIFORM, 100, min, max, seed
        );
        List<Double> s1 = g1.getSignal().getValues();
        List<Double> s2 = g2.getSignal().getValues();
        assertEquals(s1, s2, "Uniform reproducible");
        for (double v : s1) {
            assertTrue(v >= min && v <= max,
                () -> "Uniform fuera de rango: " + v);
        }
    }

    @Test
    void testTriangular() {
        long seed = 234L;
        double lower = 0.0, upper = 10.0;
        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.TRIANGULAR, 100, lower, upper, seed
        );
        DistributionGenerator g2 = new DistributionGenerator(
            DistributionGenerator.DistType.TRIANGULAR, 100, lower, upper, seed
        );
        List<Double> s1 = g1.getSignal().getValues();
        List<Double> s2 = g2.getSignal().getValues();
        assertEquals(s1, s2, "Triangular reproducible");
        for (double v : s1) {
            assertTrue(v >= lower && v <= upper,
                () -> "Triangular fuera de rango: " + v);
        }
    }
    
    @Test
    void testBinomial() {
        long seed = 345L;
        int trials = 20;
        double p = 0.3;
        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.BINOMIAL, 50, trials, p, seed
        );
        List<Double> s1 = g1.getSignal().getValues();

        for (double v : s1) {
            // Convertimos double a int
            int iv = (int) v;
            // Comprobamos que no hay parte fraccionaria
            assertEquals((double) iv, v, 1e-9, "Binomial no entero: " + v);
            // Comprobamos rango
            assertTrue(iv >= 0 && iv <= trials,
                () -> "Binomial fuera de rango: " + iv);
        }
    }

    @Test
    void testPoisson() {
        long seed = 456L;
        double mean = 4.5;
        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.POISSON, 50, mean, 0.0, seed
        );
        List<Double> s1 = g1.getSignal().getValues();

        for (double v : s1) {
            int iv = (int) v;
            // Comprueba que v es entero
            assertEquals((double) iv, v, 1e-9, 
                "Poisson no entero: " + v);
            // Comprueba que no es negativo
            assertTrue(iv >= 0, "Poisson negativo: " + iv);
        }
    }

    @Test
    void testExponential() {
        long seed = 567L;
        double mean = 2.0;
        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.EXPONENTIAL, 100, mean, 0.0, seed
        );
        DistributionGenerator g2 = new DistributionGenerator(
            DistributionGenerator.DistType.EXPONENTIAL, 100, mean, 0.0, seed
        );
        List<Double> s1 = g1.getSignal().getValues();
        List<Double> s2 = g2.getSignal().getValues();
        assertEquals(s1, s2, "Exponential reproducible");
        for (double v : s1) {
            assertTrue(v >= 0.0, "Exponencial negativa: " + v);
        }
    }

    @Test
    void testGeometric() {
        long seed = 678L;
        double p = 0.4;

        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.GEOMETRIC, 50, p, 0.0, seed
        );
        DistributionGenerator g2 = new DistributionGenerator(
            DistributionGenerator.DistType.GEOMETRIC, 50, p, 0.0, seed
        );

        List<Double> s1 = g1.getSignal().getValues();
        List<Double> s2 = g2.getSignal().getValues();
        assertEquals(s1, s2, "Geometric reproducible");

        for (double v : s1) {
            int iv = (int) v;
            // comprueba que no tiene parte fraccionaria
            assertEquals((double) iv, v, 1e-9, "Geométrica no entero: " + v);
            // y que cumple iv ≥ 1
            assertTrue(iv >= 1, "Geométrica <1: " + iv);
        }
        
        //Error:
        //int iv = v.intValue();
        //assertEquals(iv, v, "Geométrica no entero: " + v);
    }

    @Test
    void testWeibull() {
        long seed = 789L;
        double shape = 1.5, scale = 2.0;
        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.WEIBULL, 100, shape, scale, seed
        );
        DistributionGenerator g2 = new DistributionGenerator(
            DistributionGenerator.DistType.WEIBULL, 100, shape, scale, seed
        );
        List<Double> s1 = g1.getSignal().getValues();
        List<Double> s2 = g2.getSignal().getValues();
        assertEquals(s1, s2, "Weibull reproducible");
        for (double v : s1) {
            assertTrue(v >= 0.0, "Weibull negativa: " + v);
        }
    }

    @Test
    void testBeta() {
        long seed = 891L;
        double alpha = 2.0, beta = 5.0;
        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.BETA, 1000, alpha, beta, seed
        );
        DistributionGenerator g2 = new DistributionGenerator(
            DistributionGenerator.DistType.BETA, 1000, alpha, beta, seed
        );
        List<Double> s1 = g1.getSignal().getValues();
        List<Double> s2 = g2.getSignal().getValues();
        assertEquals(s1, s2, "Beta reproducible");
        for (double v : s1) {
            assertTrue(v >= 0.0 && v <= 1.0,
                "Beta fuera de [0,1]: " + v);
        }
    }

    @Test
    void testLognormal() {
        long seed = 912L;
        double scale = 0.0, shape = 1.0;
        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.LOGNORMAL, 1000, scale, shape, seed
        );
        DistributionGenerator g2 = new DistributionGenerator(
            DistributionGenerator.DistType.LOGNORMAL, 1000, scale, shape, seed
        );
        List<Double> s1 = g1.getSignal().getValues();
        List<Double> s2 = g2.getSignal().getValues();
        assertEquals(s1, s2, "Lognormal reproducible");
        for (double v : s1) {
            assertTrue(v > 0.0, "Lognormal no positiva: " + v);
        }
    }

    @Test
    void testNormal() {
        long seed = 993L;
        double mean = 0.0, std = 1.0;
        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.NORMAL, 1000, mean, std, seed
        );
        DistributionGenerator g2 = new DistributionGenerator(
            DistributionGenerator.DistType.NORMAL, 1000, mean, std, seed
        );
        assertEquals(g1.getSignal().getValues(),
                     g2.getSignal().getValues(),
                     "Normal reproducible");
    }

    @Test
    void testHypergeometric() {
        long seed = 104L;
        int population = 50, successes = 20, samplesize = 10;
        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.HYPERGEOMETRIC,
            samplesize,
            population,
            successes,
            seed
        );
        DistributionGenerator g2 = new DistributionGenerator(
            DistributionGenerator.DistType.HYPERGEOMETRIC,
            samplesize,
            population,
            successes,
            seed
        );
        List<Double> s1 = g1.getSignal().getValues();
        List<Double> s2 = g2.getSignal().getValues();
        assertEquals(s1, s2, "Hypergeometric reproducible");
        for (double v : s1) {
            int iv = (int) v;
            assertEquals((double)iv, v);
            assertTrue(iv >= 0 && iv <= Math.min(successes, samplesize),
                "Hypergeometric fuera de rango: " + iv);
        }
    }    

    @Test
    void testChiSquare() {
        long seed = 115L;
        double df = 3.0;
        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.CHI_SQUARE, 500, df, 0.0, seed
        );
        DistributionGenerator g2 = new DistributionGenerator(
            DistributionGenerator.DistType.CHI_SQUARE, 500, df, 0.0, seed
        );
        List<Double> s1 = g1.getSignal().getValues();
        List<Double> s2 = g2.getSignal().getValues();
        assertEquals(s1, s2, "Chi-Square reproducible");
        for (double v : s1) {
            assertTrue(v >= 0.0, "Chi-Square negativa: " + v);
        }
    }

    @Test
    void testStudentT() {
        long seed = 126L;
        double df = 5.0;
        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.STUDENT_T, 500, df, 0.0, seed
        );
        DistributionGenerator g2 = new DistributionGenerator(
            DistributionGenerator.DistType.STUDENT_T, 500, df, 0.0, seed
        );
        assertEquals(g1.getSignal().getValues(),
                     g2.getSignal().getValues(),
                     "Student-T reproducible");
    }

    @Test
    void testGamma() {
        long seed = 137L;
        double shape = 2.0, scale = 2.0;
        DistributionGenerator g1 = new DistributionGenerator(
            DistributionGenerator.DistType.GAMMA, 500, shape, scale, seed
        );
        DistributionGenerator g2 = new DistributionGenerator(
            DistributionGenerator.DistType.GAMMA, 500, shape, scale, seed
        );
        List<Double> s1 = g1.getSignal().getValues();
        List<Double> s2 = g2.getSignal().getValues();
        assertEquals(s1, s2, "Gamma reproducible");
        for (double v : s1) {
            assertTrue(v >= 0.0, "Gamma negativa: " + v);
        }
    }
    
    @Test
    void testHypergeometricReproducible() {
        long seed = 1234L;
        // P‐1=50, K‐2=20, n=10
        DistributionGenerator g1 = new DistributionGenerator(
        		DistributionGenerator.DistType.HYPERGEOMETRIC, 10, 50, 20, seed
        );
        List<Double> out1 = g1.getSignal().getValues();
        // Estos valores los obtuviste de antemano (primer run con seed=1234)
        List<Double> expected = List.of(8d, 5d, 4d, 3d, 6d, 2d, 3d, 7d, 1d, 5d);
        assertEquals(expected, out1, "Hypergeometric con seed fijo debe coincidir");
    }

 
}
