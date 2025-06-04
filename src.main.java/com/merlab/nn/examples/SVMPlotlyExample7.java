package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.io.IOException;
import java.util.*;

public class SVMPlotlyExample7 {
    public static void main(String[] args) throws IOException {
        Random rand = new Random(123);
        List<Signal> points = new ArrayList<>();
        int N = 15;
        // Clase +1 alrededor de (2,2)
        for (int i = 0; i < N; i++) {
            double x = 2.0 + 0.6 * rand.nextGaussian();
            double y = 2.0 + 0.6 * rand.nextGaussian();
            points.add(new Signal(Arrays.asList(x, y, 1.0)));
        }
        // Clase -1 alrededor de (-2,-2)
        for (int i = 0; i < N; i++) {
            double x = -2.0 + 0.6 * rand.nextGaussian();
            double y = -2.0 + 0.6 * rand.nextGaussian();
            points.add(new Signal(Arrays.asList(x, y, -1.0)));
        }

        // Frontera lineal óptima para datos bien separados
        double[] w = {1.0, 1.0};
        double b = 0.0;

        // Puntos por clase
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

        // Líneas de frontera y márgenes
        double[] xLine = {-4.0, 4.0};
        double[] yDecision = new double[2], yMargin1 = new double[2], yMargin2 = new double[2];
        for (int i = 0; i < xLine.length; i++) {
            yDecision[i] = (-w[0]*xLine[i] - b)/w[1];           // Frontera principal
            yMargin1[i] = (-w[0]*xLine[i] - b + 1)/w[1];        // Primer margen
            yMargin2[i] = (-w[0]*xLine[i] - b - 1)/w[1];        // Segundo margen
        }

        // HTML Plotly
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
                x: %s, y: %s, mode: 'markers', type: 'scatter', name: '+1', marker: {color:'blue', size:8}
              };
              const minus = {
                x: %s, y: %s, mode: 'markers', type: 'scatter', name: '-1', marker: {color:'red', size:8}
              };
              const frontier = {
                x: [%f,%f], y: [%f,%f], mode:'lines', type:'scatter', name:'Decision Boundary',
                line:{color:'green', width:3}
              };
              const margin1 = {
                x: [%f,%f], y: [%f,%f], mode:'lines', type:'scatter', name:'Margin +1',
                line:{color:'black', width:2, dash:'dash'}
              };
              const margin2 = {
                x: [%f,%f], y: [%f,%f], mode:'lines', type:'scatter', name:'Margin -1',
                line:{color:'black', width:2, dash:'dash'}
              };
              Plotly.newPlot('chart', [plus, minus, frontier, margin1, margin2], {
                title:'SVM Decision Boundary & Margins (Linearly Separable)',
                xaxis: {title:'x1', range:[-4,4]},
                yaxis: {title:'x2', range:[-4,4]}
              });
            </script>
          </body>
        </html>
        """.formatted(
                xPlus.toString(), yPlus.toString(),
                xMinus.toString(), yMinus.toString(),
                xLine[0], xLine[1], yDecision[0], yDecision[1],
                xLine[0], xLine[1], yMargin1[0], yMargin1[1],
                xLine[0], xLine[1], yMargin2[0], yMargin2[1]
        );

        PlotlyBrowserViewer.showInBrowser(html);
    }
}
