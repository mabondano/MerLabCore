// src/main/java/com/merlab/signals/nn/trainer/LinearRegressionTrainer.java
package com.merlab.signals.nn.trainer.simple;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.SimplePerceptronProcessor;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import com.merlab.signals.nn.processor.LinearProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Entrenador basado en regresión lineal múltiple.
 * Calcula coeficientes y crea un SimplePerceptronProcessor con ellos.
 */
public class SimpleLinearRegressionTrainer implements SimpleModelTrainer {

    @Override
    public NeuralNetworkProcessor train(
        List<Signal> inputs,
        List<Signal> targets,
        Path modelOutputPath
    ) {
        int n = inputs.size();
        int features = inputs.get(0).getValues().size();
        double[][] X = new double[n][features];
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < features; j++) {
                X[i][j] = inputs.get(i).getValues().get(j);
            }
            // Asumimos target de dimensión 1
            y[i] = targets.get(i).getValues().get(0);
        }

        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(y, X);
        double[] params = regression.estimateRegressionParameters();
        double intercept = params[0];
        double[] weights = Arrays.copyOfRange(params, 1, params.length);

        // Guardar modelo en texto (por ejemplo JSON o propio)
        try {
            String modelData = "intercept=" + intercept +
                               ";weights=" + Arrays.toString(weights);
            Files.writeString(modelOutputPath, modelData);
        } catch (Exception e) {
            throw new RuntimeException("Error guardando modelo", e);
        }

        // Devolvemos la implementación que queramos
        // Devolvemos un perceptrón simple que use estos coeficientes
        //return new SimplePerceptronProcessor(weights, intercept);
        return new LinearProcessor(weights, intercept);
    }
}
