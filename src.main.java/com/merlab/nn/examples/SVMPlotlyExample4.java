package com.merlab.nn.examples;
import com.merlab.signals.core.Signal;
import com.merlab.signals.ml.SVMProcessor2;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.io.IOException;
import java.util.*;

public class SVMPlotlyExample4 {
    public static void main(String[] args) throws IOException {
        Random rand = new Random(42);

        // 1. Generar data sintética: dos clases separables
        List<Signal> points = new ArrayList<>();
        int N = 15;
        // Clase +1: alrededor de (2,2)
        for (int i = 0; i < N; i++) {
            double x = 1.5 + rand.nextGaussian();
            double y = 1.5 + rand.nextGaussian();
            points.add(new Signal(Arrays.asList(x, y, 1.0))); // Último valor = clase
        }
        // Clase -1: alrededor de (-2,-2)
        for (int i = 0; i < N; i++) {
            double x = -1.5 + rand.nextGaussian();
            double y = -1.5 + rand.nextGaussian();
            points.add(new Signal(Arrays.asList(x, y, -1.0)));
        }

        // 2. Tres SVMs con distintas fronteras
        // Frontera “ideal”
        double[] w1 = {1.0, 1.0};
        double b1 = 0.0;
        // Frontera "casi correcta" (rotada)
        double[] w2 = {0.5, 1.5};
        double b2 = 0.0;
        // Frontera “incorrecta”
        double[] w3 = {-1.0, 1.0};
        double b3 = 0.0;

        // 3. Clasificar puntos para graficar
        double[] xPlus = new double[N], yPlus = new double[N];
        double[] xMinus = new double[N], yMinus = new double[N];
        int idxP = 0, idxM = 0;
        for (Signal pt : points) {
            double label = pt.get(2);
            if (label == 1.0) {
                xPlus[idxP] = pt.get(0);
                yPlus[idxP] = pt.get(1);
                idxP++;
            } else {
                xMinus[idxM] = pt.get(0);
                yMinus[idxM] = pt.get(1);
                idxM++;
            }
        }

        // 4. Accuracy para cada frontera
        double acc1 = accuracy(points, w1, b1);
        double acc2 = accuracy(points, w2, b2);
        double acc3 = accuracy(points, w3, b3);

        System.out.printf("Accuracy (Ideal)         : %.2f%%%n", acc1 * 100);
        System.out.printf("Accuracy (Almost correct): %.2f%%%n", acc2 * 100);
        System.out.printf("Accuracy (Wrong)         : %.2f%%%n", acc3 * 100);

        // 5. Preparar líneas de frontera (y = (-w1*x - b)/w2)
        double[] xLine = {-4.0, 4.0};
        double[] yLine1 = new double[2];
        double[] yLine2 = new double[2];
        double[] yLine3 = new double[2];
        for (int i = 0; i < xLine.length; i++) {
            yLine1[i] = (-w1[0]*xLine[i] - b1) / w1[1];
            yLine2[i] = (-w2[0]*xLine[i] - b2) / w2[1];
            yLine3[i] = (-w3[0]*xLine[i] - b3) / w3[1];
        }

        // 6. HTML Plotly (template)
        String html = """
        <html>
          <head>
            <meta charset="utf-8"/>
            <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
          </head>
          <body>
            <div id="chart" style="width:800px;height:600px;"></div>
            <script>
              const plus = {
                x: %s,
                y: %s,
                mode: 'markers',
                type: 'scatter',
                name: '+1',
                marker: {color:'blue', size:8}
              };
              const minus = {
                x: %s,
                y: %s,
                mode: 'markers',
                type: 'scatter',
                name: '-1',
                marker: {color:'red', size:8}
              };
              const frontier1 = {
                x: [%f,%f],
                y: [%f,%f],
                mode: 'lines',
                type: 'scatter',
                name: 'Ideal',
                line: {color:'green', width:3}
              };
              const frontier2 = {
                x: [%f,%f],
                y: [%f,%f],
                mode: 'lines',
                type: 'scatter',
                name: 'Almost',
                line: {color:'orange', width:2, dash:'dash'}
              };
              const frontier3 = {
                x: [%f,%f],
                y: [%f,%f],
                mode: 'lines',
                type: 'scatter',
                name: 'Wrong',
                line: {color:'purple', width:2, dash:'dot'}
              };
              Plotly.newPlot('chart', [plus, minus, frontier1, frontier2, frontier3], {
                title:'SVM Three Decision Boundaries (Synthetic Data)',
                xaxis: {title:'x1', range:[-4,4]},
                yaxis: {title:'x2', range:[-4,4]}
              });
            </script>
          </body>
        </html>
        """.formatted(
                Arrays.toString(xPlus), Arrays.toString(yPlus),
                Arrays.toString(xMinus), Arrays.toString(yMinus),
                xLine[0], xLine[1], yLine1[0], yLine1[1],
                xLine[0], xLine[1], yLine2[0], yLine2[1],
                xLine[0], xLine[1], yLine3[0], yLine3[1]
        );

        PlotlyBrowserViewer.showInBrowser(html);
    }

    /**
     * Calcula el accuracy de una frontera SVM sobre los puntos
     * @param points Lista de señales (x, y, label)
     * @param w vector de pesos (double[2])
     * @param b bias
     * @return accuracy [0,1]
     */
    private static double accuracy(List<Signal> points, double[] w, double b) {
        int correct = 0;
        for (Signal pt : points) {
            double x = pt.get(0), y = pt.get(1), label = pt.get(2);
            double result = w[0]*x + w[1]*y + b;
            double pred = result >= 0 ? 1.0 : -1.0;
            if (pred == label) correct++;
        }
        return correct / (double) points.size();
    }
}
