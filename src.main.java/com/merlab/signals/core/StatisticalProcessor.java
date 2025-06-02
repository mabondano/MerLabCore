package com.merlab.signals.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Calcula estadísticas básicas de una señal.
 */
public class StatisticalProcessor {
	
    /**
     * Devuelve un mapa (operación → descripción) de todas las funciones disponibles.
     */
    public static Map<String, String> getOperationDescriptions() {
        Map<String, String> ops = new LinkedHashMap<>();

        // Estadísticos básicos
        ops.put("count",               "int count(Signal): número de muestras");
        ops.put("sum",                 "double sum(Signal): Σ xᵢ");
        ops.put("mean",                "double mean(Signal): media aritmética");
        ops.put("variance",            "double variance(Signal): varianza poblacional");
        ops.put("stdDev",              "double stdDev(Signal): desviación estándar");
        ops.put("min",                 "double min(Signal): valor mínimo");
        ops.put("max",                 "double max(Signal): valor máximo");
        ops.put("range",               "double range(Signal): max – min");
        ops.put("median",              "double median(Signal): mediana");
        ops.put("percentile",          "double percentile(Signal, double p): percentil p");
        ops.put("sumOfSquares",        "double sumOfSquares(Signal): Σ xᵢ²");
        ops.put("rms",                 "double rms(Signal): raíz cuadrada de la media de cuadrados");
        ops.put("skewness",            "double skewness(Signal): coeficiente de asimetría");
        ops.put("kurtosis",            "double kurtosis(Signal): curtosis poblacional");
        ops.put("extractStats",        "Signal extractStats(Signal): devuelve [media, varianza]");

        // Posición y dispersión adicionales
        ops.put("mode",                "double mode(Signal): valor más frecuente");
        ops.put("iqr",                 "double iqr(Signal): rango intercuartílico (Q3–Q1)");
        ops.put("coefficientOfVariation", "double coefficientOfVariation(Signal): desviación/ media");
        ops.put("standardError",       "double standardError(Signal): σ / √n");
        ops.put("meanAbsoluteDeviation", "double meanAbsoluteDeviation(Signal): media de |x–μ|");
        ops.put("medianAbsoluteDeviation", "double medianAbsoluteDeviation(Signal): mediana de |x–mediana|");
        ops.put("energy",              "double energy(Signal): alias de sumOfSquares");
        ops.put("entropy",             "double entropy(Signal): entropía de Shannon");

        // Momentos de orden superior
        ops.put("thirdCentralMoment",  "double thirdCentralMoment(Signal): Σ(x–μ)³ / n");
        ops.put("fourthCentralMoment", "double fourthCentralMoment(Signal): Σ(x–μ)⁴ / n");

        // Forma de onda
        ops.put("peakToPeak",          "double peakToPeak(Signal): max(|x|) – min(|x|)");
        ops.put("crestFactor",         "double crestFactor(Signal): pico / RMS");
        ops.put("impulseFactor",       "double impulseFactor(Signal): pico / media(|x|)");
        ops.put("shapeFactor",         "double shapeFactor(Signal): RMS / media(|x|)");
        ops.put("clearanceFactor",     "double clearanceFactor(Signal): pico / media(√|x|)");
        ops.put("marginFactor",        "double marginFactor(Signal): pico / media(|x|^(1/4))");
        ops.put("zeroCrossingRate",    "double zeroCrossingRate(Signal): cruces por cero por muestra");

        // Series temporales y correlación
        ops.put("autocorrelation",     "double autocorrelation(Signal, int lag): autocorrelación en lag");
        ops.put("autocorrelations",    "double[] autocorrelations(Signal, int maxLag): r[0..maxLag]");
        ops.put("partialAutocorrelation", "double partialAutocorrelation(Signal, int p): PACF p,p");
        ops.put("crossCorrelation",    "double crossCorrelation(Signal x, Signal y, int lag): correlación cruzada");

        // Ventanas móviles y suavizado
        ops.put("movingAverage",       "Signal movingAverage(Signal, int window): media móvil");
        ops.put("exponentialMovingAverage", "Signal exponentialMovingAverage(Signal, double α): EMA");
        ops.put("movingStdDev",        "Signal movingStdDev(Signal, int window): desviación móvil");
        ops.put("movingMedian",        "Signal movingMedian(Signal, int window): mediana móvil");
        ops.put("weightedMovingAverage", "Signal weightedMovingAverage(Signal, double[] weights): WMA");
        ops.put("triangularMovingAverage", "Signal triangularMovingAverage(Signal, int window): TMA");
        ops.put("gaussianSmoothing",   "Signal gaussianSmoothing(Signal, double σ): suavizado gaussiano");
        ops.put("savitzkyGolay5",      "Signal savitzkyGolay5(Signal): filtro Savitzky–Golay");

        return ops;
    }

    /**
     * Imprime en consola todas las operaciones y sus usos.
     */
    public static void printOperations() {
        getOperationDescriptions()
            .forEach((name, desc) -> System.out.printf("%-30s : %s%n", name, desc));
    }	
	

    /** 1. Count: número de muestras */
    public static int count(Signal input) {
        return input.size();
    }

    /** 2. Sum: Σ xᵢ */
    public static double sum(Signal input) {
        double total = 0.0;
        for (double v : input.getValues()) {
            total += v;
        }
        return total;
    }

    /** 3. Mean: μ = Σ xᵢ / n */
    public static double mean(Signal input) {
        int n = count(input);
        if (n == 0) throw new IllegalArgumentException("Señal vacía");
        return sum(input) / n;
    }

    /** 4. Variance: σ² = Σ (xᵢ – μ)² / n */
    public static double variance(Signal input) {
        int n = count(input);
        if (n == 0) throw new IllegalArgumentException("Señal vacía");
        double μ = mean(input);
        double acc = 0.0;
        for (double v : input.getValues()) {
            double d = v - μ;
            acc += d * d;
        }
        return acc / n;
    }

    /** 5. Standard Deviation: σ = √σ² */
    public static double stdDev(Signal input) {
        return Math.sqrt(variance(input));
    }

    /** 6a. Min: valor mínimo */
    public static double min(Signal input) {
        List<Double> vals = input.getValues();
        if (vals.isEmpty()) throw new IllegalArgumentException("Señal vacía");
        return Collections.min(vals);
    }

    /** 6b. Max: valor máximo */
    public static double max(Signal input) {
        List<Double> vals = input.getValues();
        if (vals.isEmpty()) throw new IllegalArgumentException("Señal vacía");
        return Collections.max(vals);
    }

    /** 7. Range: R = max – min */
    public static double range(Signal input) {
        return max(input) - min(input);
    }

    /** 8. Median: valor central (o promedio de dos centrales) */
    public static double median(Signal input) {
        List<Double> sorted = new ArrayList<>(input.getValues());
        Collections.sort(sorted);
        int n = sorted.size();
        if (n == 0) throw new IllegalArgumentException("Señal vacía");
        if (n % 2 == 1) {
            return sorted.get(n / 2);
        } else {
            double a = sorted.get(n/2 - 1);
            double b = sorted.get(n/2);
            return (a + b) / 2.0;
        }
    }

    /** 9. Percentile p (0–100) con interpolación lineal */
    public static double percentile(Signal input, double p) {
        List<Double> sorted = new ArrayList<>(input.getValues());
        Collections.sort(sorted);
        int n = sorted.size();
        if (n == 0) throw new IllegalArgumentException("Señal vacía");
        if (p < 0 || p > 100) throw new IllegalArgumentException("Percentil fuera de rango");
        
        // Caso especial para cuartiles con método Tukey
        if (p == 25.0) {
            return medianOfList(sorted.subList(0, n/2));
        }
        if (p == 50.0) {
            return medianOfList(sorted);
        }
        if (p == 75.0) {
            // la mitad superior: si n es par, empieza en n/2; si es impar, en (n+1)/2
            return medianOfList(sorted.subList((n+1)/2, n));
        }
   
        // Fallback: interpolación lineal para otros percentiles
        double idx = p / 100.0 * (n - 1);
        int lo = (int)Math.floor(idx);
        int hi = (int)Math.ceil(idx);
        if (lo == hi) {
            return sorted.get(lo);
        }
        double frac = idx - lo;
        return sorted.get(lo) * (1 - frac) + sorted.get(hi) * frac;
    }
    
    /** Auxiliar para calcular mediana de cualquier lista sorted */
    private static double medianOfList(List<Double> list) {
        int m = list.size();
        if (m == 0) {
            throw new IllegalArgumentException("Lista vacía");
        }
        if (m % 2 == 1) {
            return list.get(m / 2);
        } else {
            return (list.get(m/2 - 1) + list.get(m/2)) / 2.0;
        }
    }    

    /** 10. Sum of Squares: Σ xᵢ² */
    public static double sumOfSquares(Signal input) {
        double acc = 0.0;
        for (double v : input.getValues()) {
            acc += v * v;
        }
        return acc;
    }

    /** 11. RMS: √(Σ xᵢ² / n) */
    public static double rms(Signal input) {
        int n = count(input);
        if (n == 0) throw new IllegalArgumentException("Señal vacía");
        return Math.sqrt(sumOfSquares(input) / n);
    }

    /** 12. Skewness (asimetría poblacional) */
    public static double skewness(Signal input) {
        int n = count(input);
        if (n == 0) throw new IllegalArgumentException("Señal vacía");
        double μ = mean(input);
        double σ = stdDev(input);
        if (σ == 0) return 0.0;
        double acc = 0.0;
        for (double v : input.getValues()) {
            acc += Math.pow((v - μ) / σ, 3);
        }
        return acc / n;
    }

    /** 13. Kurtosis (curtosis poblacional) */
    public static double kurtosis(Signal input) {
        int n = count(input);
        if (n == 0) throw new IllegalArgumentException("Señal vacía");
        double μ = mean(input);
        double σ = stdDev(input);
        if (σ == 0) return 0.0;
        double acc = 0.0;
        for (double v : input.getValues()) {
            acc += Math.pow((v - μ) / σ, 4);
        }
        return acc / n;
    }	

    /**
     * 14. Extrae media y varianza de la señal de entrada.
     * @param input señal de la que se extraen estadísticas
     * @return señal de dos puntos: [media, varianza]
     */
    public static Signal extractStats(Signal input) {
        int n = input.size();
        if (n == 0) {
            throw new IllegalArgumentException("No se puede calcular estadísticas de una señal vacía");
        }

        List<Double> data = input.getValues();

        // Media
        double sum = 0.0;
        for (double v : data) {
            sum += v;
        }
        double mean = sum / n;

        // Varianza
        double acc = 0.0;
        for (double v : data) {
            double diff = v - mean;
            acc += diff * diff;
        }
        double variance = acc / n;

        // Creamos una señal con [media, varianza]
        Signal stats = new Signal();
        stats.add(mean);
        stats.add(variance);

        return stats;
    }
    
    /**
     * Estadísticos de posición y dispersión para una señal.
     */

    /**
     * 15. Mode: valor más frecuente. Si hay varios, devuelve uno cualquiera.
     */
    public static double mode(Signal input) {
        List<Double> data = input.getValues();
        if (data.isEmpty()) throw new IllegalArgumentException("Señal vacía");

        Map<Double, Integer> freq = new HashMap<>();
        for (double v : data) {
            freq.put(v, freq.getOrDefault(v, 0) + 1);
        }
        return Collections.max(freq.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    /**
     * 16. Interquartile Range (IQR): Q3 - Q1.
     */
    public static double iqr(Signal input) {
        return percentile(input, 75.0) - percentile(input, 25.0);
    }

    /**
     * 17. Quantile genérico (ya lo tienes, se reutiliza):
     * percentile(input, p)
     */

    /**
     * 18. Coefficient of Variation (CV): σ / μ
     */
    public static double coefficientOfVariation(Signal input) {
        double mu = mean(input);
        if (mu == 0) throw new IllegalArgumentException("Media cero, CV indefinido");
        return stdDev(input) / mu;
    }

    /**
     * 19. Standard Error of the Mean (SEM): σ / √n
     */
    public static double standardError(Signal input) {
        int n = count(input);
        if (n == 0) throw new IllegalArgumentException("Señal vacía");
        return stdDev(input) / Math.sqrt(n);
    }

    /**
     * 20. Mean Absolute Deviation (MAD): mean(|xᵢ – μ|)
     */
    public static double meanAbsoluteDeviation(Signal input) {
        double mu = mean(input);
        List<Double> data = input.getValues();
        double acc = 0.0;
        for (double v : data) {
            acc += Math.abs(v - mu);
        }
        return acc / data.size();
    }

    /**
     * 21. Median Absolute Deviation: mediana(|xᵢ – mediana|)
     */
    public static double medianAbsoluteDeviation(Signal input) {
        List<Double> data = input.getValues();
        if (data.isEmpty()) throw new IllegalArgumentException("Señal vacía");
        double med = median(input);
        List<Double> absDevs = new ArrayList<>();
        for (double v : data) {
            absDevs.add(Math.abs(v - med));
        }
        Collections.sort(absDevs);
        int n = absDevs.size();
        if (n % 2 == 1) {
            return absDevs.get(n / 2);
        } else {
            return (absDevs.get(n / 2 - 1) + absDevs.get(n / 2)) / 2.0;
        }
    }

    /**
     * 22. Energy: Σ xᵢ² (igual que sumOfSquares)
     */
    public static double energy(Signal input) {
        return sumOfSquares(input);  // reutiliza tu método existente
    }

    /**
     * 23. Entropy (Shannon): -Σ pᵢ log pᵢ, pᵢ = freqᵢ / n
     */
    public static double entropy(Signal input) {
        List<Double> data = input.getValues();
        int n = data.size();
        if (n == 0) throw new IllegalArgumentException("Señal vacía");

        // calcular frecuencias
        Map<Double, Integer> freq = new HashMap<>();
        for (double v : data) {
            freq.put(v, freq.getOrDefault(v, 0) + 1);
        }
        // calcular entropía
        double h = 0.0;
        for (int count : freq.values()) {
            double p = (double) count / n;
            h -= p * Math.log(p);
        }
        return h;
    }
    
    /**
     * Momentos de orden superior         
     */
    
    /**
     * 24. Tercer momento central:
     * Σ (xᵢ – μ)³ / n
     */
    public static double thirdCentralMoment(Signal input) {
        int n = count(input);
        if (n == 0) throw new IllegalArgumentException("Señal vacía");
        double mu = mean(input);
        double acc = 0.0;
        for (double v : input.getValues()) {
            acc += Math.pow(v - mu, 3);
        }
        return acc / n;
    }

    /**
     * 25. Cuarto momento central:
     * Σ (xᵢ – μ)⁴ / n
     */
    public static double fourthCentralMoment(Signal input) {
        int n = count(input);
        if (n == 0) throw new IllegalArgumentException("Señal vacía");
        double mu = mean(input);
        double acc = 0.0;
        for (double v : input.getValues()) {
            acc += Math.pow(v - mu, 4);
        }
        return acc / n;
    }   
    
    /**
     * Estadísticos de forma de onda (señales)        
     */
    

    /**
     * 26. Peak-to-Peak: diferencia entre valor máximo y mínimo.
     */
    public static double peakToPeak(Signal input) {
        return max(input) - min(input);
    }

    /**
     * 27. Crest Factor: relación entre pico absoluto y RMS.
     * CF = max(|x|) / RMS
     */
    public static double crestFactor(Signal input) {
        double peak = 0.0;
        for (double v : input.getValues()) {
            peak = Math.max(peak, Math.abs(v));
        }
        double rms = rms(input);
        if (rms == 0.0) throw new IllegalArgumentException("RMS cero, Crest Factor indefinido");
        return peak / rms;
    }

    /**
     * 28. Impulse Factor: relación entre pico absoluto y valor medio absoluto.
     * IF = max(|x|) / mean(|x|)
     */
    public static double impulseFactor(Signal input) {
        List<Double> data = input.getValues();
        int n = data.size();
        if (n == 0) throw new IllegalArgumentException("Señal vacía");

        double peak = 0.0;
        double sumAbs = 0.0;
        for (double v : data) {
            double abs = Math.abs(v);
            peak = Math.max(peak, abs);
            sumAbs += abs;
        }
        double meanAbs = sumAbs / n;
        if (meanAbs == 0.0) throw new IllegalArgumentException("Mean Absolute cero, Impulse Factor indefinido");
        return peak / meanAbs;
    }

    /**
     * 29. Shape Factor: relación entre RMS y valor medio absoluto.
     * SF = RMS / mean(|x|)
     */
    public static double shapeFactor(Signal input) {
        List<Double> data = input.getValues();
        int n = data.size();
        if (n == 0) throw new IllegalArgumentException("Señal vacía");
        
        double sumAbs = 0.0;
        for (double v : data) {
            sumAbs += Math.abs(v);
        }
        double meanAbs = sumAbs / n;
        double rms = rms(input);
        if (meanAbs == 0.0) throw new IllegalArgumentException("Mean Absolute cero, Shape Factor indefinido");
        return rms / meanAbs;
    }

    /**
     * 30. Clearance Factor: relación entre pico absoluto y media de la raíz cuadrada de |x|.
     * CF' = max(|x|) / mean(√|x|)
     */
    public static double clearanceFactor(Signal input) {
        List<Double> data = input.getValues();
        int n = data.size();
        if (n == 0) throw new IllegalArgumentException("Señal vacía");

        double peak = 0.0;
        double sumSqrt = 0.0;
        for (double v : data) {
            double abs = Math.abs(v);
            peak = Math.max(peak, abs);
            sumSqrt += Math.sqrt(abs);
        }
        double meanSqrt = sumSqrt / n;
        if (meanSqrt == 0.0) throw new IllegalArgumentException("Mean sqrt cero, Clearance Factor indefinido");
        return peak / meanSqrt;
    }

    /**
     * 31. Margin Factor: relación entre pico absoluto y media de la cuarta raíz de |x|.
     * MF = max(|x|) / mean(|x|^(1/4))
     */
    public static double marginFactor(Signal input) {
        List<Double> data = input.getValues();
        int n = data.size();
        if (n == 0) throw new IllegalArgumentException("Señal vacía");

        double peak = 0.0;
        double sumFourthRoot = 0.0;
        for (double v : data) {
            double abs = Math.abs(v);
            peak = Math.max(peak, abs);
            sumFourthRoot += Math.pow(abs, 0.25);
        }
        double meanFourthRoot = sumFourthRoot / n;
        if (meanFourthRoot == 0.0) throw new IllegalArgumentException("Mean fourth root cero, Margin Factor indefinido");
        return peak / meanFourthRoot;
    }

    /**
     * 32. Zero-Crossing Rate: cantidad de veces que la señal cruza por cero (signo distinto)
     * dividido por (n-1).
     */
    public static double zeroCrossingRate(Signal input) {
        List<Double> data = input.getValues();
        int n = data.size();
        if (n < 2) return 0.0;

        int crossings = 0;
        for (int i = 1; i < n; i++) {
            if ((data.get(i-1) >= 0 && data.get(i) < 0) ||
                (data.get(i-1) < 0  && data.get(i) >= 0)) {
                crossings++;
            }
        }
        return (double) crossings / (n - 1);
    }    
    
    /**
     * Series temporales y correlación         
     */
    
    /**
     * 33. Autocorrelación en el lag k:
     * r[k] = Σ_{i=0..n-k-1} (x[i]-μ)*(x[i+k]-μ) / Σ_{i=0..n-1} (x[i]-μ)^2
     */
    public static double autocorrelation(Signal input, int lag) {
        List<Double> data = input.getValues();
        int n = data.size();
        if (lag < 0 || lag >= n) throw new IllegalArgumentException("Lag fuera de rango");
        double mu = mean(input);
        double num = 0.0, den = 0.0;
        for (int i = 0; i < n; i++) {
            double d = data.get(i) - mu;
            den += d * d;
            if (i + lag < n) {
                num += d * (data.get(i + lag) - mu);
            }
        }
        return (den == 0.0) ? 0.0 : num / den;
    }

    /**
     * 34. Devuelve el array de autocorrelaciones r[0..maxLag] para la señal.
     */
    public static double[] autocorrelations(Signal input, int maxLag) {
        int n = input.size();
        if (maxLag < 0 || maxLag >= n) throw new IllegalArgumentException("maxLag fuera de rango");
        double[] result = new double[maxLag + 1];
        for (int k = 0; k <= maxLag; k++) {
            result[k] = autocorrelation(input, k);
        }
        return result;
    }

    /**
     * 35. Partial Autocorrelation Function (PACF) hasta el lag p,
     * usando el algoritmo de Durbin–Levinson.
     * Retorna φ[p][p].
     */
    public static double partialAutocorrelation(Signal input, int p) {
        double[] r = autocorrelations(input, p);
        // Durbin–Levinson recursivo
        double[] phiPrev = new double[p + 1];
        double[] phiCurr = new double[p + 1];
        double[] err    = new double[p + 1];
        err[0] = r[0];       // varianza

        for (int k = 1; k <= p; k++) {
            // calcular phi[k][k]
            double num = r[k];
            for (int j = 1; j < k; j++) {
                num -= phiPrev[j] * r[k - j];
            }
            phiCurr[k] = num / err[k - 1];
            // actualizar phi[k][j] para j<k
            for (int j = 1; j < k; j++) {
                phiCurr[j] = phiPrev[j] - phiCurr[k] * phiPrev[k - j];
            }
            // actualizar error
            err[k] = err[k - 1] * (1.0 - phiCurr[k] * phiCurr[k]);
            // preparar siguiente iteración
            System.arraycopy(phiCurr, 1, phiPrev, 1, k + 1 - 1);
        }
        return phiCurr[p];
    }

    /**
     * 36. Cross-Correlation entre dos señales en lag k:
     * ρ_xy[k] = Σ (x[i]-μx)*(y[i+k]-μy) / sqrt(Σ(x-μx)^2 * Σ(y-μy)^2)
     */
    public static double crossCorrelation(Signal x, Signal y, int lag) {
        List<Double> dx = x.getValues(), dy = y.getValues();
        int nx = dx.size(), ny = dy.size();
        if (lag < 0 || lag >= nx || lag >= ny) throw new IllegalArgumentException("Lag fuera de rango");
        double mux = mean(x), muy = mean(y);
        double num = 0.0, denx = 0.0, deny = 0.0;
        for (int i = 0; i + lag < nx && i + lag < ny; i++) {
            double vx = dx.get(i) - mux;
            double vy = dy.get(i + lag) - muy;
            num += vx * vy;
        }
        for (double v : dx) {
            double d = v - mux;
            denx += d * d;
        }
        for (double v : dy) {
            double d = v - muy;
            deny += d * d;
        }
        double denom = Math.sqrt(denx * deny);
        return (denom == 0.0) ? 0.0 : num / denom;
    }    
    
    /**
     * Ventanas móviles y suavizado         
     */    
    
    /**
     * 37. Moving Average: media móvil simple con ventana de tamaño `window`.
     * Devuelve una señal de tamaño n-window+1.
     * @param input  señal original
     * @param window tamaño de la ventana (>=1 y <= n)
     */
    public static Signal movingAverage(Signal input, int window) {
        int n = input.size();
        if (window < 1 || window > n) {
            throw new IllegalArgumentException("Window debe estar en [1, " + n + "]");
        }
        Signal result = new Signal();
        List<Double> data = input.getValues();
        // suma de la primera ventana
        double sum = 0.0;
        for (int i = 0; i < window; i++) {
            sum += data.get(i);
        }
        result.add(sum / window);
        // ventanas subsecuentes
        for (int i = window; i < n; i++) {
            sum += data.get(i) - data.get(i - window);
            result.add(sum / window);
        }
        return result;
    }

    /**
     * 38. Exponential Moving Average (EMA) con factor de suavizado alpha (0 < alpha <= 1).
     * Devuelve una señal de tamaño n.
     * @param input señal original
     * @param alpha factor de suavizado
     */
    public static Signal exponentialMovingAverage(Signal input, double alpha) {
        int n = input.size();
        if (n == 0) throw new IllegalArgumentException("Señal vacía");
        if (alpha <= 0.0 || alpha > 1.0) {
            throw new IllegalArgumentException("Alpha debe estar en (0, 1]");
        }
        Signal result = new Signal();
        List<Double> data = input.getValues();
        // inicializa EMA con el primer valor
        double ema = data.get(0);
        result.add(ema);
        // recursión EMA
        for (int i = 1; i < n; i++) {
            ema = alpha * data.get(i) + (1 - alpha) * ema;
            result.add(ema);
        }
        return result;
    }

    /**
     * 39. Moving Standard Deviation: desviación estándar móvil con ventana de tamaño `window`.
     * Devuelve una señal de tamaño n-window+1.
     * @param input  señal original
     * @param window tamaño de la ventana (>=1 y <= n)
     */
    public static Signal movingStdDev(Signal input, int window) {
        int n = input.size();
        if (window < 1 || window > n) {
            throw new IllegalArgumentException("Window debe estar en [1, " + n + "]");
        }
        List<Double> data = input.getValues();
        Signal result = new Signal();
        // para cada posición j = window-1..n-1, calcular varianza y sqrt
        for (int j = window - 1; j < n; j++) {
            double sum = 0.0, sumSq = 0.0;
            for (int k = j - window + 1; k <= j; k++) {
                double v = data.get(k);
                sum += v;
                sumSq += v * v;
            }
            double mean = sum / window;
            double var = sumSq / window - mean * mean;
            result.add(Math.sqrt(var));
        }
        return result;
    }
    
    /**
     * 40. Moving Median: mediana móvil con ventana de tamaño window.
     * Señal de salida de longitud n–window+1.
     */
    public static Signal movingMedian(Signal input, int window) {
        int n = input.size();
        if (window < 1 || window > n) {
            throw new IllegalArgumentException("Window debe estar en [1, " + n + "]");
        }
        Signal result = new Signal();
        List<Double> data = input.getValues();
        Deque<Double> windowList = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            windowList.addLast(data.get(i));
            if (windowList.size() > window) {
                windowList.removeFirst();
            }
            if (windowList.size() == window) {
                List<Double> sorted = new ArrayList<>(windowList);
                Collections.sort(sorted);
                int m = window / 2;
                if (window % 2 == 1) {
                    result.add(sorted.get(m));
                } else {
                    result.add((sorted.get(m-1) + sorted.get(m)) / 2.0);
                }
            }
        }
        return result;
    }

    /**
     * 41. Weighted Moving Average (WMA). Pesa cada punto con weights[i].
     * weights.length == window.
     */
    public static Signal weightedMovingAverage(Signal input, double[] weights) {
        int window = weights.length;
        int n = input.size();
        if (window < 1 || window > n) {
            throw new IllegalArgumentException("Window debe estar en [1," + n + "]");
        }
        double weightSum = 0;
        for(double w: weights) weightSum += w;

        Signal result = new Signal();
        List<Double> data = input.getValues();
        for (int i = 0; i <= n - window; i++) {
            double sum = 0;
            for (int j = 0; j < window; j++) {
                sum += data.get(i + j) * weights[j];
            }
            result.add(sum / weightSum);
        }
        return result;
    }

    /**
     * 42. Triangular Moving Average: WMA con pesos triangulares [1,2,…,k,…,2,1].
     */
    public static Signal triangularMovingAverage(Signal input, int window) {
        if (window % 2 == 0) {
            throw new IllegalArgumentException("Triangular MA requiere window impar");
        }
        int k = window / 2;
        // construir pesos [1,2,…,k+1,…,2,1]
        double[] weights = new double[window];
        for (int i = 0; i <= k; i++) {
            weights[i] = i + 1;
            weights[window - 1 - i] = i + 1;
        }
        return weightedMovingAverage(input, weights);
    }

    /**
     * 43. Gaussian Smoothing: convolución con núcleo gaussiano de desviación sigma.
     * Crea un kernel de tamaño m = 2*ceil(3*sigma)+1.
     */
    public static Signal gaussianSmoothing2(Signal input, double sigma) {
        int radius = (int)Math.ceil(3 * sigma);
        int m = 2 * radius + 1;
        double[] kernel = new double[m];
        double sum = 0;
        for (int i = -radius; i <= radius; i++) {
            double v = Math.exp(-0.5 * i*i / (sigma*sigma));
            kernel[i + radius] = v;
            sum += v;
        }
        // normalizar kernel
        for (int i = 0; i < m; i++) {
            kernel[i] /= sum;
        }
        // aplicar WMA con kernel
        return weightedMovingAverage(input, kernel);
    }
    
    public static Signal gaussianSmoothing(Signal input, double sigma) {
        List<Double> data = input.getValues();
        int n = data.size();
        int radius = (int) Math.ceil(3 * sigma);
        int m = 2 * radius + 1;

        // Si la señal es más corta que el kernel, devolvemos la señal sin cambios
        if (m > n) {
            return new Signal(data);
        }

        // Construcción del kernel Gaussiano
        double[] kernel = new double[m];
        double sum = 0;
        for (int i = -radius; i <= radius; i++) {
            double v = Math.exp(-0.5 * i * i / (sigma * sigma));
            kernel[i + radius] = v;
            sum += v;
        }
        for (int i = 0; i < m; i++) {
            kernel[i] /= sum;
        }

        // Aplicar WMA con el kernel normalizado
        return weightedMovingAverage(input, kernel);
    }
    

    /**
     * 44. Savitzky–Golay Filter simplificado (polinomio de orden 2).
     * Aquí solo un ejemplo de kernel para window=5, order=2.
     */
    public static Signal savitzkyGolay5(Signal input) {
        // coeficientes precalculados para window=5, order=2
        double[] kernel = { -3.0/35, 12.0/35, 17.0/35, 12.0/35, -3.0/35 };
        return weightedMovingAverage(input, kernel);
    }    
    
}
