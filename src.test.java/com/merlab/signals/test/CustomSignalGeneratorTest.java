package com.merlab.signals.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.DoubleStream;

import org.junit.jupiter.api.Test;

import com.merlab.signals.core.CustomSignalGenerator;


public class CustomSignalGeneratorTest {

    private static final double TOL = 1e-6;

    @Test
    void testUniformReproducibility() {
        long seed = 12345L;
        CustomSignalGenerator gen1 = new CustomSignalGenerator(
            CustomSignalGenerator.DistType.UNIFORM, 10, 1.0,1.0, 0.0, 0.0, 5.0, seed
        );
        CustomSignalGenerator gen2 = new CustomSignalGenerator(
            CustomSignalGenerator.DistType.UNIFORM, 10,1.0,1.0, 0.0, 0.0, 5.0, seed
        );
        List<Double> s1 = gen1.getSignal().getValues();
        List<Double> s2 = gen2.getSignal().getValues();
        assertEquals(s1, s2,
            "Con la misma semilla, las señales uniformes deben coincidir");
    }

    @Test
    void testUniformRange() {
        double min = -1.0, max = 2.5;
        CustomSignalGenerator gen = new CustomSignalGenerator(
            CustomSignalGenerator.DistType.UNIFORM, 1000,1.0,1.0, 0.0, min, max, 42L
        );
        List<Double> s = gen.getSignal().getValues();
        assertEquals(1000, s.size(),
            "La señal uniforme debe tener la longitud indicada");
        for (double v : s) {
            assertTrue(v >= min && v <= max,
                () -> String.format("Valor %f fuera de [%f,%f]", v, min, max));
        }
    }

    @Test
    void testNormalReproducibility() {
        long seed = 999L;
        CustomSignalGenerator gen1 = new CustomSignalGenerator(
            CustomSignalGenerator.DistType.NORMAL, 20,1.0,1.0, 0.0, 1.0, 2.0, seed
        );
        CustomSignalGenerator gen2 = new CustomSignalGenerator(
            CustomSignalGenerator.DistType.NORMAL, 20,1.0,1.0, 0.0, 1.0, 2.0, seed
        );
        List<Double> s1 = gen1.getSignal().getValues();
        List<Double> s2 = gen2.getSignal().getValues();
        assertEquals(s1, s2,
            "Con la misma semilla, las señales normales deben coincidir");
    }

    @Test
    void testNormalStats() {
        double meanParam = 5.0, stdParam = 3.0;
        CustomSignalGenerator gen = new CustomSignalGenerator(
            CustomSignalGenerator.DistType.NORMAL, 10000,1.0,1.0, 0.0, meanParam, stdParam, 314L
        );
        List<Double> s = gen.getSignal().getValues();
        double[] arr = s.stream().mapToDouble(Double::doubleValue).toArray();
        double mean = DoubleStream.of(arr).average().orElse(Double.NaN);
        double var  = DoubleStream.of(arr)
                          .map(x -> (x - mean)*(x - mean))
                          .average().orElse(0.0);
        double std  = Math.sqrt(var);

        assertEquals(meanParam, mean, 0.01 * stdParam,
            "La media muestral debe aproximarse a la media paramétrica");
        assertEquals(stdParam, std, 0.05 * stdParam,
            "La desviación estándar muestral debe aproximarse a la paramétrica");
    }
}  
