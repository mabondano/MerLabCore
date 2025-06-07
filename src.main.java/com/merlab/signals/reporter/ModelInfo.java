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

package com.merlab.signals.reporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Contiene la información a reportar de un modelo entrenado:
 * - Nombre
 * - Capas (para redes neuronales)
 * - Épocas, learningRate
 * - Métricas (mse, accuracy, r2)
 * - Para clustering: lista de centroides
 */
public class ModelInfo {
    private final String modelName;
    private final List<LayerInfo> layers;
    private final int    epochs;
    private final double learningRate;
    private final Double accuracy;   // null si no aplica
    private final double mse;
    private final Double r2;         // null si no aplica
    private final List<double[]> centroids; // puede ser lista vacía
    private final Integer maxIterations;     // Puede ser null si no aplica

    private ModelInfo(Builder b) {
        this.modelName    = b.modelName;
        this.layers       = List.copyOf(b.layers);
        this.epochs       = b.epochs;
        this.learningRate = b.learningRate;
        this.accuracy     = b.accuracy;
        this.mse          = b.mse;
        this.r2           = b.r2;
        this.centroids    = List.copyOf(b.centroids);
        this.maxIterations   = b.maxIterations;
    }

    /** Nombre descriptivo del modelo */
    public String getModelName() {
        return modelName;
    }

    /** Capas (solo para redes neuronales) */
    public List<LayerInfo> getLayers() {
        return layers;
    }

    /** Número de épocas (si aplica) */
    public int getEpochs() {
        return epochs;
    }

    /** Tasa de aprendizaje (si aplica) */
    public double getLearningRate() {
        return learningRate;
    }

    /** MSE final (si aplica) */
    public double getMse() {
        return mse;
    }

    /** ¿Se definió accuracy? */
    public boolean hasAccuracy() {
        return accuracy != null;
    }

    /** Devuelve accuracy (puede ser null) */
    public Double getAccuracy() {
        return accuracy;
    }

    /** ¿Se definió R²? */
    public boolean hasR2() {
        return r2 != null;
    }

    /** Devuelve R² (puede ser null) */
    public Double getR2() {
        return r2;
    }

    /** ¿Hay centroides? */
    public boolean hasCentroids() {
        return !centroids.isEmpty();
    }

    /** Devuelve la lista de centroides (cada double[] es un vector) */
    public List<double[]> getCentroids() {
        return centroids;
    }

    /**
     * @return máximo número de iteraciones usadas (p.ej. en KMeans/KMedians) o null si no aplica.
     */
    public Integer getMaxIterations() {
        return maxIterations;
    }

    /**
     * Builder para ModelInfo
     */
    public static class Builder {
        private final String modelName;
        private final List<LayerInfo> layers = new ArrayList<>();
        private int    epochs;
        private double learningRate;
        private Double accuracy;
        private double mse;
        private Double r2;  // ahora Double para poder quedar en null
        private final List<double[]> centroids = new ArrayList<>(); // inicializada aquí
        private Integer maxIterations;

        /**
         * @param modelName Nombre descriptivo del modelo (p.ej. "KMeans k=2")
         */
        public Builder(String modelName) {
            this.modelName = modelName;
        }

        /**
         * Añade una capa (solo para redes neuronales)
         * @param in  número de entradas
         * @param out número de salidas
         * @param activation Descripción de la activación (p.ej. "ReLU", "Sigmoid")
         */
        public Builder addLayer(int in, int out, String activation) {
            layers.add(new LayerInfo(in, out, activation));
            return this;
        }

        /** Indica número de épocas */
        public Builder epochs(int e) {
            this.epochs = e;
            return this;
        }

        /** Indica learningRate */
        public Builder learningRate(double lr) {
            this.learningRate = lr;
            return this;
        }

        /** Indica accuracy (por ejemplo para clasificación) */
        public Builder accuracy(double acc) {
            this.accuracy = acc;
            return this;
        }

        /** Indica MSE (por ejemplo para regresión) */
        public Builder mse(double mse) {
            this.mse = mse;
            return this;
        }

        /** Indica R² (por ejemplo para regresión) */
        public Builder r2(double r2) {
            this.r2 = r2;
            return this;
        }

        /**
         * Añade un centroide (para clustering).
         * Se recibe un arreglo de dobles (una coordenada), se clona
         * y se almacena en la lista interna.
         * @param centroid vector de coordenadas del centroide
         */
        public Builder addClusterCentroid(double[] centroid) {
            if (centroid == null) {
                throw new IllegalArgumentException("El arreglo de centroide no puede ser null");
            }
            double[] copy = new double[centroid.length];
            System.arraycopy(centroid, 0, copy, 0, centroid.length);
            this.centroids.add(copy);
            return this;
        }
        
        /**
         * Fija el número máximo de iteraciones usado (por ejemplo, en KMeans/KMedians).
         */
        public Builder maxIterations(int maxIter) {
            this.maxIterations = maxIter;
            return this;
        }

        /** Construye el objeto ModelInfo a partir de este builder */
        public ModelInfo build() {
            return new ModelInfo(this);
        }
    }


    /**
     * Información de una capa (puede quedar vacía si no es red neuronal)
     */
    public static class LayerInfo {
        public final int inputs, outputs;
        public final String activation;
        public LayerInfo(int inputs, int outputs, String activation) {
            this.inputs = inputs;
            this.outputs = outputs;
            this.activation = activation;
        }
    }
}
