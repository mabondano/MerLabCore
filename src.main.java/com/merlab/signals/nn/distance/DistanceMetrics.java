package com.merlab.signals.nn.distance;

import java.util.List;
import com.merlab.signals.core.Signal;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.IntStream;

/**
 * Colección de métodos estáticos para calcular diversas métricas de distancia.
 * Cada método recibe dos vectores (arrays de double) o bien Signals (listas de Double)
 * y devuelve un double que representa la distancia entre ellos.
 */
public final class DistanceMetrics {

    private DistanceMetrics() { /* constructor privado para evitar instanciar */ }

    /**
     * Distancia Euclidiana (L2):
     *   d(a,b) = sqrt( sum_i (a_i - b_i)^2 ).
     */
    public static double euclidean(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double d = a[i] - b[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }

    /**
     * Distancia Manhattan (L1):
     *   d(a,b) = sum_i |a_i - b_i|.
     */
    public static double manhattan(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.abs(a[i] - b[i]);
        }
        return sum;
    }

    /**
     * Distancia de Chebyshev (L∞):
     *   d(a,b) = max_i |a_i - b_i|.
     */
    public static double chebyshev(double[] a, double[] b) {
        double max = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = Math.abs(a[i] - b[i]);
            if (diff > max) {
                max = diff;
            }
        }
        return max;
    }

    /**
     * Distancia de Minkowski de orden p:
     *   d(a,b) = ( sum_i |a_i - b_i|^p )^(1/p).
     *  - Si p=1, es la Manhattan.
     *  - Si p=2, es la Euclidiana.
     */
    public static double minkowski(double[] a, double[] b, double p) {
        if (p <= 0) {
            throw new IllegalArgumentException("Parametro p debe ser > 0");
        }
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(Math.abs(a[i] - b[i]), p);
        }
        return Math.pow(sum, 1.0 / p);
    }

    /**
     * Distancia de Mahalanobis entre vectores a y b, dada la matriz de covarianza invertida invCov.
     *   d(a,b) = sqrt( (a-b)^T · invCov · (a-b) ).
     * Se asume que invCov es la matriz inversa de la covarianza del dataset.
     */
    public static double mahalanobis(double[] a, double[] b, double[][] invCov) {
        int d = a.length;
        double[] diff = new double[d];
        for (int i = 0; i < d; i++) {
            diff[i] = a[i] - b[i];
        }
        // Primero: temp = invCov · diff  (multiplicación matricial)
        double[] temp = new double[d];
        for (int i = 0; i < d; i++) {
            double s = 0.0;
            for (int j = 0; j < d; j++) {
                s += invCov[i][j] * diff[j];
            }
            temp[i] = s;
        }
        // Luego: (a-b)^T · temp
        double sum = 0.0;
        for (int i = 0; i < d; i++) {
            sum += diff[i] * temp[i];
        }
        return Math.sqrt(sum);
    }

    /**
     * Distancia de Coseno (1 - similitud de coseno):
     *   d(a,b) = 1 - ( (a·b) / (||a|| * ||b||) ).
     */
    public static double cosine(double[] a, double[] b) {
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) {
            return 1.0;  // si uno de los vectores es cero, se considera distancia máxima
        }
        return 1.0 - (dot / (Math.sqrt(normA) * Math.sqrt(normB)));
    }

    /**
     * Distancia de Hamming:
     *  - Para vectores binarios o discretos, cuenta en cuántas posiciones difiere.
     *  - Aquí se asume que a y b contienen valores discretos (ej. 0.0, 1.0, 2.0, etc.).
     */
    public static double hamming(double[] a, double[] b) {
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                count++;
            }
        }
        return (double) count;
    }

    /**
     * Distancia de Canberra:
     *   d(a,b) = sum_i ( |a_i - b_i| / (|a_i| + |b_i|) ), con convención 0/0 = 0.
     */
    public static double canberra(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double num = Math.abs(a[i] - b[i]);
            double den = Math.abs(a[i]) + Math.abs(b[i]);
            if (den == 0) {
                continue; // cuando ambos son cero, consideramos contribución = 0
            }
            sum += num / den;
        }
        return sum;
    }

    /**
     * Distancia de Bray–Curtis (solo para vectores no negativos):
     *   d(a,b) = ( sum_i |a_i - b_i| ) / ( sum_i (a_i + b_i) ).
     *  Se asume sum(a_i + b_i) > 0.
     */
    public static double brayCurtis(double[] a, double[] b) {
        double num = 0.0, den = 0.0;
        for (int i = 0; i < a.length; i++) {
            num += Math.abs(a[i] - b[i]);
            den += (a[i] + b[i]);
        }
        if (den == 0) {
            return 0.0; // si todos los valores son 0 en ambos vectores, se consideran idénticos
        }
        return num / den;
    }

    /**
     * Distancia de Jaccard (para vectores binarios o conjuntos representados como arrays de 0/1):
     *   d(a,b) = 1 - ( |A ∩ B| / |A ∪ B| ).
     *  Aquí interpretamos a_i y b_i como “0 o 1” para simular conjuntos.
     */
    public static double jaccard(double[] a, double[] b) {
        int intersection = 0, union = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != 0 || b[i] != 0) {
                union++;
                if (a[i] == b[i]) {
                    intersection++;
                }
            }
        }
        if (union == 0) {
            return 0.0;
        }
        return 1.0 - ((double) intersection / union);
    }

    /**
     * Distancia de Sørensen–Dice (para vectores binarios o conjuntos como arrays de 0/1):
     *   d(a,b) = 1 - ( 2 * |A ∩ B| ) / ( |A| + |B| ).
     */
    public static double sorensenDice(double[] a, double[] b) {
        int intersection = 0, sizeA = 0, sizeB = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != 0) sizeA++;
            if (b[i] != 0) sizeB++;
            if (a[i] != 0 && b[i] != 0) intersection++;
        }
        if (sizeA + sizeB == 0) {
            return 0.0;
        }
        return 1.0 - (2.0 * intersection / (sizeA + sizeB));
    }

    /**
     * Distancia de Pearson (1 - correlación):
     *   d(a,b) = 1 - ( cov(a,b) / (σ_a * σ_b) ).
     *  Donde cov(a,b) es la covarianza muestral y σ_a, σ_b las desviaciones estándar.
     */
    public static double pearson(double[] a, double[] b) {
        int n = a.length;
        double meanA = 0.0, meanB = 0.0;
        for (int i = 0; i < n; i++) {
            meanA += a[i];
            meanB += b[i];
        }
        meanA /= n;
        meanB /= n;

        double cov = 0.0, varA = 0.0, varB = 0.0;
        for (int i = 0; i < n; i++) {
            double da = a[i] - meanA;
            double db = b[i] - meanB;
            cov   += da * db;
            varA  += da * da;
            varB  += db * db;
        }
        if (varA == 0 || varB == 0) {
            return 1.0; // si una varianza es cero, definimos distancia máxima
        }
        double corr = cov / (Math.sqrt(varA) * Math.sqrt(varB));
        return 1.0 - corr;
    }


    /**
     * Enum interno para escoger la métrica por nombre.
     */
    public enum Metric {
        EUCLIDEAN,
        MANHATTAN,
        CHEBYSHEV,
        MINKOWSKI,      // requerirá parámetro p (ver sobrecarga más abajo)
        MAHALANOBIS,    // requerirá invCovariana
        COSINE,
        HAMMING,
        CANBERRA,
        BRAY_CURTIS,
        JACCARD,
        SORENSEN_DICE,
        PEARSON
    }

    /**
     * “Dispatcher” genérico que elige la métrica según el parámetro metric.
     * — Para EUCLIDEAN, MANHATTAN, CHEBYSHEV, COSINE, HAMMING, CANBERRA, BRAY_CURTIS, JACCARD,
     *   SORENSEN_DICE, PEARSON: usa directamente la implementación sin parámetros extra.
     * — Para MINKOWSKI: necesita un parámetro adicional p. El método invoca minkowski(a,b,p).
     * — Para MAHALANOBIS: necesita la matriz inversa de covarianza invCov.  
     *
     * @param metric   qué tipo de distancia usar
     * @param a        primer vector
     * @param b        segundo vector
     * @param params   parámetros extra (p para Minkowski; invCov para Mahalanobis).
     * @return distancia calculada
     */
    public static double computeDistance(
            Metric metric,
            double[] a,
            double[] b,
            Object... params
    ) {
        switch (metric) {
            case EUCLIDEAN:
                return euclidean(a, b);
            case MANHATTAN:
                return manhattan(a, b);
            case CHEBYSHEV:
                return chebyshev(a, b);
            case COSINE:
                return cosine(a, b);
            case HAMMING:
                return hamming(a, b);
            case CANBERRA:
                return canberra(a, b);
            case BRAY_CURTIS:
                return brayCurtis(a, b);
            case JACCARD:
                return jaccard(a, b);
            case SORENSEN_DICE:
                return sorensenDice(a, b);
            case PEARSON:
                return pearson(a, b);
            case MINKOWSKI:
                if (params.length < 1 || !(params[0] instanceof Number)) {
                    throw new IllegalArgumentException(
                        "Minkowski requiere un parámetro p (un Double)."
                    );
                }
                double p = ((Number) params[0]).doubleValue();
                return minkowski(a, b, p);
            case MAHALANOBIS:
                if (params.length < 1 || !(params[0] instanceof double[][])) {
                    throw new IllegalArgumentException(
                        "Mahalanobis requiere la matriz inversa de covarianza (double[][])."
                    );
                }
                double[][] invCov = (double[][]) params[0];
                return mahalanobis(a, b, invCov);
            default:
                throw new IllegalArgumentException("Métrica no soportada: " + metric);
        }
    }

    /**
     * Versión auxiliar que recibe dos Signals (listas de Double) en lugar de dos arrays de double.
     */
    public static double computeDistance(
            Metric metric,
            Signal sa,
            Signal sb,
            Object... params
    ) {
        // Convertimos a array primitivo
        double[] a = sa.getValues().stream().mapToDouble(d -> d).toArray();
        double[] b = sb.getValues().stream().mapToDouble(d -> d).toArray();
        return computeDistance(metric, a, b, params);
    }
}
