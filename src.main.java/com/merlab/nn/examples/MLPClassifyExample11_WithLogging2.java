package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.data.synthetic.SyntheticCirclesDataset;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.trainer.BackpropMLPTrainer;
import com.merlab.signals.nn.trainer.MLPTrainer;
import com.merlab.signals.plot.PlotlyBrowserViewer;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MLPClassifyExample11_WithLogging2:
 * Igual que WithLogging, pero al final grafica
 * la frontera y los puntos en el navegador usando Plotly CDN.
 */
public class MLPClassifyExample11_WithLogging2 {

    public static void main(String[] args) throws IOException {
        // Parámetros
        int    nSamples     = 500;
        double rInt         = 1.0;
        double rExt         = 2.0;
        int    hidden1      = 64;
        int    hidden2      = 32;
        int    epochs       = 5000;
        double learningRate = 0.005;
        int    logInterval  = 500;

        // 1) Generar dataset
        double[][] raw = SyntheticCirclesDataset.generate(nSamples, rInt, rExt);
        DataSet ds = DataSetBuilder.fromArray(convertToOneHot(raw), 2);

        // 2) Arquitectura MLP
        Layer l1 = initLayer(hidden1, 2, ActivationFunctions.RELU);
        Layer l2 = initLayer(hidden2, hidden1, ActivationFunctions.RELU);
        Layer out = initLayer(2, hidden2, ActivationFunctions.IDENTITY);
        MultiLayerPerceptronProcessor mlp = new MultiLayerPerceptronProcessor(List.of(l1, l2, out));

        // 3) Entrenamiento con logging
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        MultiLayerPerceptronProcessor current = mlp;
        for (int epoch = 1; epoch <= epochs; epoch++) {
            current = trainer.train(current, ds, 1, learningRate);
            if (epoch % logInterval == 0 || epoch == 1 || epoch == epochs) {
                double acc = evaluateAccuracy(current, raw, ds);
                System.out.printf("Epoch %4d/%d  lr=%.4f  Acc=%.2f%%%n",
                                  epoch, epochs, learningRate, acc);
            }
        }

        // 4) Generar datos para Plotly
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();
        for (double[] p : raw) {
            if ((int) p[2] == 0) { x0.add(p[0]); y0.add(p[1]); }
            else                { x1.add(p[0]); y1.add(p[1]); }
        }
        // Frontera de decisión: muestreo en malla
        int grid = 100;
        double minX = min(raw,0), maxX = max(raw,0);
        double minY = min(raw,1), maxY = max(raw,1);
        double stepX = (maxX - minX)/grid, stepY = (maxY - minY)/grid;
        List<Double> bx = new ArrayList<>(), by = new ArrayList<>();
        for (int i = 0; i <= grid; i++) {
            for (int j = 0; j <= grid; j++) {
                double xp = minX + i*stepX, yp = minY + j*stepY;
                Signal s = new Signal(); s.add(xp); s.add(yp);
                double s0 = current.predict(s).getValues().get(0);
                double s1 = current.predict(s).getValues().get(1);
                if (s0 > s1) { bx.add(xp); by.add(yp); }
            }
        }

        // 5) Construir HTML usando Plotly CDN
        String html = buildPlotlyHtml(x0, y0, x1, y1, bx, by);

        // 6) Abrir en navegador
        PlotlyBrowserViewer.showInBrowser(html);

        // 7) Reporte final
        double finalAcc = evaluateAccuracy(current, raw, ds);
        ModelInfo info = new ModelInfo.Builder("MLP Clasificador Círculos v11+Log2")
            .addLayer(2, hidden1, "ReLU")
            .addLayer(hidden1, hidden2, "ReLU")
            .addLayer(hidden2, 2,       "Identity")
            .epochs(epochs)
            .learningRate(learningRate)
            .accuracy(finalAcc)
            .build();
        ModelReporter.report(info);
    }

    private static String buildPlotlyHtml(
        List<Double> x0, List<Double> y0,
        List<Double> x1, List<Double> y1,
        List<Double> bx, List<Double> by
    ) {
        return """
          <html>
           <head>
             <meta charset="utf-8"/>
             <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
           </head>
           <body>
             <div id="chart" style="width:800px;height:600px;"></div>
             <script>
               const trace0 = {
                 x: %s, y: %s, mode: 'markers',
                 marker: {color: 'blue', size: 6}, name: 'Clase 0'
               };
               const trace1 = {
                 x: %s, y: %s, mode: 'markers',
                 marker: {color: 'red', size: 6}, name: 'Clase 1'
               };
               const trace2 = {
                 x: %s, y: %s, mode: 'markers',
                 marker: {color: 'rgba(0,0,0,0.2)', size: 2}, name: 'Frontera'
               };
               Plotly.newPlot('chart', [trace0, trace1, trace2]);
             </script>
           </body>
          </html>
          """.formatted(
            x0.toString(), y0.toString(),
            x1.toString(), y1.toString(),
            bx.toString(), by.toString()
          );
    }

    private static double evaluateAccuracy(
        MultiLayerPerceptronProcessor mlp, double[][] raw, DataSet ds
    ) {
        int correct = 0;
        for (int i = 0; i < raw.length; i++) {
            Signal in  = ds.getInputs().get(i);
            Signal out = mlp.predict(in);
            int pred   = out.getValues().get(1) > out.getValues().get(0) ? 1 : 0;
            if (pred == (int) raw[i][2]) correct++;
        }
        return 100.0 * correct / raw.length;
    }

    private static double min(double[][] raw, int c) {
        double m = Double.POSITIVE_INFINITY;
        for (var r : raw) m = Math.min(m, r[c]);
        return m;
    }
    private static double max(double[][] raw, int c) {
        double m = Double.NEGATIVE_INFINITY;
        for (var r : raw) m = Math.max(m, r[c]);
        return m;
    }

    private static double[][] convertToOneHot(double[][] raw) {
        double[][] one = new double[raw.length][4];
        for (int i = 0; i < raw.length; i++) {
            one[i][0] = raw[i][0];
            one[i][1] = raw[i][1];
            if ((int) raw[i][2] == 0) { one[i][2]=1; one[i][3]=0; }
            else                      { one[i][2]=0; one[i][3]=1; }
        }
        return one;
    }

    private static Layer initLayer(
        int neurons, int inputs,
        com.merlab.signals.nn.processor.ActivationFunction act
    ) {
        java.util.Random rnd = new java.util.Random(0);
        double[][] w = new double[neurons][inputs];
        double[]   b = new double[neurons];
        for (int i = 0; i < neurons; i++) {
            b[i] = (rnd.nextDouble()*2-1)*0.1;
            for (int j = 0; j < inputs; j++) {
                w[i][j] = (rnd.nextDouble()*2-1)*0.1;
            }
        }
        return new Layer(w, b, act);
    }
}
