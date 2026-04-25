package com.merlab.nn.examples;

import com.merlab.signals.data.DataLoader;
import com.merlab.signals.data.DataLoaderConfig;
import com.merlab.signals.data.DataLoaderFactory;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.BatchNormLayer;
import com.merlab.signals.nn.processor.DropoutLayer;
import com.merlab.signals.nn.trainer.BackpropMLPTrainer;
import com.merlab.signals.nn.trainer.MLPTrainer;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HousePriceRegressionExample12b {

    public static void main(String[] args) throws Exception {
        // 1) Cargar dataset CSV
        DataLoaderConfig cfg = new DataLoaderConfig();
        cfg.setCsvPath("src/main/resources/data/house_prices_large.csv");
        cfg.setNumInputs(3);
        cfg.setNumTargets(1);
        DataLoader loader = DataLoaderFactory.create(
            DataLoaderFactory.Type.CSV, cfg
        );
        DataSet ds = loader.load();

        // 2) Definir MLP con BatchNorm+Dropout, usando initLayer(...) para densas
        Random rnd = new Random(42);

        // capa 1: densa 3→32 + ReLU
        Layer hidden1 = initLayer(32, 3, rnd, ActivationFunctions.RELU);
        Layer bn1     = new BatchNormLayer(32);
        Layer do1     = new DropoutLayer(0.8, 123, 32);

        // capa 2: densa 32→16 + ReLU
        Layer hidden2 = initLayer(16, 32, rnd, ActivationFunctions.RELU);
        Layer bn2     = new BatchNormLayer(16);
        Layer do2     = new DropoutLayer(0.8, 123, 16);

        // capa de salida: densa 16→1 + identidad
        Layer output  = initLayer(1, 16, rnd, ActivationFunctions.IDENTITY);

        var mlp = new MultiLayerPerceptronProcessor(
            List.of(hidden1, bn1, do1,
                    hidden2, bn2, do2,
                    output)
        );

        // 3) Entrenar
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        int epochs    = 3000;
        int batchSize = 32;
        double lr     = 0.01;
        int n = ds.getInputs().size();
        List<Integer> indices = IntStream.range(0, n).boxed().collect(Collectors.toList());

        for (int e = 1; e <= epochs; e++) {
            Collections.shuffle(indices, rnd);
            for (int i = 0; i < n; i += batchSize) {
                int end = Math.min(i + batchSize, n);
                // construir sub-batch de rows
                double[][] batch = new double[end - i][4]; // 3 inputs + 1 target
                for (int j = i; j < end; j++) {
                    int idx = indices.get(j);
                    // combinamos inputs y target para DataSetBuilder
                    List<Double> in  = ds.getInputs().get(idx).getValues();
                    List<Double> tg  = ds.getTargets().get(idx).getValues();
                    batch[j - i][0] = in.get(0);
                    batch[j - i][1] = in.get(1);
                    batch[j - i][2] = in.get(2);
                    batch[j - i][3] = tg.get(0);
                }
                DataSet sub = DataSetBuilder.fromArray(batch, 1);
                mlp = trainer.train(mlp, sub, 1, lr);
            }
            if (e % 500 == 0) {
                double mse = computeMSE(mlp, ds);
                System.out.printf("Epoch %4d: MSE=%.4f%n", e, mse);
            }
        }

        // 4) Extraer índices, valores reales y predichos
        List<Integer> order = IntStream.range(0, n)
            .boxed()
            .sorted(Comparator.comparing(i -> ds.getInputs().get(i).getValues().get(0)))
            .collect(Collectors.toList());

        List<Double> xData = new ArrayList<>(n), yReal = new ArrayList<>(n), yPred = new ArrayList<>(n);
        for (int i : order) {
            // para eje X usamos simplemente el índice
            xData.add((double)xData.size());
            yReal.add(ds.getTargets().get(i).getValues().get(0));
            yPred.add(mlp.predict(ds.getInputs().get(i)).getValues().get(0));
        }
        
        //Verification
        System.out.println("Primeros 5 reales:   " + yReal.subList(0,5));
        System.out.println("Primeros 5 predichos:" + yPred.subList(0,5));

        // 5) Generar HTML inline para Plotly
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

        // 6) Mostrar en el navegador
        PlotlyBrowserViewer.showInBrowser(html);
    }

    // constructor helper para capas densas
    private static Layer initLayer(int neurons, int inputs, Random rnd,
                                   com.merlab.signals.nn.processor.ActivationFunction act) {
        double[][] w = new double[neurons][inputs];
        double[]   b = new double[neurons];
        for (int i = 0; i < neurons; i++) {
            b[i] = (rnd.nextDouble() * 2 - 1) * 0.1;
            for (int j = 0; j < inputs; j++) {
                w[i][j] = (rnd.nextDouble() * 2 - 1) * 0.1;
            }
        }
        return new Layer(w, b, act);
    }

    // cálculo de MSE en modo inferencia
    private static double computeMSE(MultiLayerPerceptronProcessor mlp, DataSet ds) {
        double sum = 0;
        int    m   = ds.getInputs().size();
        for (int i = 0; i < m; i++) {
            double yTrue = ds.getTargets().get(i).getValues().get(0);
            double yHat  = mlp.predict(ds.getInputs().get(i)).getValues().get(0);
            sum += (yTrue - yHat) * (yTrue - yHat);
        }
        return sum / m;
    }
}
