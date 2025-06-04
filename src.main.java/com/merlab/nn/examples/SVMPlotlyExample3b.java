package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.ml.SVMProcessor2;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

public class SVMPlotlyExample3b {
    public static void main(String[] args) throws IOException {
        // 1. Datos base
        List<Signal> points = new ArrayList<>();
        points.add(new Signal(Arrays.asList(2.0, 1.0)));   // +1
        points.add(new Signal(Arrays.asList(1.0, 3.0)));   // -1
        points.add(new Signal(Arrays.asList(3.0, 0.5)));   // +1
        points.add(new Signal(Arrays.asList(0.5, 2.5)));   // -1
        points.add(new Signal(Arrays.asList(2.5, 2.5)));   // Cerca frontera
        points.add(new Signal(Arrays.asList(1.5, 1.2)));

        double[] x1 = new double[points.size()];
        double[] x2 = new double[points.size()];
        int[] cls = new int[points.size()];

        // Un modelo base para clasificar puntos (elige el primero para etiquetas)
        SVMProcessor2 svmBase = new SVMProcessor2(2);
        svmBase.setWeights(new double[]{1.0, -1.0});
        svmBase.setBias(0.0);
        for (int i = 0; i < points.size(); i++) {
            x1[i] = points.get(i).get(0);
            x2[i] = points.get(i).get(1);
            cls[i] = (int) svmBase.predict(points.get(i)).get(0);
        }

        // 2. Sweep de parámetros
        double[] w1s = {1.0, 2.0, -1.0};
        double[] w2s = {-1.0, 1.0, 2.0};
        double[] biases = {-1.0, 0.0, 1.0};

        // Para cada frontera, calculamos los pares de arrays [x, y]
        StringBuilder lineTraces = new StringBuilder();
        String[] colors = {"green", "orange", "purple", "magenta", "cyan", "brown", "black"};
        int colorIdx = 0;
        for (double w1 : w1s) {
            for (double w2 : w2s) {
                for (double b : biases) {
                    double[] xline = {-1.0, 4.0};
                    double[] yline = new double[2];
                    for (int i = 0; i < xline.length; i++)
                        yline[i] = (-w1 * xline[i] - b) / w2;
                    String color = colors[colorIdx % colors.length];
                    colorIdx++;
                    // Añade como trace JS
                    lineTraces.append(
                        """
                        {
                          x: [%f, %f],
                          y: [%f, %f],
                          mode: 'lines',
                          type: 'scatter',
                          name: 'w1=%.1f, w2=%.1f, b=%.1f',
                          line: {color: '%s', width: 2}
                        },
                        """.formatted(
                            xline[0], xline[1],
                            yline[0], yline[1],
                            w1, w2, b,
                            color
                        )
                    );
                }
            }
        }

        // 3. Prepara arrays JS de puntos según clase
        StringBuilder xPos = new StringBuilder(), yPos = new StringBuilder();
        StringBuilder xNeg = new StringBuilder(), yNeg = new StringBuilder();
        for (int i = 0; i < x1.length; i++) {
            if (cls[i] == 1) {
                xPos.append(x1[i]).append(",");
                yPos.append(x2[i]).append(",");
            } else {
                xNeg.append(x1[i]).append(",");
                yNeg.append(x2[i]).append(",");
            }
        }

        // 4. HTML limpio tipo template
        String html = """
        <html>
          <head>
            <meta charset="utf-8"/>
            <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
          </head>
          <body>
            <div id="chart" style="width:800px;height:600px;"></div>
            <script>
              // Puntos clase +1
              const tracePos = {
                x: [%s],
                y: [%s],
                mode: 'markers',
                type: 'scatter',
                name: '+1',
                marker: {color:'blue', size:10}
              };
              // Puntos clase -1
              const traceNeg = {
                x: [%s],
                y: [%s],
                mode: 'markers',
                type: 'scatter',
                name: '-1',
                marker: {color:'red', size:10}
              };
              // Todas las fronteras del sweep
              const lines = [
                %s
              ];

              const data = [tracePos, traceNeg, ...lines];
              Plotly.newPlot('chart', data, {
                title:'SVM Decision Boundaries (Auto Sweep)',
                xaxis: {title:'x1'},
                yaxis: {title:'x2'}
              });
            </script>
          </body>
        </html>
        """.formatted(
                xPos.toString(), yPos.toString(),
                xNeg.toString(), yNeg.toString(),
                lineTraces.toString()
        );

        // 5. Mostrar en navegador
        PlotlyBrowserViewer.showInBrowser(html);
    }
}
