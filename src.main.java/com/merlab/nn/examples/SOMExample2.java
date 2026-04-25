package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.ml.SOMProcessor2;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Ejemplo de Self‐Organizing Map (Kohonen) en datos sintéticos 2D.
 * - Se generan tres clusters gaussianos en R^2.
 * - Se entrena un SOM 10×10 (100 neuronas) sobre esos puntos.
 * - Se grafica con Plotly: (1) puntos reales coloreados según cluster original, (2) pesos finales de cada neurona.
 */
public class SOMExample2 {

    public static void main(String[] args) throws Exception {
        // 1) Generar datos sintéticos en 2D: tres clústeres gaussianos
        int N = 300;
        double[][] raw = new double[N][3]; // [x, y, label]
        Random rnd = new Random(123);

        // Cluster 0: centrado en (-2, -2)
        for (int i = 0; i < N/3; i++) {
            double x = rnd.nextGaussian() * 0.3 - 2.0;
            double y = rnd.nextGaussian() * 0.3 - 2.0;
            raw[i] = new double[]{ x, y, 0.0 };
        }
        // Cluster 1: centrado en (0, 2)
        for (int i = N/3; i < 2*N/3; i++) {
            double x = rnd.nextGaussian() * 0.3 + 0.0;
            double y = rnd.nextGaussian() * 0.3 + 2.0;
            raw[i] = new double[]{ x, y, 1.0 };
        }
        // Cluster 2: centrado en (2, -1)
        for (int i = 2*N/3; i < N; i++) {
            double x = rnd.nextGaussian() * 0.3 + 2.0;
            double y = rnd.nextGaussian() * 0.3 - 1.0;
            raw[i] = new double[]{ x, y, 2.0 };
        }

        // 2) Construir DataSet (las dos primeras columnas son features; la última es etiqueta "dummy")
        DataSet ds = DataSetBuilder.fromArray(raw, 2);

        // 3) Entrenar el SOM: grilla 10×10, dimensión entrada=2, 1000 iteraciones, lr=0.1, radio=5.0
        int rows = 10, cols = 10;
        int inputDim = 2;
        int totalIters = 1000;
        double initialLR = 0.1;
        double initialRadius = Math.max(rows, cols) / 2.0; // ej. 5.0
        long seed = 123L;

        SOMProcessor2 som = new SOMProcessor2(rows, cols, inputDim,
                                              totalIters,
                                              initialLR,
                                              initialRadius,
                                              seed);

        som.train(ds);

        // 4) Extraer pesos finales (centroides de cada neurona)
        List<double[]> centroides = som.getWeights();
        // centroides.size() == rows * cols == 100

        // 5) Preparar Listas para graficar en Plotly
        // (a) puntos originales: 3 listas (x,y) según label 0,1,2
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();
        List<Double> x2 = new ArrayList<>(), y2 = new ArrayList<>();

        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal s = ds.getInputs().get(i);
            double label = ds.getTargets().get(i).getValues().get(0);
            double xv = s.getValues().get(0);
            double yv = s.getValues().get(1);
            if (label < 0.5) {
                x0.add(xv);
                y0.add(yv);
            } else if (label < 1.5) {
                x1.add(xv);
                y1.add(yv);
            } else {
                x2.add(xv);
                y2.add(yv);
            }
        }

        // (b) pesos de cada neurona: 100 puntos (px, py)
        List<Double> px = centroides.stream().map(w -> w[0]).collect(Collectors.toList());
        List<Double> py = centroides.stream().map(w -> w[1]).collect(Collectors.toList());

        // 6) Construir reporte del modelo (no hay capas; solo informamos iteraciones y tasa)
        ModelInfo info = new ModelInfo.Builder("SOM 10×10")
            .epochs(totalIters)
            .learningRate(initialLR)
            .maxIterations(totalIters)   // esto está permitido en ModelInfo
            .build();
        ModelReporter.report(info);

        // 7) Generar HTML/Plotly inline
        //    Mostraremos: 
        //      - Cluster 0 (puntos azules), cluster 1 (naranjas), cluster 2 (verde)
        //      - Neuronas (pesos) como puntos rojos (marcador: x grande)
        String html = """
            <html>
            <head>
              <script src="plotly.min.js"></script>
            </head>
            <body>
              <div id="chart" style="width:800px;height:600px;"></div>
              <script>
                const x0 = %s;
                const y0 = %s;
                const x1 = %s;
                const y1 = %s;
                const x2 = %s;
                const y2 = %s;
                const px = %s;
                const py = %s;

                const trace0 = {
                  x: x0, y: y0, mode: 'markers',
                  name: 'Cluster 0', marker: { color: 'blue', size: 6, opacity: 0.7 }
                };
                const trace1 = {
                  x: x1, y: y1, mode: 'markers',
                  name: 'Cluster 1', marker: { color: 'orange', size: 6, opacity: 0.7 }
                };
                const trace2 = {
                  x: x2, y: y2, mode: 'markers',
                  name: 'Cluster 2', marker: { color: 'green', size: 6, opacity: 0.7 }
                };
                const traceP = {
                  x: px, y: py, mode: 'markers',
                  name: 'Pesos SOM (neuronas)', 
                  marker: { color: 'red', size: 10, symbol: 'x' }
                };

                Plotly.newPlot('chart', [trace0, trace1, trace2, traceP], {
                  title: 'Self‐Organizing Map (10×10) sobre clusters 2D',
                  xaxis: { title: 'x' },
                  yaxis: { title: 'y' }
                });
              </script>
            </body>
            </html>
            """.formatted(
                x0.toString(), y0.toString(),
                x1.toString(), y1.toString(),
                x2.toString(), y2.toString(),
                px.toString(), py.toString()
            );

        // 8) Mostrar en navegador
        PlotlyBrowserViewer.showInBrowser(html);
    }
}
