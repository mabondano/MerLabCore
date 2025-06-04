package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.ml.SVMProcessor2;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

public class SVMPlotlyExample3 {
    public static void main(String[] args) throws IOException {
        // Puntos sintéticos base
        List<Signal> points = new ArrayList<>();
        points.add(new Signal(Arrays.asList(2.0, 1.0))); // Esperado +1
        points.add(new Signal(Arrays.asList(1.0, 3.0))); // Esperado -1
        points.add(new Signal(Arrays.asList(3.0, 0.5))); // Esperado +1
        points.add(new Signal(Arrays.asList(0.5, 2.5))); // Esperado -1
        points.add(new Signal(Arrays.asList(2.5, 2.5))); // Cerca de la frontera
        points.add(new Signal(Arrays.asList(1.5, 1.2)));

        double[] x1 = new double[points.size()];
        double[] x2 = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            x1[i] = points.get(i).get(0);
            x2[i] = points.get(i).get(1);
        }

        // Valores para barrer (puedes cambiar los arrays para probar otros rangos)
        double[] w1s = {1.0, 2.0, -1.0};    // Peso para x1
        double[] w2s = {-1.0, 1.0, 2.0};    // Peso para x2
        double[] biases = {-1.0, 0.0, 1.0}; // Bias

        // HTML Plotly: empieza la cabecera
        StringBuilder html = new StringBuilder();
        html.append("<html><head>")
            .append("<script src='https://cdn.plot.ly/plotly-latest.min.js'></script></head><body>")
            .append("<div id='plot'></div>")
            .append("<script>let data=[");

        // Pinta los puntos azules y rojos según la clasificación con el primer modelo
        SVMProcessor2 svmBase = new SVMProcessor2(2);
        svmBase.setWeights(new double[]{w1s[0], w2s[0]});
        svmBase.setBias(biases[0]);
        int[] cls = new int[points.size()];
        for (int i = 0; i < points.size(); i++)
            cls[i] = (int) svmBase.predict(points.get(i)).get(0);

        // Scatter +1
        html.append("{x:[");
        for (int i = 0; i < points.size(); i++) if (cls[i] == 1) html.append(x1[i]).append(",");
        html.append("],y:[");
        for (int i = 0; i < points.size(); i++) if (cls[i] == 1) html.append(x2[i]).append(",");
        html.append("],mode:'markers',type:'scatter',name:'+1',marker:{color:'blue',size:10}},");
        // Scatter -1
        html.append("{x:[");
        for (int i = 0; i < points.size(); i++) if (cls[i] == -1) html.append(x1[i]).append(",");
        html.append("],y:[");
        for (int i = 0; i < points.size(); i++) if (cls[i] == -1) html.append(x2[i]).append(",");
        html.append("],mode:'markers',type:'scatter',name:'-1',marker:{color:'red',size:10}},");

        // Barrido automático de líneas de frontera
        int colorIdx = 0;
        String[] colors = {"green", "orange", "purple", "magenta", "cyan", "brown", "black"};
        for (double w1 : w1s) {
            for (double w2 : w2s) {
                for (double b : biases) {
                    // Calcula la línea de frontera para este set de parámetros
                    double[] xline = new double[]{-1.0, 4.0};
                    double[] yline = new double[2];
                    for (int i = 0; i < xline.length; i++)
                        yline[i] = (-w1 * xline[i] - b) / w2;

                    // Añade la línea al plot
                    String col = colors[colorIdx % colors.length];
                    colorIdx++;
                    html.append("{x:[").append(xline[0]).append(",").append(xline[1])
                        .append("],y:[").append(yline[0]).append(",").append(yline[1])
                        .append("],mode:'lines',type:'scatter',name:'w1=").append(w1)
                        .append(", w2=").append(w2).append(", b=").append(b)
                        .append("',line:{color:'").append(col).append("',width:2}},");
                }
            }
        }

        // Limpia última coma y cierra arrays
        if (html.charAt(html.length() - 1) == ',') html.setLength(html.length() - 1);

        html.append("];Plotly.newPlot('plot',data,{title:'SVM Decision Boundaries (Auto Sweep)',xaxis:{title:'x1'},yaxis:{title:'x2'}});</script></body></html>");

        // Mostrar en navegador
        PlotlyBrowserViewer.showInBrowser(html.toString());
    }
}
