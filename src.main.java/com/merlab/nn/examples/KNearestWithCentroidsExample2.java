package com.merlab.nn.examples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.ml.KMeansProcessor2;
import com.merlab.signals.ml.KNearestProcessor2;
import com.merlab.signals.plot.PlotlyBrowserViewer;

/**
 * Ejemplo que combina KMeans (para ver centroides) + KNN para clasificación.
 */
public class KNearestWithCentroidsExample2 {

    public static void main(String[] args) throws Exception {
        // 1) Crear datos sintéticos (dos clases, linealmente separables)
        int N = 200;
        double[][] raw = new double[N][3];
        Random rnd = new Random(123);

        for (int i = 0; i < N; i++) {
            double x = rnd.nextDouble() * 4 - 2;   // en [-2,2]
            double y = rnd.nextDouble() * 4 - 2;   // en [-2,2]
            // Etiqueta según la recta y = 0.5*x - 0.2
            int label = (y > 0.5 * x - 0.2) ? 1 : 0;
            raw[i] = new double[]{ x, y, label };
        }

        // 2) Construir DataSet a partir de raw
        DataSet ds = DataSetBuilder.fromArray(raw, 2);
        
        
     // … (previo: ya tienes tu DataSet ds con ds.getInputs() y ds.getTargets())

     // 1) Calcular media (centroide) de clase 0 y clase 1:
     double sumX0 = 0, sumY0 = 0; int count0 = 0;
     double sumX1 = 0, sumY1 = 0; int count1 = 0;

     for (int i = 0; i < ds.getInputs().size(); i++) {
         Signal s = ds.getInputs().get(i);
         int lab = ds.getTargets().get(i).getValues().get(0).intValue();
         double x = s.getValues().get(0);
         double y = s.getValues().get(1);
         if (lab == 0) {
             sumX0 += x; sumY0 += y; count0++;
         } else {
             sumX1 += x; sumY1 += y; count1++;
         }
     }
     double centroideX0 = sumX0 / count0;
     double centroideY0 = sumY0 / count0;
     double centroideX1 = sumX1 / count1;
     double centroideY1 = sumY1 / count1;

     // 2) Extraer listas para graficar puntos:
     List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
     List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();

     for (int i = 0; i < ds.getInputs().size(); i++) {
         Signal s = ds.getInputs().get(i);
         int lab = ds.getTargets().get(i).getValues().get(0).intValue();
         if (lab == 0) {
             x0.add(s.getValues().get(0));
             y0.add(s.getValues().get(1));
         } else {
             x1.add(s.getValues().get(0));
             y1.add(s.getValues().get(1));
         }
     }

     // 3) Convertir centroides a listas de un elemento para Plotly:
     List<Double> cx0 = List.of(centroideX0);
     List<Double> cy0 = List.of(centroideY0);
     List<Double> cx1 = List.of(centroideX1);
     List<Double> cy1 = List.of(centroideY1);

     // 4) Crear HTML + Plotly inline para mostrar:
//          - puntos clase 0 en azul
//          - puntos clase 1 en naranja
//          - centroide clase 0 con triángulo rojo (tamaño grande)
//          - centroide clase 1 con triángulo púrpura (tamaño grande)
     String html = """
         <html><head>
           <script src="plotly.min.js"></script>
         </head><body>
           <div id="chart" style="width:800px;height:600px;"></div>
           <script>
             const x0 = %s, y0 = %s;
             const x1 = %s, y1 = %s;
             const cx0 = %s, cy0 = %s;
             const cx1 = %s, cy1 = %s;

             const trace0 = {
               x: x0, y: y0,
               mode: 'markers',
               name: 'Clase 0',
               marker: { color:'blue', size:6, opacity:0.7 }
             };
             const trace1 = {
               x: x1, y: y1,
               mode: 'markers',
               name: 'Clase 1',
               marker: { color:'orange', size:6, opacity:0.7 }
             };
             const traceC0 = {
               x: cx0, y: cy0,
               mode: 'markers',
               name: 'Centroide 0',
               marker: { color:'red', symbol:'triangle-up', size:14, opacity:0.9 }
             };
             const traceC1 = {
               x: cx1, y: cy1,
               mode: 'markers',
               name: 'Centroide 1',
               marker: { color:'purple', symbol:'triangle-up', size:14, opacity:0.9 }
             };

             Plotly.newPlot('chart',
               [trace0, trace1, traceC0, traceC1],
               {
                 title: 'Puntos y su centroide por clase',
                 xaxis: { title: 'x' },
                 yaxis: { title: 'y' }
               }
             );
           </script>
         </body></html>
         """.formatted(
             x0.toString(),  y0.toString(),
             x1.toString(),  y1.toString(),
             cx0.toString(), cy0.toString(),
             cx1.toString(), cy1.toString()
         );

     PlotlyBrowserViewer.showInBrowser(html);

        
        
 
    }
}
