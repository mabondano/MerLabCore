package com.merlab.nn.examples;

import com.merlab.signals.data.DataLoader;
import com.merlab.signals.data.DataLoaderConfig;
import com.merlab.signals.data.DataLoaderFactory;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.processor.*;
import com.merlab.signals.nn.trainer.BackpropMLPTrainer;
import com.merlab.signals.nn.trainer.MLPTrainer;
import com.merlab.signals.plot.PlotlyBrowserViewer;
import com.merlab.signals.preprocessing.StandardScaler;
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

import com.merlab.signals.data.*;
import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.trainer.*;

import java.util.stream.*;


/**
 * Ejemplo de Deep Feedforward Network para regresión multivariable:
 * predicción del precio de una casa a partir de 3 características:
 *  - superficie (sqft)
 *  - número de habitaciones
 *  - antigüedad de la vivienda
 *
 * Usa BatchNorm y Dropout entre capas ocultas.
 */


/**
 * Ejemplo completo:
 * - Carga CSV
 * - Normaliza con StandardScaler
 * - Entrena MLP (3→32→16→1)
 * - Desnormaliza y grafica inline con Plotly
 */
public class HousePriceRegressionExample12 {

    public static void main(String[] args) throws Exception {
        // 1) Cargar CSV
        DataLoaderConfig cfg = new DataLoaderConfig();
        cfg.setCsvPath("src/main/resources/data/house_prices_large.csv");
        cfg.setNumInputs(3);
        cfg.setNumTargets(1);
        DataLoader loader = DataLoaderFactory.create(
            DataLoaderFactory.Type.CSV, cfg
        );
        DataSet ds = loader.load();

        // 1b) Normalizar entradas y objetivos
        StandardScaler scalerX = new StandardScaler();
        scalerX.fit(ds.getInputs());
        List<Signal> normX = scalerX.transform(ds.getInputs());

        StandardScaler scalerY = new StandardScaler();
        scalerY.fit(ds.getTargets());
        List<Signal> normY = scalerY.transform(ds.getTargets());

        DataSet dsNorm = new DataSet(normX, normY);

        // 2) Definir la red: 3→32(ReLU)→16(ReLU)→1
        Random rnd = new Random(123);
        Layer in1 = initLayer(32, 3, rnd, ActivationFunctions.RELU);
        Layer in2 = initLayer(16, 32, rnd, ActivationFunctions.RELU);
        Layer out = initLayer(1, 16, rnd, ActivationFunctions.IDENTITY);

        MultiLayerPerceptronProcessor mlp = new MultiLayerPerceptronProcessor(
            List.of(in1, in2, out)
        );

        // 3) Entrenamiento con Backprop, mini-batches
        BackpropMLPTrainer trainer =new BackpropMLPTrainer();
        int epochs    = 3000;
        double lr     = 0.01;
        int batchSize = 16;
        int N         = dsNorm.getInputs().size();
        List<Integer> idx = IntStream.range(0, N).boxed().collect(Collectors.toList());

        for (int e = 1; e <= epochs; e++) {
            mlp.setMode(Mode.TRAIN);
            Collections.shuffle(idx, rnd);

            for (int i = 0; i < N; i += batchSize) {
                int end = Math.min(N, i + batchSize);
                List<Signal> inB = idx.subList(i, end).stream()
                                      .map(dsNorm.getInputs()::get)
                                      .toList();
                List<Signal> tgB = idx.subList(i, end).stream()
                                      .map(dsNorm.getTargets()::get)
                                      .toList();
                DataSet batch = new DataSet(inB, tgB);
                mlp = trainer.train(mlp, batch, 1, lr);
            }

            if (e == 1 || e == epochs || e % 500 == 0) {
                double mse = computeMSE(mlp, dsNorm);
                System.out.printf("Epoch %4d/%d  lr=%.3f  MSE=%.4f%n",
                                  e, epochs, lr, mse);
            }
        }

        // 4) Evaluación en modo inferencia
        mlp.setMode(Mode.INFERENCE);
        double finalMse = computeMSE(mlp, dsNorm);
        System.out.printf("MSE final (norm): %.4f%n", finalMse);

        // 5) Reporte del modelo
        ModelInfo info = new ModelInfo.Builder("HousePrice MLP 3→32→16→1")
            .addLayer(3, 32, "ReLU")
            .addLayer(32, 16, "ReLU")
            .addLayer(16,  1, "Identity")
            .epochs(epochs)
            .learningRate(lr)
            .mse(finalMse)
            .build();
        ModelReporter.report(info);

        // 6) Predecir y desnormalizar
        List<Double> xData = new ArrayList<>(N),
                       yReal = new ArrayList<>(N),
                       yPred = new ArrayList<>(N);

        // A) Construir lista de predicciones normalizadas
        List<Signal> predsNorm = dsNorm.getInputs().stream()
                                       .map(mlp::predict)
                                       .toList();
        // B) Desnormalizar
        List<Signal> preds     = scalerY.inverseTransform(predsNorm);

        // C) Recoger valores reales originales
        for (int i = 0; i < N; i++) {
            xData.add((double) i);
            yReal.add(ds.getTargets().get(i).getValues().get(0));
            yPred.add(preds.get(i).getValues().get(0));
        }

        // 6b) Verificación 1:1
        List<Double> yPredCheck = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            Signal in  = dsNorm.getInputs().get(i);
            double hat = scalerY.inverseTransform(
                             List.of(mlp.predict(in))
                         ).get(0).getValues().get(0);
            yPredCheck.add(hat);
        }
        System.out.println("Verif. primeros 5 reales:   " + yReal.subList(0,5));
        System.out.println("Verif. primeros 5 predichos:" + yPredCheck.subList(0,5));

        // 7) HTML inline con Plotly
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
                const t1 = { x:x, y:yReal, mode:'lines+markers',
                             name:'Precio Real', line:{color:'blue'} };
                const t2 = { x:x, y:yPred, mode:'lines+markers',
                             name:'Predicción',   line:{color:'red'}  };
                Plotly.newPlot('chart',[t1,t2],{
                  title:'Regresión Precio Casas (MLP)',
                  xaxis:{title:'Índice'}, yaxis:{title:'Precio'}
                });
              </script>
            </body>
            </html>
            """.formatted(
                xData.toString(),
                yReal.toString(),
                yPred.toString()
            );

        // 8) Mostrar en navegador
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
}

/*
 * Cuando entrenas con los precios “tal cual” (del orden de 10⁵), las activaciones y los gradientes de tu red quedan en una escala tan grande que:
1. Las actualizaciones de pesos explotan (los gradientes son enormes),
2. Las operaciones numéricas generan NaN (por overflow o divisiones por cero en BatchNorm),
3. El entrenamiento no converge (el MSE se mantiene en NaN).

Al normalizar tanto las entradas (X) como las salidas (Y) (ponerles media cero y desviación estándar uno):
1. Los valores que pasan por la red quedan todos en una misma escala moderada.
2. Las funciones de activación (ReLU, sigmoid, etc.) trabajan en su zona más “estable”.
3. Las actualizaciones por backpropagation tienen magnitudes razonables, sin desbordarse.
4. El optimizador puede bajar el MSE gradualmente y produzca predicciones reales coherentes.

Por eso, tras aplicar el StandardScaler a X e Y, tu MLP dejó de generar NaN, el MSE empezó a bajar y las primeras predicciones se sitúan ya en el rango correcto de precios. En breve:

Sin normalizar: entradas ~10⁵ ⇒ gradientes ~10⁵ ⇒ overflow ⇒ NaN
Con normalización: entradas ~1 ⇒ gradientes ~1 ⇒ entrenamiento estable ⇒ predicciones correctas
 * */

