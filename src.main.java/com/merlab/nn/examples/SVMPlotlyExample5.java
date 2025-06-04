package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.io.IOException;
import java.util.*;

public class SVMPlotlyExample5 {
    public static void main(String[] args) throws IOException {
        Random rand = new Random(77);

        // 1. Data no linealmente separable: círculo (clase +1) y anillo (clase -1)
        List<Signal> points = new ArrayList<>();
        int N = 30;
        // Círculo interno (clase +1)
        for (int i = 0; i < N; i++) {
            double r = 1.0 * Math.sqrt(rand.nextDouble());
            double theta = 2 * Math.PI * rand.nextDouble();
            double x = r * Math.cos(theta);
            double y = r * Math.sin(theta);
            points.add(new Signal(Arrays.asList(x, y, 1.0)));
        }
        // Anillo externo (clase -1)
        for (int i = 0; i < N; i++) {
            double r = 2.5 + 0.5 * Math.sqrt(rand.nextDouble());
            double theta = 2 * Math.PI * rand.nextDouble();
            double x = r * Math.cos(theta);
            double y = r * Math.sin(theta);
            points.add(new Signal(Arrays.asList(x, y, -1.0)));
        }

        // 2. Usamos 3 fronteras para comparar (elige valores arbitrarios)
        double[] w1 = {1.0, 0.0};    double b1 = 0.0;   // Vertical
        double[] w2 = {0.0, 1.0};    double b2 = 0.0;   // Horizontal
        double[] w3 = {1.0, 1.0};    double b3 = 0.0;   // Diagonal

        // 3. Para graficar
        List<Double> xPlus = new ArrayList<>(), yPlus = new ArrayList<>();
        List<Double> xMinus = new ArrayList<>(), yMinus = new ArrayList<>();
        for (Signal pt : points) {
            if (pt.get(2) == 1.0) {
                xPlus.add(pt.get(0));
                yPlus.add(pt.get(1));
            } else {
                xMinus.add(pt.get(0));
                yMinus.add(pt.get(1));
            }
        }

        // 4. Accuracy para cada frontera
        double acc1 = accuracy(points, w1, b1);
        double acc2 = accuracy(points, w2, b2);
        double acc3 = accuracy(points, w3, b3);

        System.out.printf("Accuracy (Vertical)  : %.2f%%%n", acc1 * 100);
        System.out.printf("Accuracy (Horizontal): %.2f%%%n", acc2 * 100);
        System.out.printf("Accuracy (Diagonal)  : %.2f%%%n", acc3 * 100);

        // 5. Líneas de frontera para plot
        double[] xLine = {-4.0, 4.0};
        double[] yLine1 = new double[2]; // w1*x + w2*y + b = 0 -> y = (-w1*x - b)/w2
        double[] yLine2 = new double[2];
        double[] yLine3 = new double[2];
        for (int i = 0; i < xLine.length; i++) {
            yLine1[i] = w1[1] == 0.0 ? 0.0 : (-w1[0]*xLine[i] - b1)/w1[1];
            yLine2[i] = w2[1] == 0.0 ? 0.0 : (-w2[0]*xLine[i] - b2)/w2[1];
            yLine3[i] = w3[1] == 0.0 ? 0.0 : (-w3[0]*xLine[i] - b3)/w3[1];
        }

        // 6. HTML para Plotly
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
                name: '+1 (círculo)',
                marker: {color:'blue', size:7}
              };
              const minus = {
                x: %s,
                y: %s,
                mode: 'markers',
                type: 'scatter',
                name: '-1 (anillo)',
                marker: {color:'red', size:7}
              };
              const frontier1 = {
                x: [%f,%f],
                y: [%f,%f],
                mode: 'lines',
                type: 'scatter',
                name: 'Vertical',
                line: {color:'green', width:3}
              };
              const frontier2 = {
                x: [%f,%f],
                y: [%f,%f],
                mode: 'lines',
                type: 'scatter',
                name: 'Horizontal',
                line: {color:'orange', width:2, dash:'dash'}
              };
              const frontier3 = {
                x: [%f,%f],
                y: [%f,%f],
                mode: 'lines',
                type: 'scatter',
                name: 'Diagonal',
                line: {color:'purple', width:2, dash:'dot'}
              };
              Plotly.newPlot('chart', [plus, minus, frontier1, frontier2, frontier3], {
                title:'SVM Linear Boundaries (Non-Linearly Separable Data)',
                xaxis: {title:'x1', range:[-4,4]},
                yaxis: {title:'x2', range:[-4,4]}
              });
            </script>
          </body>
        </html>
        """.formatted(
                xPlus.toString(), yPlus.toString(),
                xMinus.toString(), yMinus.toString(),
                xLine[0], xLine[1], yLine1[0], yLine1[1],
                xLine[0], xLine[1], yLine2[0], yLine2[1],
                xLine[0], xLine[1], yLine3[0], yLine3[1]
        );

        PlotlyBrowserViewer.showInBrowser(html);
    }

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
