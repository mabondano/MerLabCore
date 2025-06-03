package com.merlab.nn.examples;

import com.merlab.signals.data.DataLoader;
import com.merlab.signals.data.DataLoaderConfig;
import com.merlab.signals.data.DataLoaderFactory;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.processor.*;
import com.merlab.signals.nn.trainer.BackpropMLPTrainer;
import com.merlab.signals.nn.trainer.MLPTrainer;
import com.merlab.signals.plot.PlotlyBrowserViewer;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Ejemplo de Deep Feedforward Network para regresión multivariable:
 * predicción del precio de una casa a partir de 3 características:
 *  - superficie (sqft)
 *  - número de habitaciones
 *  - antigüedad de la vivienda
 *
 * Usa BatchNorm y Dropout entre capas ocultas.
 */


import com.merlab.signals.data.*;
import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.trainer.*;

import java.util.stream.*;

public class HousePriceRegressionExample {

    public static void main(String[] args) throws Exception {
        // 1) Cargar CSV: asumimos que el CSV está en resources/data/house_prices_large.csv
        DataLoaderConfig cfg = new DataLoaderConfig();
        cfg.setCsvPath("src/main/resources/data/house_prices_large.csv");
        cfg.setNumInputs(3);
        cfg.setNumTargets(1);
        DataLoader loader = DataLoaderFactory.create(DataLoaderFactory.Type.CSV, cfg);
        DataSet ds = loader.load();
        
        // después de cargar el DataSet:
        /*
        StandardScaler scalerX = new StandardScaler();
        scalerX.fit(ds.getInputs());        // calcula media+desviación
        List<Signal> normX = scalerX.transform(ds.getInputs());
        StandardScaler scalerY = new StandardScaler();
        scalerY.fit(ds.getTargets());
        List<Signal> normY = scalerY.transform(ds.getTargets());
        DataSet dsNorm = new DataSet(normX, normY);
         */
        // 2) Definir la red: 3 → 32(ReLU)→BN→DO→16(ReLU)→BN→DO→1(Identity)
        Random rnd = new Random(123);
        Layer in1   = initLayer(32, 3, rnd, ActivationFunctions.RELU);
        Layer bn1   = new BatchNormLayer(32);
        Layer do1   = new DropoutLayer(0.8, 42L, 32);

        Layer in2   = initLayer(16, 32, rnd, ActivationFunctions.RELU);
        Layer bn2   = new BatchNormLayer(16);
        Layer do2   = new DropoutLayer(0.8, 42L, 16);

        Layer out   = initLayer(1, 16, rnd, ActivationFunctions.IDENTITY);

        MultiLayerPerceptronProcessor mlp = new MultiLayerPerceptronProcessor(
            List.of(in1, bn1, do1, in2, bn2, do2, out)
            //List.of(in1, in2, out)
        );

        // 3) Entrenamiento con Backprop, mini‐batches
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        int epochs = 3000;
        double lr = 0.01;
        int batchSize = 16;

        int N = ds.getInputs().size();
        List<Integer> idx = IntStream.range(0, N).boxed().collect(Collectors.toList());

        for (int e = 1; e <= epochs; e++) {
            mlp.setMode(Mode.TRAIN);
            Collections.shuffle(idx, rnd);
            for (int i = 0; i < N; i += batchSize) {
                int end = Math.min(N, i + batchSize);
                List<Signal> inB = ds.getInputs().subList(i, end);
                List<Signal> tgB = ds.getTargets().subList(i, end);
                DataSet batch = new DataSet(inB, tgB);
                mlp = trainer.train(mlp, batch, 1, lr);
            }
            if (e % 500 == 0 || e == 1 || e == epochs) {
                double mse = computeMSE(mlp, ds);
                System.out.printf("Epoch %4d/%d  lr=%.3f  MSE=%.4f%n", e, epochs, lr, mse);
            }
        }

        // 4) Evaluación final
        mlp.setMode(Mode.INFERENCE);
        double finalMse = computeMSE(mlp, ds);
        System.out.printf("MSE final: %.4f%n", finalMse);

        // 5) Reporte del modelo
        ModelInfo info = new ModelInfo.Builder("HousePrice MLP 3→32→16→1")
            .addLayer(3, 32, "ReLU + BatchNorm + Dropout(0.8)")
            .addLayer(32, 16, "ReLU + BatchNorm + Dropout(0.8)")
            .addLayer(16, 1,  "Identity")
            .epochs(epochs)
            .learningRate(lr)
            .mse(finalMse)
            .build();
        ModelReporter.report(info);

        // 6) Extraer índices, valores reales y predichos
        List<Integer> order = IntStream.range(0, N)
            .boxed()
            .sorted(Comparator.comparing(i -> ds.getInputs().get(i).getValues().get(0)))
            .collect(Collectors.toList());

        List<Double> xData = new ArrayList<>(N), 
                       yReal = new ArrayList<>(N), 
                       yPred = new ArrayList<>(N);
        for (int i : order) {
            xData.add((double)xData.size());
            yReal.add(ds.getTargets().get(i).getValues().get(0));
            yPred.add(mlp.predict(ds.getInputs().get(i)).getValues().get(0));
        }

        // --- Verificación extra: asegurar mapeo 1:1 entre inputs y preds ---
        List<Double> yPredCheck = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            Signal in  = ds.getInputs().get(i);
            double hat = mlp.predict(in).getValues().get(0);
            yPredCheck.add(hat);
        }
        System.out.println("Verif. primeros 5 reales:   " + yReal.subList(0,5));
        System.out.println("Verif. primeros 5 predichos:" + yPredCheck.subList(0,5));

        // 7) Generar HTML inline para Plotly
        String html = """
            <html>
            <head>
              <script src="plotly.min.js"></script>
            </head>
            <body>
              <div id="chart" style="width:800px;height:600px;"></div>
              <script>
                const x     = %s;
                const yReal = %s;
                const yPred = %s;
                const trace1 = { x:x, y:yReal, mode:'lines+markers', name:'Precio Real', line:{color:'blue'} };
                const trace2 = { x:x, y:yPred, mode:'lines+markers', name:'Predicción',   line:{color:'red'}  };
                Plotly.newPlot('chart',[trace1, trace2],{
                  title:'Regresión Precio Casas (MLP)',
                  xaxis:{title:'Índice'},
                  yaxis:{title:'Precio'}
                });
              </script>
            </body>
            </html>
            """.formatted(
                xData.toString(),
                yReal.toString(),
                yPred.toString()
            );

        // 8) Mostrar en el navegador
        PlotlyBrowserViewer.showInBrowser2(html);
    }

    private static double computeMSE(MultiLayerPerceptronProcessor mlp, DataSet ds) {
        double sum = 0;
        int N = ds.getInputs().size();
        for (int i = 0; i < N; i++) {
            double yTrue = ds.getTargets().get(i).getValues().get(0);
            double yHat  = mlp.predict(ds.getInputs().get(i)).getValues().get(0);
            sum += Math.pow(yTrue - yHat, 2);
        }
        return sum / N;
    }

    private static Layer initLayer(int neurons, int inputs, Random rnd, ActivationFunction act) {
        double[][] w = new double[neurons][inputs];
        double[]   b = new double[neurons];
        for (int i = 0; i < neurons; i++) {
            b[i] = (rnd.nextDouble()*2 - 1)*0.1;
            for (int j = 0; j < inputs; j++) {
                w[i][j] = (rnd.nextDouble()*2 - 1)*0.1;
            }
        }
        return new Layer(w, b, act);
    }
}
