// src/main/java/com/merlab/signals/nn/trainer/LinearRegressionTrainer4.java
package com.merlab.signals.nn.trainer.simple;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.model.RegressionModel;
import com.merlab.signals.nn.model.SimpleRegressionModel;
import com.merlab.signals.nn.processor.LinearProcessor;
import com.merlab.signals.nn.processor.SimpleLinearProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Trainer que sólo devuelve parámetros de regresión,
 * sin ligar la inferencia a un tipo de processor.
 */
//public class LinearRegressionTrainer4 implements Trainer<LinearProcessor4> {
//public class LinearRegressionTrainer4 implements ModelTrainer  {
public class SimpleLinearRegressionTrainer4 implements SimpleRegressionTrainer {
	
	@Override
    public SimpleRegressionModel train(
            List<Signal> inputs,
            List<Signal> targets,
            Path modelOutputPath
        ) {
            int n = inputs.size();
            int m = inputs.get(0).getValues().size();
            double[][] X = new double[n][m];
            double[]   y = new double[n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    X[i][j] = inputs.get(i).getValues().get(j);
                }
                y[i] = targets.get(i).getValues().get(0);
            }

            OLSMultipleLinearRegression reg = new OLSMultipleLinearRegression();
            reg.newSampleData(y, X);
            double[] params    = reg.estimateRegressionParameters();
            double   intercept = params[0];
            double[] weights   = Arrays.copyOfRange(params, 1, params.length);

            // Guardar modelo en fichero
            try {
                String data = "intercept=" + intercept +
                              ";weights=" + Arrays.toString(weights);
                Files.writeString(modelOutputPath, data);
            } catch (Exception e) {
                throw new RuntimeException("Error guardando modelo", e);
            }

            //return new RegressionModel(weights, intercept);
           return new SimpleRegressionModel(weights, intercept);
        }
	
	
    // FIRMA NUEVA (para pipelines genéricos)
	/*
    @Override
    public NeuralNetworkProcessor train(
        List<Signal> inputs,
        List<Signal> targets,
        Path modelOutputPath
    ) {
        int n = inputs.size();
        int m = inputs.get(0).getValues().size();
        double[][] X = new double[n][m];
        double[]   y = new double[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                X[i][j] = inputs.get(i).getValues().get(j);
            }
            y[i] = targets.get(i).getValues().get(0);
        }

        OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();
        ols.newSampleData(y, X);
        double[] params    = ols.estimateRegressionParameters();
        double   intercept = params[0];
        double[] weights   = Arrays.copyOfRange(params, 1, params.length);

        // Guardar modelo en texto
        try {
            String modelData = "intercept=" + intercept +
                               ";weights=" + Arrays.toString(weights);
            Files.writeString(modelOutputPath, modelData);
        } catch (Exception e) {
            throw new RuntimeException("Error guardando modelo", e);
        }

        // Devolver un procesador que aplica la regresión lineal
        return new LinearProcessor4(weights, intercept);
    }
	
*/
 

}
