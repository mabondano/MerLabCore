package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.ml.SVMProcessor2;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

public class SVMPlotlyExample2 {
    public static void main(String[] args) throws IOException {
        // ========== OPCIÓN 2: Cambia estos valores para rotar/trasladar la frontera ==========
        double w1 = 1.0;  // Prueba 2.0, -1.0, etc.
        double w2 = -1.0; // Prueba -2.0, 1.0, etc.
        double bias = 0.0; // Prueba 1.0, -1.0, etc.

        // ========== OPCIÓN 3: Mueve la frontera para que pase por (xF, yF) ==========
        // Descomenta estas líneas para calcular el bias que pase por ese punto
        /*
        double xF = 2.0, yF = 2.0;
        bias = -(w1 * xF + w2 * yF); // Calcula bias para que la frontera pase por (xF, yF)
        */

        // Crea el SVM con los pesos definidos
        SVMProcessor2 svm = new SVMProcessor2(2);
        svm.setWeights(new double[]{w1, w2});
        svm.setBias(bias);

        // ========== OPCIÓN 1: Agrega/quita tus puntos aquí ==========
        List<Signal> points = new ArrayList<>();
        points.add(new Signal(Arrays.asList(2.0, 1.0))); // Esperado +1
        points.add(new Signal(Arrays.asList(1.0, 3.0))); // Esperado -1
        points.add(new Signal(Arrays.asList(3.0, 0.5))); // Esperado +1
        points.add(new Signal(Arrays.asList(0.5, 2.5))); // Esperado -1
        // Puedes experimentar con más puntos:
        points.add(new Signal(Arrays.asList(2.5, 2.5))); // Cerca de la frontera
        points.add(new Signal(Arrays.asList(1.5, 1.2))); // Cambia para experimentar

        // Prepara arrays para Plotly y clasifica
        double[] x1 = new double[points.size()];
        double[] x2 = new double[points.size()];
        int[] cls = new int[points.size()];
        for (int i = 0; i < points.size(); i++) {
            Signal pt = points.get(i);
            x1[i] = pt.get(0);
            x2[i] = pt.get(1);
            cls[i] = (int) svm.predict(pt).get(0);
        }

        // ========== OPCIÓN 4: Ver los cambios en la gráfica ==========
        // Calcula frontera: w1*x + w2*y + b = 0  =>  y = ( -w1*x - b ) / w2
        double[] xline = new double[]{-1.0, 4.0}; // rango x para la línea (ajusta si lo necesitas)
        double[] yline = new double[2];
        for (int i = 0; i < xline.length; i++) {
            yline[i] = ( -w1 * xline[i] - bias ) / w2;
        }

        // Construcción HTML Plotly
        StringBuilder html = new StringBuilder();
        html.append("<html><head>")
            .append("<script src='https://cdn.plot.ly/plotly-latest.min.js'></script></head><body>")
            .append("<div id='plot'></div>")
            .append("<script>let data=[");

        // Scatter clase +1
        html.append("{x:[");
        for (int i = 0; i < points.size(); i++) if (cls[i] == 1) html.append(x1[i]).append(",");
        html.append("],y:[");
        for (int i = 0; i < points.size(); i++) if (cls[i] == 1) html.append(x2[i]).append(",");
        html.append("],mode:'markers',type:'scatter',name:'+1',marker:{color:'blue',size:10}},");
        // Scatter clase -1
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

        // Mostrar en el navegador
        PlotlyBrowserViewer.showInBrowser(html.toString());
    }
}
