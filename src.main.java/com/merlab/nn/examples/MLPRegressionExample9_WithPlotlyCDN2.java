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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * MLPRegressionExample9_WithPlotlyCDN2
 * Regresión de sin(x) con un MLP mejor entrenado y logging de MSE, 
 * y visualización con Plotly.js desde CDN.
 */
public class MLPRegressionExample9_WithPlotlyCDN2 {

    public static void main(String[] args) throws IOException {
        // 1) Generar dataset sintético: x∈[0,2π], y=sin(x)
        int n = 100;
        double[][] raw = new double[n][2];
        for (int i = 0; i < n; i++) {
            double x = 2 * Math.PI * i / (n - 1);
            raw[i][0] = x;
            raw[i][1] = Math.sin(x);
        }
        // DataSet: cada fila [x, y], última columna (y) es target
        DataSet ds = DataSetBuilder.fromArray(raw, /* targetCols= */ 1);

        // 2) Definir MLP: 1→10(ReLU)→10(ReLU)→1(Identity)
        Layer h1 = initLayer(10, 1, ActivationFunctions.RELU);
        Layer h2 = initLayer(10, 10, ActivationFunctions.RELU);
        Layer out = initLayer(1, 10, ActivationFunctions.IDENTITY);
        MultiLayerPerceptronProcessor mlp = 
            new MultiLayerPerceptronProcessor(List.of(h1, h2, out));

        // 3) Entrenar con logging de MSE
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        int epochs       = 5000;
        double lr        = 0.02;
        int logInterval  = 500;

        for (int ep = 1; ep <= epochs; ep++) {
            mlp = trainer.train(mlp, ds, 1, lr);
            if (ep % logInterval == 0 || ep == 1 || ep == epochs) {
                double mse = computeMSE(mlp, raw);
                System.out.printf("Epoch %4d/%d  lr=%.3f  MSE=%.5f%n", ep, epochs, lr, mse);
            }
        }

        // 4) Preparar datos para Plotly
        List<Double> xData = new ArrayList<>(), yReal = new ArrayList<>(), yPred = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double x = raw[i][0];
            xData.add(x);
            yReal.add(raw[i][1]);
            Signal in = new Signal();
            in.add(x);
            double p = mlp.predict(in).getValues().get(0);
            yPred.add(p);
        }

        // 5) Generar HTML con Plotly CDN
        String html = """
          <html>
           <head><meta charset="utf-8"/>
             <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
           </head>
           <body>
             <div id="chart" style="width:800px;height:600px;"></div>
             <script>
               const real = { x:%s, y:%s, mode:'lines', 
                              name:'sin(x) Real', line:{color:'blue',width:2} };
               const pred = { x:%s, y:%s, mode:'lines+markers', 
                              name:'Predicción', line:{color:'red',dash:'dash'},
                              marker:{size:4} };
               Plotly.newPlot('chart',[real,pred],{
                 title:'Regresión sin(x) con MLP (CDN)',
                 xaxis:{title:'x'}, yaxis:{title:'y'}
               });
             </script>
           </body>
          </html>
          """.formatted(
            xData, yReal,
            xData, yPred
          );

        // 6) Mostrar en navegador
        PlotlyBrowserViewer.showInBrowser(html);
    }

    private static double computeMSE(MultiLayerPerceptronProcessor mlp, double[][] raw) {
        double sum = 0;
        for (double[] p : raw) {
            Signal in = new Signal(); in.add(p[0]);
            double yHat = mlp.predict(in).getValues().get(0);
            double err = yHat - p[1];
            sum += err * err;
        }
        return sum / raw.length;
    }

    private static Layer initLayer(int neurons, int inputs,
                                   com.merlab.signals.nn.processor.ActivationFunction act) {
        var rnd = new java.util.Random(0);
        double[][] w = new double[neurons][inputs];
        double[]   b = new double[neurons];
        for (int i = 0; i < neurons; i++) {
            b[i] = (rnd.nextDouble()*2 -1)*0.1;
            for (int j = 0; j < inputs; j++) {
                w[i][j] = (rnd.nextDouble()*2 -1)*0.1;
            }
        }
        return new Layer(w, b, act);
    }
}
