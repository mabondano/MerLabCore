/*
 * Copyright 2025 Merly Abondano
 *
 * Created:   2025-06-07
 * Author:    Merly Abondano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.merlab.signals.core;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.merlab.signals.core.SignalManager.RPNOp;
import com.merlab.signals.core.SignalProcessor.LengthMode;

public class SignalProcessor {
	
    public enum LengthMode {
        REQUIRE_EQUAL,    // sólo si miden lo mismo
        PAD_WITH_ZEROS,   // extiende con ceros hasta igualar
        THROW_ERROR       // lanza IllegalArgumentException
    }

    /**
     * Ajusta o comprueba longitudes de dos señales según LengthMode.
     */
    private static void validateAlignment_(List<Double> a,
                                         List<Double> b,
                                         LengthMode mode) {
        int na = a.size(), nb = b.size();
        if (na == nb) return;

        switch (mode) {
            case REQUIRE_EQUAL:
                throw new IllegalArgumentException(
                    "Señales de distinto tamaño: " + na + " vs " + nb);
            case THROW_ERROR:              
                throw new IllegalArgumentException("Longitudes distintas entre señales"); //"Señales de distinto tamaño: " + na + " vs " + nb);
            case PAD_WITH_ZEROS:
                if (na < nb) {
                    for (int i = na; i < nb; i++) a.add(0.0);
                } else {
                    for (int i = nb; i < na; i++) b.add(0.0);
                }
                return;
        }
    }	

    /**
     * Valida que dos señales estén alineadas según el modo:
     * - REQUIRE_EQUAL o THROW_ERROR: lanza IllegalArgumentException si longitudes difieren.
     * - PAD_WITH_ZEROS: rellena con ceros hasta igualar longitudes.
     */
    public static void validateAlignment(List<Double> a,
            							List<Double> b,
            							LengthMode mode) {
			int na = a.size(), nb = b.size();
			if (na == nb) {
			return;  // ya igualadas
			}
			switch (mode) {
				case REQUIRE_EQUAL:
				case THROW_ERROR:
					// Asegurarnos de que el mensaje contenga "longitudes"
					throw new IllegalArgumentException("Longitudes distintas entre señales");
				case PAD_WITH_ZEROS:
					int max = Math.max(na, nb);
					while (a.size() < max) a.add(0.0);
					while (b.size() < max) b.add(0.0);
					break;
				//default:
	                //throw new IllegalArgumentException("Modo desconocido: " + mode);
		}
	}   
	
    /**
     * Normaliza la señal al rango [0, 1].
     * @param input señal original
     * @return nueva señal normalizada
     */
    public static Signal process(Signal input) {
        // Suponemos que normalizeTo devuelve List<Double> normalizada
        List<Double> normalized = normalizeTo(input.getValues(), 1.0);
        return new Signal(normalized);
    }
    
    /**
     * Escala una señal: multiplica o divide cada muestra por una constante.
     *
     * @param input  señal original
     * @param factor constante por la que multiplicar o dividir
     * @param divide si es true, divide por factor; si es false, multiplica por factor
     * @return       nueva señal escalada
     */
    public static Signal scale(Signal input, double factor, boolean divide) {
        double f = divide ? 1.0 / factor : factor;
        List<Double> scaled = input.getValues().stream()
            .map(v -> v * f)
            .collect(Collectors.toList());
        return new Signal(scaled);
    }    
    
    /**
     * Escala una señal representada como List<Double>: multiplica o divide
     * cada muestra por un factor, devolviendo otra lista.
     *
     * @param inputSignal lista de valores original
     * @param factor      constante de escalado
     * @param divide      si es true, divide por factor; si es false, multiplica
     * @return            nueva lista escalada
     */
    public static List<Double> scale(List<Double> inputSignal,
                                     double factor,
                                     boolean divide) {
        double f = divide ? 1.0 / factor : factor;
        return inputSignal.stream()
                          .map(v -> v * f)
                          .collect(Collectors.toList());
    }    

    /**
     * Escala un objeto Signal y devuelve otro Signal.
     */
    public static Signal scaleSignal(Signal input,
                                     double factor,
                                     boolean divide) {
        List<Double> scaled = scale(input.getValues(), factor, divide);
        return new Signal(scaled);
    }
    
    public static List<Double> normalizeTo(List<Double> inputSignal, double scale) {
        double max = inputSignal.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        double min = inputSignal.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        double range = (max - min == 0) ? 1.0 : max - min;

        return inputSignal.stream()
                .map(val -> ((val - min) / range) * scale)
                .collect(Collectors.toList());
    }
    
    // Función para decimar la señal con un factor
    public static List<Double> decimate(List<Double> inputSignal, int factor) {
        // Verificamos si el factor es mayor que 0 para evitar errores
        if (factor <= 0) {
            throw new IllegalArgumentException("El factor de decimación debe ser mayor que 0.");
        }

        return inputSignal.stream()
                .filter(val -> inputSignal.indexOf(val) % factor == 0) // Tomamos cada n-ésimo valor
                .collect(Collectors.toList());
    }
    
    // Función para decimar la señal, tomando cada 2 valores
    public static List<Double> decimateByTwo(List<Double> inputSignal) {
        return inputSignal.stream()
                .filter(val -> inputSignal.indexOf(val) % 2 == 0) // Tomamos cada segundo valor
                .collect(Collectors.toList());
    }    
    
    // Función para interpolar la señal con un factor
    public static List<Double> interpolate(List<Double> inputSignal, int factor) {
        List<Double> interpolatedSignal = new ArrayList<>();
        
        // Iteramos sobre la señal original para interpolar
        for (int i = 0; i < inputSignal.size() - 1; i++) {
            double x1 = i;
            double y1 = inputSignal.get(i);
            double x2 = i + 1;
            double y2 = inputSignal.get(i + 1);
            
            // Añadimos el primer punto de la señal original
            interpolatedSignal.add(y1);
            
            // Interpolamos los valores entre el punto i y el i+1
            for (int j = 1; j < factor; j++) {
                double x = x1 + (double) j / factor;
                double y = y1 + (x - x1) * (y2 - y1) / (x2 - x1);
                interpolatedSignal.add(y);
            }
        }
        
        // Añadimos el último punto
        interpolatedSignal.add(inputSignal.get(inputSignal.size() - 1));
        
        return interpolatedSignal;
    }   
    
    /**
     * Calcula la derivada discreta de una señal con paso dt.
     *
     * @param signal lista de valores
     * @param dt     salto de muestras para la diferencia
     * @return       lista de (signal[i] - signal[i-dt]) / dt, para i>=dt
     */
    public static List<Double> derivative(List<Double> signal, int dt) {
        if (dt < 1) {
            throw new IllegalArgumentException("dt debe ser >= 1");
        }
        List<Double> deriv = new ArrayList<>();
        for (int i = dt; i < signal.size(); i++) {
            double diff = signal.get(i) - signal.get(i - dt);
            deriv.add(diff / dt);
        }
        return deriv;
    }

    /** 
     * Mantener la versión sin parámetro para compatibilidad:  
     * equivale a derivative(signal, 1)
     */
    public static List<Double> derivative(List<Double> signal) {
        return derivative(signal, 1);
    }    
    
    //derivador
    public static List<Double> derivative1(List<Double> signal) {
        List<Double> derivative = new ArrayList<>();
        for (int i = 1; i < signal.size(); i++) {
            derivative.add(signal.get(i) - signal.get(i - 1));
        }
        return derivative;
    }

    //integrador
    public static List<Double> integrate(List<Double> signal) {
        List<Double> integratedSignal = new ArrayList<>();
        double sum = 0;
        for (double value : signal) {
            sum += value;
            integratedSignal.add(sum);
        }
        return integratedSignal;
    }
    
    /**
     * Suma elemento a elemento dos señales.
     */
    public static List<Double> addSignals_(List<Double> a,
                                          List<Double> b,
                                          LengthMode mode) {
        // creamos copias para no mutar las originales
        List<Double> x = new ArrayList<>(a);
        List<Double> y = new ArrayList<>(b);
        validateAlignment(x, y, mode);
        List<Double> out = new ArrayList<>(x.size());
        for (int i = 0; i < x.size(); i++) {
            out.add(x.get(i) + y.get(i));
        }
        return out;
    }
    
    public static List<Double> addSignals(List<Double> a, List<Double> b, LengthMode mode) {
        List<Double> x = new ArrayList<>(a);
        List<Double> y = new ArrayList<>(b);

        if (mode == LengthMode.PAD_WITH_ZEROS) {
        	System.out.println("ADD PAD_WITH_ZEROS");
            int maxLen = Math.max(x.size(), y.size());
            // Padding para x
            while (x.size() < maxLen) x.add(0.0);
            // Padding para y
            while (y.size() < maxLen) y.add(0.0);
            System.out.println("x:" + x);
            System.out.println("y:" + y);
        } else {
        	System.out.println("ADD REQUIRE_EQUAL");
            validateAlignment(x, y, mode);
        }

        List<Double> out = new ArrayList<>(x.size());
        for (int i = 0; i < x.size(); i++) {
            out.add(x.get(i) + y.get(i));
        }
        return out;
    }


    /**
     * Resta elemento a elemento dos señales (a - b).
     */
    public static List<Double> subtractSignals(List<Double> a,
                                               List<Double> b,
                                               LengthMode mode) {
        List<Double> x = new ArrayList<>(a);
        List<Double> y = new ArrayList<>(b);
        validateAlignment(x, y, mode);
        List<Double> out = new ArrayList<>(x.size());
        for (int i = 0; i < x.size(); i++) {
            out.add(x.get(i) - y.get(i));
        }
        return out;
    }

    /**
     * Multiplica elemento a elemento dos señales.
     */
    public static List<Double> multiplySignals(List<Double> a,
                                               List<Double> b,
                                               LengthMode mode) {
        List<Double> x = new ArrayList<>(a);
        List<Double> y = new ArrayList<>(b);
        validateAlignment(x, y, mode);
        List<Double> out = new ArrayList<>(x.size());
        for (int i = 0; i < x.size(); i++) {
            out.add(x.get(i) * y.get(i));
        }
        return out;
    }
    

    /**
     * Divide elemento a elemento dos señales (a ÷ b).
     *
     * @param a     primera señal (numerador)
     * @param b     segunda señal (denominador)
     * @param mode  modo de alineación de longitudes
     * @return      lista con a[i] / b[i]
     * @throws      IllegalArgumentException si b[i] == 0 en alguna posición
     *              o si las longitudes difieren y mode == REQUIRE_EQUAL/THROW_ERROR
     */
    public static List<Double> divideSignals(List<Double> a,
                                             List<Double> b,
                                             LengthMode mode) {
        // Copiamos para no mutar las originales
        List<Double> x = new ArrayList<>(a);
        List<Double> y = new ArrayList<>(b);
        validateAlignment(x, y, mode);

        List<Double> out = new ArrayList<>(x.size());
        for (int i = 0; i < x.size(); i++) {
            double denom = y.get(i);
            if (denom == 0.0) {
                throw new IllegalArgumentException(
                    "División por cero en la posición " + i
                );
            }
            out.add(x.get(i) / denom);
        }
        return out;
    }    

    /**
     * Convolución discreta (sin invertir y sin “stride”), línea base.
     */
    public static List<Double> convolve(List<Double> a,
                                        List<Double> b,
                                        LengthMode mode) {
        // Para convolución clásica, no necesitamos validar igual longitud:
        // asumimos a como “señal” y b como “kernel”
        int na = a.size(), nb = b.size();
        List<Double> out = new ArrayList<>(na + nb - 1);
        for (int n = 0; n < na + nb - 1; n++) {
            double sum = 0;
            for (int k = 0; k < nb; k++) {
                int i = n - k;
                if (i >= 0 && i < na) {
                    sum += a.get(i) * b.get(k);
                }
            }
            out.add(sum);
        }
        return out;
    }    
    
   
    
    /**
     * Convolución clásica invirtiendo el kernel y usando stride.
     *
     * @param signal lista de valores de entrada
     * @param kernel lista del kernel (se invierte internamente)
     * @param stride salto entre posiciones de salida (>=1)
     * @return       lista con los valores de convolución
     */
    public static List<Double> convolveReversedWithStride(List<Double> signal,
                                                List<Double> kernel,
                                                int stride) {
        if (stride < 1) {
            throw new IllegalArgumentException("Stride debe ser >= 1");
        }
        int n = signal.size();
        int k = kernel.size();
        if (k == 0 || n == 0 || n < k) {
            return Collections.emptyList();
        }
        // invertir el kernel
        List<Double> rev = new ArrayList<>(kernel);
        Collections.reverse(rev);

        List<Double> out = new ArrayList<>();
        // desplazamiento de la ventana de longitud k, avanzando de stride en stride
        for (int i = 0; i + k <= n; i += stride) {
            double sum = 0;
            for (int j = 0; j < k; j++) {
                sum += signal.get(i + j) * rev.get(j);
            }
            out.add(sum);
        }
        return out;
    }    
    
    public static List<Double> convolveReversed(List<Double> x, List<Double> h) {
        // reverse h
        List<Double> hr = new ArrayList<>(h);
        Collections.reverse(hr);
        return convolve(x, hr);
    }
    
    
    //add
    public static List<Double> addSignals(List<Double> signal1, List<Double> signal2) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < signal1.size(); i++) {
            result.add(signal1.get(i) + signal2.get(i));
        }
        return result;
    }
    
    //subtract
    public static List<Double> subtractSignals(List<Double> signal1, List<Double> signal2) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < signal1.size(); i++) {
            result.add(signal1.get(i) - signal2.get(i));
        }
        return result;
    }
    
    //convolve
    public static List<Double> convolve_(List<Double> signal, List<Double> kernel) {
        List<Double> result = new ArrayList<>(Collections.nCopies(signal.size(), 0.0));
        for (int i = 0; i < signal.size() - kernel.size(); i++) {
            for (int j = 0; j < kernel.size(); j++) {
                result.set(i + j, result.get(i + j) + signal.get(i + j) * kernel.get(j));
            }
        }
        return result;
    }
    
    public static List<Double> convolve(List<Double> x, List<Double> h) {
        int n = x.size();
        int m = h.size();
        List<Double> y = new ArrayList<>(n + m - 1);
        // for each possible output index k
        for (int k = 0; k < n + m - 1; k++) {
            double sum = 0;
            // sum over all overlaps
            for (int i = 0; i < n; i++) {
                int j = k - i;
                if (j >= 0 && j < m) {
                    sum += x.get(i) * h.get(j);
                }
            }
            y.add(sum);
        }
        return y;
    }    
    
    //lowpassfilter
    public static List<Double> lowPassFilter(List<Double> signal, double alpha) {
        List<Double> result = new ArrayList<>();
        result.add(signal.get(0)); // El primer valor no cambia
        for (int i = 1; i < signal.size(); i++) {
            result.add(alpha * signal.get(i) + (1 - alpha) * result.get(i - 1));
        }
        return result;
    }

    /**
     * Filtro pasa altos exponencial discreto:
     * y[n] = α·(y[n-1] + x[n] – x[n-1])
     *
     * @param signal lista de valores de entrada
     * @param alpha  factor de filtro en [0,1]
     * @return       nueva lista con la señal filtrada
     */
    public static List<Double> highPassFilter(List<Double> signal, double alpha) {
        if (signal.isEmpty()) {
            return Collections.emptyList();
        }
        List<Double> result = new ArrayList<>(signal.size());
        // Conservamos el primer valor sin filtrar
        result.add(signal.get(0));
        for (int i = 1; i < signal.size(); i++) {
            double hp = alpha * (result.get(i - 1) + signal.get(i) - signal.get(i - 1));
            result.add(hp);
        }
        return result;
    }
    
    /**
     * Filtro pasabanda simple: primero pasa-altos (alphaHP), luego pasa-bajos (alphaLP).
     *
     * @param signal    señal de entrada
     * @param alphaHP   coeficiente de filtro pasa-altos en [0,1]
     * @param alphaLP   coeficiente de filtro pasa-bajos en [0,1]
     * @return          señal filtrada en la banda definida
     */
    public static List<Double> bandPassFilter(List<Double> signal,
                                              double alphaHP,
                                              double alphaLP) {
        // 1) Aplicar filtro pasa-altos
        List<Double> high = highPassFilter(signal, alphaHP);
        // 2) Aplicar filtro pasa-bajos al resultado
        return lowPassFilter(high, alphaLP);
    }
    
    /** Comprueba si n es potencia de dos */
    private static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    /**
     * FFT radix-2 recursiva. Entrada real en lista, salida compleja.
     * @param signal lista de valores reales
     * @return lista de coeficientes complejos de la FFT
     */
    public static List<Complex> fft(List<Double> signal) {
        int n = signal.size();
        if (!isPowerOfTwo(n)) {
            throw new IllegalArgumentException("FFT requiere longitud potencia de dos, got " + n);
        }
        // Construir lista de complejos
        List<Complex> x = new ArrayList<>(n);
        for (double v : signal) x.add(new Complex(v, 0.0));
        return fftRecursive(x);
    }

    /** Implementación recursiva radix-2 **/
    private static List<Complex> fftRecursive(List<Complex> x) {
        int n = x.size();
        if (n == 1) {
            return List.of(x.get(0));
        }
        // dividir pares e impares
        List<Complex> even = new ArrayList<>(n/2), odd = new ArrayList<>(n/2);
        for (int i = 0; i < n; i++) {
            if ((i & 1) == 0) even.add(x.get(i));
            else             odd.add(x.get(i));
        }
        List<Complex> Fe = fftRecursive(even);
        List<Complex> Fo = fftRecursive(odd);
        List<Complex> F  = new ArrayList<>(n);
        for (int k = 0; k < n; k++) {
            F.add(null);
        }
        for (int k = 0; k < n/2; k++) {
            double angle = -2 * Math.PI * k / n;
            Complex w = new Complex(Math.cos(angle), Math.sin(angle));
            Complex t = w.mul(Fo.get(k));
            F.set(k,       Fe.get(k).add(t));
            F.set(k + n/2, Fe.get(k).sub(t));
        }
        return F;
    }    
    
    // ————————————————————————————————————————
    // I. Estadísticos de posición y dispersión
    // ————————————————————————————————————————
    /** Media aritmética */
    public static double mean(List<Double> x) {
        return x.stream().mapToDouble(d -> d).average()
                .orElseThrow(() -> new IllegalArgumentException("Señal vacía"));
    }

    /** Varianza poblacional */
    public static double variance(List<Double> x) {
        double m = mean(x);
        return x.stream().mapToDouble(d -> (d - m)*(d - m)).average().orElse(0.0);
    }

    /** Desviación estándar poblacional */
    public static double stdDev(List<Double> x) {
        return Math.sqrt(variance(x));
    }

    /** Mediana */
    public static double median(List<Double> x) {
        int n = x.size();
        if (n == 0) throw new IllegalArgumentException("Señal vacía");
        List<Double> sorted = new ArrayList<>(x);
        Collections.sort(sorted);
        if (n % 2 == 1) return sorted.get(n/2);
        return (sorted.get(n/2 - 1) + sorted.get(n/2)) / 2.0;
    }

    /** Percentil p (0–100) */
    public static double percentile(List<Double> x, double p) {
        if (x.isEmpty()) throw new IllegalArgumentException("Señal vacía");
        if (p < 0 || p > 100)   throw new IllegalArgumentException("p debe estar en [0,100]");
        List<Double> s = new ArrayList<>(x);
        Collections.sort(s);
        double idx = p/100.0 * (s.size()-1);
        int lo = (int)Math.floor(idx), hi = (int)Math.ceil(idx);
        if (lo == hi) return s.get(lo);
        double frac = idx - lo;
        return s.get(lo) + frac * (s.get(hi) - s.get(lo));
    }

    /** Rango (max – min) */
    public static double range(List<Double> x) {
        return max(x) - min(x);
    }

    /** Valor mínimo */
    public static double min(List<Double> x) {
        return x.stream().mapToDouble(d->d).min()
                .orElseThrow(() -> new IllegalArgumentException("Señal vacía"));
    }

    /** Valor máximo */
    public static double max(List<Double> x) {
        return x.stream().mapToDouble(d->d).max()
                .orElseThrow(() -> new IllegalArgumentException("Señal vacía"));
    }

    // ————————————————————————————————————————
    // II. Momentos de orden superior
    // ————————————————————————————————————————
    /** Asimetría (skewness) muestral */
    public static double skewness(List<Double> x) {
        int n = x.size();
        if (n < 3) throw new IllegalArgumentException("Señal muy corta");
        double m = mean(x), sd = stdDev(x);
        return x.stream()
                .mapToDouble(d -> Math.pow((d-m)/sd, 3))
                .sum() / n;
    }

    /** Curtosis (exceso de curtosis) muestral */
    public static double kurtosis(List<Double> x) {
        int n = x.size();
        if (n < 4) throw new IllegalArgumentException("Señal muy corta");
        double m = mean(x), sd = stdDev(x);
        return x.stream()
                .mapToDouble(d -> Math.pow((d-m)/sd, 4))
                .sum() / n - 3.0;
    }

    // ————————————————————————————————————————
    // III. Estadísticos de forma de onda
    // ————————————————————————————————————————
    /** Número de cruces por cero */
    public static int zeroCrossingRate(List<Double> x) {
        int count = 0;
        for (int i = 1; i < x.size(); i++) {
            if ((x.get(i-1) >= 0 && x.get(i) < 0) ||
                (x.get(i-1) <  0 && x.get(i) >= 0)) {
                count++;
            }
        }
        return count;
    }

    /** Larga‐cola‐ratio: proporción de valores > umbral (usado en RQA) */
    public static double lqr(List<Double> x, double threshold) {
        long above = x.stream().filter(d->Math.abs(d)>threshold).count();
        return (double)above / x.size();
    }

    // ————————————————————————————————————————
    // IV. Series temporales y correlación
    // ————————————————————————————————————————
    /** Autocorrelación en lag k */
    public static double autocorrelation(List<Double> x, int k) {
        int n = x.size();
        if (k < 0 || k >= n) throw new IllegalArgumentException("Lag inválido");
        double m = mean(x);
        double num = 0, den = 0;
        for (double v : x) den += (v - m)*(v - m);
        for (int i = 0; i + k < n; i++) {
            num += (x.get(i) - m)*(x.get(i+k) - m);
        }
        return num/den;
    }

    /** Convolución directa sin invertir kernel */
    public static List<Double> convolve2(List<Double> x, List<Double> h) {
        int n = x.size(), m = h.size();
        List<Double> y = new ArrayList<>(Collections.nCopies(n+m-1, 0.0));
        for (int i=0; i<n; i++) {
            for (int j=0; j<m; j++) {
                y.set(i+j, y.get(i+j) + x.get(i)*h.get(j));
            }
        }
        return y;
    }

    /** Convolución con kernel invertido y stride */
    public static List<Double> convolveReversed(List<Double> x,
                                                List<Double> h,
                                                int stride,
                                                LengthMode mode) {
        // invertir h
        List<Double> kr = new ArrayList<>(h);
        Collections.reverse(kr);
        // alinear longitudes
        List<Double> a = new ArrayList<>(x), b = new ArrayList<>(kr);
        validateAlignment(a,b,mode);
        // stride >1: nos saltamos puntos
        List<Double> y = new ArrayList<>();
        for (int i=0; i<a.size(); i+=stride) {
            double sum=0;
            for (int j=0; j<b.size(); j++) {
                int idx = i+j;
                if (idx < a.size()) sum += a.get(idx)*b.get(j);
            }
            y.add(sum);
        }
        return y;
    }

    /** Validación y alineación de dos señales */
    private static void validateAndAlign2(List<Double> a,
                                         List<Double> b,
                                         LengthMode mode) {
        int na = a.size(), nb = b.size();
        if (na == nb) return;
        switch (mode) {
            case REQUIRE_EQUAL:
                throw new IllegalArgumentException("Longitudes distintas");
            case THROW_ERROR:
                throw new IllegalArgumentException("Longitudes distintas");
            case PAD_WITH_ZEROS:
                int max = Math.max(na, nb);
                while (a.size() < max) a.add(0.0);
                while (b.size() < max) b.add(0.0);
                break;
        }
    }
    

    

    // ————————————————————————————————————————
    // V. Ventanas móviles y suavizado
    // ————————————————————————————————————————
    /** Media móvil */
    public static List<Double> movingAverage(List<Double> x, int window) {
        int n = x.size();
        if (window < 1 || window > n) throw new IllegalArgumentException("Window inválida");
        List<Double> y = new ArrayList<>();
        double sum = 0;
        for (int i=0; i<window; i++) sum += x.get(i);
        y.add(sum/window);
        for (int i=window; i<n; i++) {
            sum += x.get(i) - x.get(i-window);
            y.add(sum/window);
        }
        return y;
    }

    /** Media móvil ponderada */
    public static List<Double> weightedMovingAverage(List<Double> x, int window) {
        int n = x.size();
        if (window < 1 || window > n) throw new IllegalArgumentException("Window inválida");
        List<Double> y = new ArrayList<>();
        for (int i=0; i<=n-window; i++) {
            double sum=0, wsum=0;
            for (int j=0; j<window; j++) {
                double w = window - j;
                sum += x.get(i+j)*w;
                wsum += w;
            }
            y.add(sum/wsum);
        }
        return y;
    }

    /** Suavizado gaussiano */
    public static List<Double> gaussianSmoothing(List<Double> x, int window) {
        // creamos kernel gaussiano
        if (window < 1 || window > 5) throw new IllegalArgumentException("Window en [1,5]");
        double sigma = window/2.0;
        List<Double> kernel = new ArrayList<>();
        for (int i=0; i<window; i++) {
            double t = i - (window-1)/2.0;
            kernel.add(Math.exp(-t*t/(2*sigma*sigma)));
        }
        double sum=kernel.stream().mapToDouble(d->d).sum();
        for (int i=0; i<kernel.size(); i++) kernel.set(i, kernel.get(i)/sum);
        return convolveReversed(x, kernel, 1, LengthMode.PAD_WITH_ZEROS);
    }

    /** Filtro pasabajas simple */
    public static List<Double> lowPassFilter2(List<Double> x, double alpha) {
        if (alpha < 0 || alpha > 1) throw new IllegalArgumentException("α en [0,1]");
        List<Double> y = new ArrayList<>();
        y.add(x.get(0));
        for (int i=1; i<x.size(); i++) {
            y.add(alpha*x.get(i) + (1-alpha)*y.get(i-1));
        }
        return y;
    }

    /** Filtro pasabanda = bandpass(X) = highPass(lowPass(X)) */
    public static List<Double> bandPassFilter2(List<Double> x,
                                              double lowAlpha,
                                              double highAlpha) {
        return highPassFilter(
                  lowPassFilter(x, lowAlpha),
                  highAlpha
               );
    }

    /** Filtro pasaaltas simple */
    public static List<Double> highPassFilter2(List<Double> x, double alpha) {
        List<Double> y = new ArrayList<>();
        y.add(x.get(0));
        for (int i=1; i<x.size(); i++) {
            double hp = x.get(i) - alpha*x.get(i-1) + alpha*y.get(i-1);
            y.add(hp);
        }
        return y;
    }

    // ————————————————————————————————————————
    // VI. Derivadas e integrales
    // ————————————————————————————————————————
    /** Derivada discreta (Δx) */
    public static List<Double> derivative2(List<Double> x) {
        List<Double> dx = new ArrayList<>();
        for (int i=1; i<x.size(); i++) {
            dx.add(x.get(i) - x.get(i-1));
        }
        return dx;
    }

    /** Integración simple (sumatoria) */
    public static List<Double> integrate2(List<Double> x) {
        List<Double> y = new ArrayList<>();
        double sum=0;
        for (double v : x) {
            sum += v;
            y.add(sum);
        }
        return y;
    }

    // ————————————————————————————————————————
    // VII. Transformadas (FFT)
    // ————————————————————————————————————————
    /** FFT (Cooley–Tukey) solo potencias de dos */
    public static Complex2[] fft2(double[] real) {
        int n = real.length;
        if (Integer.bitCount(n) != 1)
            throw new IllegalArgumentException("Length debe ser potencia de 2");
        Complex2[] x = new Complex2[n];
        for (int i=0; i<n; i++) x[i] = new Complex2(real[i], 0);
        fftRecursive2(x);
        return x;
    }

    private static void fftRecursive2(Complex2[] x) {
        int n = x.length;
        if (n<=1) return;
        Complex2[] even = new Complex2[n/2], odd = new Complex2[n/2];
        for (int i=0; i<n/2; i++){
            even[i] = x[2*i];
            odd[i]  = x[2*i+1];
        }
        fftRecursive2(even);
        fftRecursive2(odd);
        for (int k=0; k<n/2; k++){
            double angle = -2*Math.PI*k/n;
            Complex2 wk = Complex2.exp(angle);
            x[k]       = even[k].plus(wk.times(odd[k]));
            x[k+n/2]  = even[k].minus(wk.times(odd[k]));
        }
    }

    // Clase auxiliar para FFT
    public static class Complex2 {
        public final double re, im;
        public Complex2(double r, double i){ re=r; im=i; }
        public Complex2 plus(Complex2 o) {
            return new Complex2(re+o.re, im+o.im);
        }
        public Complex2 minus(Complex2 o) {
            return new Complex2(re-o.re, im-o.im);
        }
        public Complex2 times(Complex2 o) {
            return new Complex2(re*o.re - im*o.im,
                               re*o.im + im*o.re);
        }
        public static Complex2 exp(double theta) {
            return new Complex2(Math.cos(theta), Math.sin(theta));
        }
    }

    // ————————————————————————————————————————
    // VIII. Operaciones binarias (RPN)
    // ————————————————————————————————————————
    public static List<Double> addSignals2(List<Double> a,
                                          List<Double> b,
                                          LengthMode mode) {
        List<Double> A = new ArrayList<>(a), B = new ArrayList<>(b);
        validateAlignment(A, B, mode);
        List<Double> C = new ArrayList<>(A.size());
        for (int i=0; i<A.size(); i++) {
            C.add(A.get(i) + B.get(i));
        }
        return C;
    }

    public static List<Double> subtractSignals2(List<Double> a,
                                               List<Double> b,
                                               LengthMode mode) {
        List<Double> A = new ArrayList<>(a), B = new ArrayList<>(b);
        validateAlignment(A, B, mode);
        List<Double> C = new ArrayList<>(A.size());
        for (int i=0; i<A.size(); i++) {
            C.add(A.get(i) - B.get(i));
        }
        return C;
    }

    public static List<Double> multiplySignals2(List<Double> a,
                                               List<Double> b,
                                               LengthMode mode) {
        List<Double> A = new ArrayList<>(a), B = new ArrayList<>(b);
        validateAlignment(A, B, mode);
        List<Double> C = new ArrayList<>(A.size());
        for (int i=0; i<A.size(); i++) {
            C.add(A.get(i) * B.get(i));
        }
        return C;
    }

    public static List<Double> divideSignals2(List<Double> a,
                                             List<Double> b,
                                             LengthMode mode) {
        List<Double> A = new ArrayList<>(a), B = new ArrayList<>(b);
        validateAlignment(A, B, mode);
        List<Double> C = new ArrayList<>(A.size());
        for (int i=0; i<A.size(); i++) {
            if (B.get(i) == 0.0)
                throw new IllegalArgumentException("División por cero en idx " + i);
            C.add(A.get(i) / B.get(i));
        }
        return C;
    }


    
}
