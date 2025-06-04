package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.ml.SVMProcessor2;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SVMPlotlyExample {
    public static void main(String[] args) throws IOException {
        // SVM setup (2D)
        SVMProcessor2 svm = new SVMProcessor2(2);
        svm.setWeights(new double[]{1.0, -1.0});
        svm.setBias(0.0);

        // Puntos sintéticos
        Signal p1 = new Signal(Arrays.asList(2.0, 1.0)); // +1
        Signal p2 = new Signal(Arrays.asList(1.0, 3.0)); // -1
        Signal p3 = new Signal(Arrays.asList(3.0, 0.5)); // +1
        Signal p4 = new Signal(Arrays.asList(0.5, 2.5)); // -1

        List<Signal> points = Arrays.asList(p1, p2, p3, p4);

        // Clasifica para colorear
        double[] x1 = new double[points.size()];
        double[] x2 = new double[points.size()];
        int[] cls = new int[points.size()];
        for (int i = 0; i < points.size(); i++) {
            Signal pt = points.get(i);
            x1[i] = pt.get(0);
            x2[i] = pt.get(1);
            cls[i] = (int) svm.predict(pt).get(0);
        }

        // Define frontera: w1*x + w2*y + b = 0  => y = ( -w1*x - b ) / w2
        double w1 = 1.0, w2 = -1.0, b = 0.0;
        double[] xline = new double[]{0.0, 3.5};
        double[] yline = new double[2];
        for (int i = 0; i < xline.length; i++) {
            yline[i] = ( -w1 * xline[i] - b ) / w2;
        }

        // Construye el HTML Plotly
        StringBuilder html = new StringBuilder();
        html.append("<html><head>")
            .append("<script src='https://cdn.plot.ly/plotly-latest.min.js'></script></head><body>")
            .append("<div id='plot'></div>")
            .append("<script>let data=[");

        // Scatter puntos clase +1
        html.append("{x:[");
        for (int i = 0; i < points.size(); i++) if (cls[i] == 1) html.append(x1[i]).append(",");
        html.append("],y:[");
        for (int i = 0; i < points.size(); i++) if (cls[i] == 1) html.append(x2[i]).append(",");
        html.append("],mode:'markers',type:'scatter',name:'+1',marker:{color:'blue',size:10}},");
        // Scatter puntos clase -1
        html.append("{x:[");
        for (int i = 0; i < points.size(); i++) if (cls[i] == -1) html.append(x1[i]).append(",");
        html.append("],y:[");
        for (int i = 0; i < points.size(); i++) if (cls[i] == -1) html.append(x2[i]).append(",");
        html.append("],mode:'markers',type:'scatter',name:'-1',marker:{color:'red',size:10}},");

        // Frontera de decisión
        html.append("{x:[").append(xline[0]).append(",").append(xline[1])
            .append("],y:[").append(yline[0]).append(",").append(yline[1])
            .append("],mode:'lines',type:'scatter',name:'Decision Boundary',line:{color:'green',width:2}}");

        html.append("];Plotly.newPlot('plot',data,{title:'SVM Decision Boundary',xaxis:{title:'x1'},yaxis:{title:'x2'}});</script></body></html>");

        // Mostrar en navegador
        PlotlyBrowserViewer.showInBrowser(html.toString());
    }
}
