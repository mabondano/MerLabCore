package com.merlab.signals.test;

import org.junit.jupiter.api.Test;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalGenerator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class SignalGeneratorTest {

    @Test
    void testGenerateSine() {
        int size = 100;
        double amp  = 2.0, freq = 1.0;
        Signal sin = SignalGenerator.generateSine(size, amp, freq);
        assertEquals(size, sin.size(), "Tamaño correcto");
        // Pico normalizado en 90°: amp*50 + 50 = 150
        assertEquals( amp*50 + 50, sin.getValues().get(size/4), 1e-9, "Pico de seno en 90°");
    }
    
    @Test
    void testGenerateSineWavePhaseZero() {
        int length = 100;
        double amp = 1.0;
        Signal wave = SignalGenerator.generateSineWave(length, amp, 0.0);

        // 1) En fase 0, sin(0) = 0
        assertEquals(0.0, wave.get(0), 1e-9, "Phase 0 en índice 0 debe ser 0");

        // 2) En fase 0, en 90° (length/4) sin(π/2) = 1
        assertEquals(1.0, wave.get(length/4), 1e-9, "Phase 0 en ¼ de longitud debe ser +1");
    }

    @Test
    void testGenerateCosineViaPhase() {
        int length = 4;
        double amp = 2.0;
        Signal wave = SignalGenerator.generateSineWave(length, amp, Math.PI / 2);

        // Al usar fase = π/2, sin(x + π/2) = cos(x):
        // i=0 ⇒ cos(0)=1 ⇒ 2.0
        assertEquals( amp, wave.get(0), 1e-9, "Coseno en índice 0 debe ser +amplitude");

        // i=1 ⇒ x=2π/4=π/2 ⇒ cos(π/2)=0
        assertEquals( 0.0, wave.get(1), 1e-9, "Coseno en índice 1 debe ser 0");

        // i=2 ⇒ x=π ⇒ cos(π)=–1 ⇒ –2.0
        assertEquals(-amp, wave.get(2), 1e-9, "Coseno en índice 2 debe ser –amplitude");

        // i=3 ⇒ x=3π/2 ⇒ cos(3π/2)=0
        assertEquals( 0.0, wave.get(3), 1e-9, "Coseno en índice 3 debe ser 0");
    }

    @Test
    void testGenerateSineWavePhasePi() {
        int length = 4;
        double amp = 3.0;
        Signal wave = SignalGenerator.generateSineWave(length, amp, Math.PI);

        // Fase π invierte la onda: sin(x+π) = –sin(x)
        // i=0 ⇒ –sin(0)=0
        assertEquals( 0.0,  wave.get(0), 1e-9);
        // i=1 ⇒ –sin(π/2)=–1 ⇒ –3.0
        assertEquals(-amp, wave.get(1), 1e-9);
        // i=2 ⇒ –sin(π)=–0 = 0
        assertEquals( 0.0,  wave.get(2), 1e-9);
        // i=3 ⇒ –sin(3π/2)=–(–1)=+1 ⇒ +3.0
        assertEquals( amp,  wave.get(3), 1e-9);
    }    
    
    /*@Test
    void testGenerateSineAllBasic() {
        // Un ciclo, fase 0, amplitud ±1, sin normalizar
        int size = 4;
        Signal sin = SignalGenerator.generateSineAll(size, 1.0, 1.0, 0.0, false);
        // Ángulos: 0, π/2, π, 3π/2 → sin: 0,1,0,-1
        List<Double> expected = List.of(0.0, 1.0, 0.0, -1.0);
        assertEquals(expected, sin.getValues(), "Seno básico sin normalizar");
    }

    @Test
    void testGenerateSineAllFrequency2() {
        // Dos ciclos en 4 muestras, fase 0, amplitud ±1
        int size = 4;
        Signal sin2 = SignalGenerator.generateSineAll(size, 1.0, 2.0, 0.0, false);
        // Ángulos: 0, π, 2π, 3π → sin: 0,0,0,0
        List<Double> expected = List.of(0.0, 0.0, 0.0, 0.0);
        assertEquals(expected, sin2.getValues(), "Dos ciclos en pocas muestras da ceros");
    }

    @Test
    void testGenerateSineAllPhaseShift() {
        // Un ciclo, fase π/2, amplitud ±2
        int size = 4;
        Signal cos = SignalGenerator.generateSineAll(size, 2.0, 1.0, Math.PI/2, false);
        // sin(x+π/2)=cos(x): cos(0)=1, cos(π/2)=0, cos(π)=-1, cos(3π/2)=0 → x2
        List<Double> expected = List.of(2.0, 0.0, -2.0, 0.0);
        assertEquals(expected, cos.getValues(), "Coseno vía fase π/2");
    }

    @Test
    void testGenerateSineAllNormalized() {
        // Un ciclo, fase 0, amplitud ±1, normalizado a 0–100
        int size = 4;
        Signal norm = SignalGenerator.generateSineAll(size, 1.0, 1.0, 0.0, true);
        // sin: 0,1,0,-1 → mapeo [0→50, 1→100, 0→50, -1→0]
        List<Double> expected = List.of(50.0, 100.0, 50.0, 0.0);
        assertEquals(expected, norm.getValues(), "Seno normalizado a 0–100");
    }

    @Test
    void testGenerateSineAllAmplitude2Normalized() {
        // Medio ciclo, fase 0, amplitud ±2, frequency=0.5 (medio ciclo), normalizado
        int size = 4;
        Signal half = SignalGenerator.generateSineAll(size, 2.0, 0.5, 0.0, true);
        // un medio ciclo: sin angles [0, π/2, π, 3π/2] but freq=0.5 desplaza a [0, π/4, π/2, 3π/4]
        // valores brutto = [0, 2·√2/2, 2·1, 2·√2/2] ≈ [0,1.4142,2,1.4142]
        // mapeo: (v+2)/(4)*100 → [50, 85.355, 100, 85.355]
        List<Double> actual = half.getValues();
        assertEquals(50.0,    actual.get(0), 1e-3);
        assertEquals(85.355, actual.get(1), 1e-3);
        assertEquals(100.0,   actual.get(2), 1e-3);
        assertEquals(85.355, actual.get(3), 1e-3);
    }    
    */
    
    @Test
    void testGenerateSineAllBasic() {
        int size = 4;
        Signal sin = SignalGenerator.generateSineAll(size, 1.0, 1.0, 0.0, false);
        double[] expected = {0.0, 1.0, 0.0, -1.0};
        double[] actual   = sin.getValues().stream().mapToDouble(d -> d).toArray();
        assertArrayEquals(expected, actual, 1e-9, "Seno básico sin normalizar");
    }

    @Test
    void testGenerateSineAllFrequency2() {
        int size = 4;
        Signal sin2 = SignalGenerator.generateSineAll(size, 1.0, 2.0, 0.0, false);
        double[] expected = {0.0, 0.0, 0.0, 0.0};
        double[] actual   = sin2.getValues().stream().mapToDouble(d -> d).toArray();
        assertArrayEquals(expected, actual, 1e-9, "Dos ciclos en pocas muestras da ceros");
    }

    @Test
    void testGenerateSineAllPhaseShift() {
        int size = 4;
        Signal cos = SignalGenerator.generateSineAll(size, 2.0, 1.0, Math.PI/2, false);
        double[] expected = {2.0, 0.0, -2.0, 0.0};
        double[] actual   = cos.getValues().stream().mapToDouble(d -> d).toArray();
        assertArrayEquals(expected, actual, 1e-9, "Coseno vía fase π/2");
    }

    @Test
    void testGenerateSineAllNormalized() {
        int size = 4;
        Signal norm = SignalGenerator.generateSineAll(size, 1.0, 1.0, 0.0, true);
        // sin: [0,1,0,-1] → [50,100,50,0]
        double[] expected = { 50.0, 100.0, 50.0, 0.0 };
        double[] actual   = norm.getValues().stream().mapToDouble(d -> d).toArray();
        assertArrayEquals(expected, actual, 1e-9, "Seno normalizado a 0–100");
    }

    @Test
    void testGenerateSineAllAmplitude2Normalized() {
        int size = 4;
        Signal half = SignalGenerator.generateSineAll(size, 2.0, 0.5, 0.0, true);
        double[] actual = half.getValues().stream().mapToDouble(d -> d).toArray();
        double[] expected = {
            50.0,      // (0+2)/(4)*100
            85.355339, // aproximado
            100.0,
            85.355339
        };
        assertArrayEquals(expected, actual, 1e-6, "Medio ciclo normalizado");
    }
    
    /*@Test
    void testGenerateSquare2() {
        int size = 10;
        double amp = 1.0, freq = 1.0;
        Signal sq = SignalGenerator.generateSquare(size, amp, freq);
        List<Double> v = sq.getValues();
        // Primero mitad de la onda >= amp*50+50, después =50
        assertTrue(v.get(0) >= amp*50, "Square alta al inicio");
        assertTrue(v.get(size/2) < amp*50+1, "Square baja a mitad");
    }*/
    
    @Test
    void testGenerateSquare() {
        int size = 10;
        double amp = 1.0, freq = 1.0;
        Signal sq = SignalGenerator.generateSquare(size, amp, freq);
        List<Double> v = sq.getValues();

        // 1) Fase 0 (i=0): sin(0)=0 ≥0 → alto → 100
        assertEquals(amp*50 + 50, v.get(0), "Square alta al inicio (fase 0)");

        // 2) Fase π/2 (i=size/4=2): sin(π/2)=1 → alto → 100
        assertEquals(amp*50 + 50, v.get(size/4), "Square alta en π/2");

        // 3) Fase π   (i=size/2=5): sin(π)=0 ≥0 → alto → 100
        assertEquals(amp*50 + 50, v.get(size/2), "Square alta en π");

        // 4) Fase 3π/2 (i=3*size/4=7): sin(3π/2)=-1 <0 → bajo → 50
        assertEquals(50.0, v.get(3*size/4), "Square baja en 3π/2");
    }
    

    /*@Test
    void testGenerateTriangle() {
        int size = 6;
        double amp  = 2.0, freq = 1.0;
        Signal tri = SignalGenerator.generateTriangle(size, amp, freq);
        List<Double> v = tri.getValues();

        // 1) Tamaño correcto
        assertEquals(size, tri.size(), "Tamaño de la señal");

        // 2) El valor en índice 0 y en size/2 debe ser fase cero → 50
        assertEquals(50.0, v.get(0),             1e-9, "Inicio debe ser 50");
        assertEquals(50.0, v.get(size/2),       1e-9, "Punto media longitud debe ser 50");

        // 3) Simetría: v[i] == v[i + size/2] para i < size/2
        for (int i = 0; i < size/2; i++) {
            assertEquals(v.get(i), v.get(i + size/2), 1e-9,
                String.format("Simetría: v[%d] == v[%d]", i, i + size/2));
        }

        // 4) El pico máximo sigue siendo amp*50 + 50 = 150
        double expectedPeak = amp * 50 + 50;
        double actualPeak   = v.stream().mapToDouble(Double::doubleValue).max().orElseThrow();
        assertEquals(expectedPeak, actualPeak, 1e-9, "Pico triangular debe ser 150");
    }*/
    
    @Test
    void testGenerateTriangleSize4() {
        int size = 4;              // 4 --> indices i=0,1,2,3
        double amp = 2.0, freq = 1.0;
        Signal tri = SignalGenerator.generateTriangle(size, amp, freq);
        List<Double> v = tri.getValues();

        assertEquals(size, tri.size(), "Tamaño debe ser 4");
        // El pico ocurre en i=1 ó i=2: value = amp*50+50 = 150
        double expectedPeak = amp * 50 + 50;
        assertTrue(v.contains(expectedPeak), "Debe contener el pico 150");
        // Verificar fase cero en extremos:
        assertEquals(50.0, v.get(0), 1e-9, "Inicio debe ser 50");
        assertEquals(50.0, v.get(size/2), 1e-9, "Punto medio debe ser 50");
    }
    
    @Test
    void testGenerateTriangleGeneral() {
        int size = 6;
        double amp = 2.0, freq = 1.0;
        Signal tri = SignalGenerator.generateTriangle(size, amp, freq);
        List<Double> v = tri.getValues();

        // 1. Tamaño correcto
        assertEquals(size, tri.size());

        // 2. Todos los valores en [50, 150]
        v.forEach(val -> 
            assertTrue(val >= 50.0 && val <= 150.0, 
                       "Valor fuera de rango: " + val)
        );

        // 3. Simetría: v[i] == v[i + size/2]
        for (int i = 0; i < size/2; i++) {
            assertEquals(v.get(i), v.get(i + size/2), 1e-9,
                String.format("Simetría en %d vs %d", i, i + size/2));
        }
    }
    
    @Test
    void testGenerateWhiteNoise() {
        int size = 1000;
        double amp = 1.0;
        Signal noise = SignalGenerator.generateWhiteNoise(size, amp);
        assertEquals(size, noise.size(), "White noise tamaño correcto");
        // Estimación de media cercana a 50 (por normalización interna)
        double mean = noise.getValues().stream().mapToDouble(d -> d).average().orElse(0);
        assertTrue(Math.abs(mean - 50) < 5, "Media aproximada de ruido");
    }

    @Test
    void testGenerateDelta() {
        int size = 10, pos = 3;
        Signal delta = SignalGenerator.generateDelta(size, pos);
        assertEquals(size, delta.size(), "Delta tamaño correcto");
        for (int i = 0; i < size; i++) {
            double expected = (i == pos ? 100.0 : 0.0);
            assertEquals(expected, delta.getValues().get(i), 1e-9,
                         "Delta en posición " + i);
        }
    }
    
    @Test
    void testGenerateDC() {
        int size = 5;
        double level = 3.14;
        Signal dc = SignalGenerator.generateDC(size, level);

        // Tamaño correcto
        assertEquals(size, dc.size(), "DC debe tener tamaño " + size);

        // Todos los valores iguales a 'level'
        for (double v : dc.getValues()) {
            assertEquals(level, v, 1e-9, "Cada muestra de DC debe ser " + level);
        }
    }

    @Test
    void testGenerateSawtoothNormal() {
        int size = 5;
        double amp = 4.0;
        Signal saw = SignalGenerator.generateSawtooth(size, amp);

        // Esperamos [0.0, 1.0, 2.0, 3.0, 4.0]
        List<Double> expected = List.of(0.0, 1.0, 2.0, 3.0, 4.0);
        assertEquals(size, saw.size(), "Sawtooth debe tener tamaño " + size);
        assertEquals(expected, saw.getValues(), "Valores de sawtooth lineal");
    }

    @Test
    void testGenerateSawtoothTwoPoints() {
        int size = 2;
        double amp = 10.0;
        Signal saw = SignalGenerator.generateSawtooth(size, amp);

        // Con size=2 esperamos [0.0, amp]
        List<Double> expected = List.of(0.0, amp);
        assertEquals(expected, saw.getValues(), "Sawtooth size=2 debe ser [0, amp]");
    }

    @Test
    void testGenerateSawtoothSinglePoint() {
        // Caso size=1 ⇒ devuelve [0.0]
        Signal saw = SignalGenerator.generateSawtooth(1, 7.0);
        assertEquals(1, saw.size(), "Sawtooth size=1 debe tener 1 punto");
        assertEquals(0.0, saw.getValues().get(0), 1e-9, "Sawtooth size=1 debe ser 0.0");
    }

    @Test
    void testGenerateSawtoothEmpty() {
        // Caso size=0 ⇒ lista vacía
        Signal saw = SignalGenerator.generateSawtooth(0, 5.0);
        assertTrue(saw.getValues().isEmpty(), "Sawtooth size=0 debe ser lista vacía");
    }    

    /*@Test
    void testLoadSignalFromFile() throws Exception {
        // Prepara un archivo temporal con valores
        Path tmp = Files.createTempFile("sig", ".txt");
        Files.write(tmp, List.of("1.0","2.5","3.75"));
        List<Double> loaded = SignalGenerator.loadSignalFromFile(tmp.toString());
        assertEquals(List.of(1.0, 2.5, 3.75), loaded, "Cargar valores desde archivo");
        Files.delete(tmp);
    }*/
}
