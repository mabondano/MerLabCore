package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.trainer.BackpropMLPTrainer;
import com.merlab.signals.nn.trainer.MLPTrainer;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.*;

/**
 * MLPRegressionExample9_WithPlotlyInline
 * Igual que tu MLPRegressionExample9 original, pero en lugar de XChart
 * genera un HTML con Plotly.js incrustado (inline) y lo abre en el navegador.
 */
public class MLPRegressionExample9_WithPlotlyInline {

    public static void main(String[] args) throws Exception {
        // 1) Dataset sintético [xNorm, sin(x)]
        int n = 100;
        double[][] raw = new double[n][2];
        for (int i = 0; i < n; i++) {
            double x     = 2 * Math.PI * i / (n - 1);
            double xNorm = (x - Math.PI) / Math.PI; // [-1,1]
            raw[i][0] = xNorm;
            raw[i][1] = Math.sin(x);
        }
        DataSet ds = DataSetBuilder.fromArray(raw, /*nColsTarget=*/1);

        // 2) Arquitectura del MLP
        Random rnd = new Random(123);
        Layer hidden1 = initLayer(30, 1, rnd, ActivationFunctions.RELU);
        Layer hidden2 = initLayer(15, 30, rnd, ActivationFunctions.RELU);
        Layer output  = initLayer( 1, 15, rnd, ActivationFunctions.IDENTITY);
        MultiLayerPerceptronProcessor mlp =
            new MultiLayerPerceptronProcessor(List.of(hidden1, hidden2, output));

        // 3) Entrenador mini-batches
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        int    epochs    = 20_000;
        double lr        = 0.01;
        int    batchSize = 16;

        // indices para barajar
        List<Integer> indices = new ArrayList<>(IntStream.range(0, n).boxed().toList());
        for (int epoch = 1; epoch <= epochs; epoch++) {
            if (epoch == 10_000 || epoch == 15_000) {
                lr *= 0.5;
            }
            Collections.shuffle(indices, rnd);
            for (int i = 0; i < n; i += batchSize) {
                int end = Math.min(i + batchSize, n);
                double[][] batch = new double[end - i][2];
                for (int j = i; j < end; j++) {
                    batch[j - i] = raw[indices.get(j)];
                }
                DataSet dsBatch = DataSetBuilder.fromArray(batch, 1);
                mlp = trainer.train(mlp, dsBatch, 1, lr);
            }
            if (epoch % 2_000 == 0) {
                double mse = computeMSE(mlp, raw);
                System.out.printf("Epoch %5d, lr=%.5f, MSE=%.6f%n", epoch, lr, mse);
            }
        }

        // 5) Recopilar datos para graficar
        List<Double> xData = new ArrayList<>(n);
        List<Double> yReal = new ArrayList<>(n);
        List<Double> yPred = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            xData.add(raw[i][0]);
            yReal.add(raw[i][1]);
            Signal inputSignal = ds.getInputs().get(i);
            double yHat = mlp.predict(inputSignal).getValues().get(0);
            yPred.add(yHat);
        }
        // ordenar por x para línea suave
        List<Integer> sorted = IntStream.range(0, n)
            .boxed()
            .sorted(Comparator.comparingDouble(xData::get))
            .toList();
        List<Double> xs = sorted.stream().map(xData::get).toList();
        List<Double> rs = sorted.stream().map(yReal::get).toList();
        List<Double> ps = sorted.stream().map(yPred::get).toList();

        // 6) Cargar plotly.min.js inline desde recursos (/web/plotly.min.js)
        String plotlyJs;
        try (InputStream in = MLPRegressionExample9_WithPlotlyInline.class
                .getResourceAsStream("/web/plotly.min.js")) {
            if (in == null) throw new IOException("plotly.min.js no encontrado en /web/");
            plotlyJs = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        // 7) Montar HTML
        String html = """
          <html>
           <head>
             <meta charset="utf-8"/>
             <script>%s</script>
           </head>
           <body>
             <div id="chart" style="width:800px;height:600px;"></div>
             <script>
               const traceReal = {
                 x: %s, y: %s,
                 mode: 'lines', name: 'sin(x) Real',
                 line: {color: 'blue', width: 2}
               };
               const tracePred = {
                 x: %s, y: %s,
                 mode: 'lines+markers', name: 'Predicción',
                 line: {color: 'red', dash: 'dash'},
                 marker: {size: 4}
               };
               Plotly.newPlot('chart', [traceReal, tracePred], {
                 title: 'Regresión sin(x) con MLP (Inline Plotly)',
                 xaxis: {title: 'xNorm'}, yaxis: {title: 'y'}
               });
             </script>
           </body>
          </html>
          """.formatted(
            plotlyJs,
            xs.toString(), rs.toString(),
            xs.toString(), ps.toString()
          );

        // 8) Mostrar en navegador
        PlotlyBrowserViewer.showInBrowser(html);
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

    private static double computeMSE(MultiLayerPerceptronProcessor mlp, double[][] raw) {
        double sum = 0;
        for (double[] row : raw) {
            Signal in  = new Signal(List.of(row[0]));
            double yh  = mlp.predict(in).getValues().get(0);
            double err = row[1] - yh;
            sum += err * err;
        }
        return sum / raw.length;
    }
}

