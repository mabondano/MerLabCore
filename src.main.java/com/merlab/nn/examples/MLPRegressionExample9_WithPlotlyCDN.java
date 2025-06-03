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

/**
 * MLPRegressionExample9_WithPlotlyCDN
 * Ejemplo de regresión sin(x) usando un MLP entrenado con backprop
 * y visualización en el navegador con Plotly.js desde CDN.
 */
public class MLPRegressionExample9_WithPlotlyCDN {

    public static void main(String[] args) throws IOException {
        // 1) Generar dataset sintético: x en [0, 2π], y = sin(x)
        int n = 100;
        double[][] raw = new double[n][2];
        for (int i = 0; i < n; i++) {
            double x = 2 * Math.PI * i / (n - 1);
            raw[i][0] = x;
            raw[i][1] = Math.sin(x);
        }
        // Construir DataSet (input 1 columna, target 1 columna)
        DataSet ds = DataSetBuilder.fromArray(
            java.util.stream.IntStream.range(0, n)
                .mapToObj(i -> new double[]{ raw[i][0], raw[i][1] })
                .toArray(double[][]::new),
            /* target columns at the end */ 1
        );

        // 2) Construir MLP de 3 capas: [1 → 10(ReLU) → 10(ReLU) → 1(Identity)]
        Layer hidden1 = initLayer(10, 1, ActivationFunctions.RELU);
        Layer hidden2 = initLayer(10, 10, ActivationFunctions.RELU);
        Layer output  = initLayer( 1, 10, ActivationFunctions.IDENTITY);
        MultiLayerPerceptronProcessor mlp = 
            new MultiLayerPerceptronProcessor(List.of(hidden1, hidden2, output));

        // 3) Entrenar con BackpropMLPTrainer
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        // entrenamos en 1000 epochs a lr=0.01
        mlp = trainer.train(mlp, ds, 1000, 0.01);

        // 4) Preparar listas para Plotly
        List<Double> xData   = new ArrayList<>();
        List<Double> yReal   = new ArrayList<>();
        List<Double> yPred   = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double x = raw[i][0];
            xData.add(x);
            yReal.add(raw[i][1]);
            Signal s = new Signal();
            s.add(x);
            double yp = mlp.predict(s).getValues().get(0);
            yPred.add(yp);
        }

        // 5) Generar HTML con Plotly CDN
        String html = """
          <html>
           <head>
             <meta charset="utf-8"/>
             <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
           </head>
           <body>
             <div id="chart" style="width:800px;height:600px;"></div>
             <script>
               const traceReal = {
                 x:%s, y:%s,
                 mode:'lines', name:'sin(x) Real',
                 line:{color:'blue',width:2}
               };
               const tracePred = {
                 x:%s, y:%s,
                 mode:'lines+markers', name:'Predicción',
                 line:{color:'red',dash:'dash'}, marker:{size:4}
               };
               Plotly.newPlot('chart',[traceReal,tracePred],{
                 title:'Regresión sin(x) con MLP',
                 xaxis:{title:'x'},
                 yaxis:{title:'y'}
               });
             </script>
           </body>
          </html>
          """.formatted(
            xData.toString(),
            yReal.toString(),
            xData.toString(),
            yPred.toString()
          );

        // 6) Abrir en el navegador
        PlotlyBrowserViewer.showInBrowser(html);
    }

    /** Crea una capa con pesos/bias inicializados aleatoriamente pequeños */
    private static Layer initLayer(int neurons, int inputs, 
                                   com.merlab.signals.nn.processor.ActivationFunction act) {
        java.util.Random rnd = new java.util.Random(0);
        double[][] w = new double[neurons][inputs];
        double[]   b = new double[neurons];
        for (int i = 0; i < neurons; i++) {
            b[i] = (rnd.nextDouble()*2 - 1) * 0.1;
            for (int j = 0; j < inputs; j++) {
                w[i][j] = (rnd.nextDouble()*2 - 1) * 0.1;
            }
        }
        return new Layer(w, b, act);
    }
}
