package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.io.IOException;
import java.util.*;

public class SVMPlotlyExample6 {
    public static void main(String[] args) throws IOException {
        Random rand = new Random(77);

        // 1. Datos no linealmente separables
        List<Signal> points = new ArrayList<>();
        int N = 30;
        for (int i = 0; i < N; i++) {
            double r = 1.0 * Math.sqrt(rand.nextDouble());
            double theta = 2 * Math.PI * rand.nextDouble();
            double x = r * Math.cos(theta);
            double y = r * Math.sin(theta);
            points.add(new Signal(Arrays.asList(x, y, 1.0)));
        }
        for (int i = 0; i < N; i++) {
            double r = 2.5 + 0.5 * Math.sqrt(rand.nextDouble());
            double theta = 2 * Math.PI * rand.nextDouble();
            double x = r * Math.cos(theta);
            double y = r * Math.sin(theta);
            points.add(new Signal(Arrays.asList(x, y, -1.0)));
        }

        // 2. Tres fronteras
        double[] w1 = {1.0, 0.0};    double b1 = 0.0;   // Vertical
        double[] w2 = {0.0, 1.0};    double b2 = 0.0;   // Horizontal
        double[] w3 = {1.0, 1.0};    double b3 = 0.0;   // Diagonal

        // 3. Puntos por clase
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

        // 4. Puntos mal clasificados por cada frontera
        List<Double> xWrong1 = new ArrayList<>(), yWrong1 = new ArrayList<>();
        List<Double> xWrong2 = new ArrayList<>(), yWrong2 = new ArrayList<>();
        List<Double> xWrong3 = new ArrayList<>(), yWrong3 = new ArrayList<>();
        for (Signal pt : points) {
            double x = pt.get(0), y = pt.get(1), label = pt.get(2);
            // Frontera 1
            double pred1 = w1[0]*x + w1[1]*y + b1 >= 0 ? 1.0 : -1.0;
            if (pred1 != label) { xWrong1.add(x); yWrong1.add(y); }
            // Frontera 2
            double pred2 = w2[0]*x + w2[1]*y + b2 >= 0 ? 1.0 : -1.0;
            if (pred2 != label) { xWrong2.add(x); yWrong2.add(y); }
            // Frontera 3
            double pred3 = w3[0]*x + w3[1]*y + b3 >= 0 ? 1.0 : -1.0;
            if (pred3 != label) { xWrong3.add(x); yWrong3.add(y); }
        }

        // 5. Líneas de frontera
        double[] xLine = {-4.0, 4.0};
        double[] yLine1 = new double[2], yLine2 = new double[2], yLine3 = new double[2];
        for (int i = 0; i < xLine.length; i++) {
            yLine1[i] = w1[1] == 0.0 ? 0.0 : (-w1[0]*xLine[i] - b1)/w1[1];
            yLine2[i] = w2[1] == 0.0 ? 0.0 : (-w2[0]*xLine[i] - b2)/w2[1];
            yLine3[i] = w3[1] == 0.0 ? 0.0 : (-w3[0]*xLine[i] - b3)/w3[1];
        }

        // 6. HTML Plotly con mal clasificados resaltados
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
              // Mal clasificados frontera 1
              const wrong1 = {
                x: %s,
                y: %s,
                mode: 'markers',
                type: 'scatter',
                name: 'Misclassified (Vertical)',
                marker: {color:'black', size:13, symbol:'x'}
              };
              // Mal clasificados frontera 2
              const wrong2 = {
                x: %s,
                y: %s,
                mode: 'markers',
                type: 'scatter',
                name: 'Misclassified (Horizontal)',
                marker: {color:'magenta', size:12, symbol:'x-thin'}
              };
              // Mal clasificados frontera 3
              const wrong3 = {
                x: %s,
                y: %s,
                mode: 'markers',
                type: 'scatter',
                name: 'Misclassified (Diagonal)',
                marker: {color:'cyan', size:12, symbol:'cross'}
              };
              // Fronteras
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
              Plotly.newPlot('chart', [
                plus, minus, wrong1, wrong2, wrong3,
                frontier1, frontier2, frontier3
              ], {
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
                xWrong1.toString(), yWrong1.toString(),
                xWrong2.toString(), yWrong2.toString(),
                xWrong3.toString(), yWrong3.toString(),
                xLine[0], xLine[1], yLine1[0], yLine1[1],
                xLine[0], xLine[1], yLine2[0], yLine2[1],
                xLine[0], xLine[1], yLine3[0], yLine3[1]
        );

        PlotlyBrowserViewer.showInBrowser(html);
    }
}
