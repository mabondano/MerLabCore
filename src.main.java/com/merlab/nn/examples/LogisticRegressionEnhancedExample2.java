package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.data.DataLoader;
import com.merlab.signals.data.DataLoaderConfig;
import com.merlab.signals.data.DataLoaderFactory;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.SimpleLogisticRegressionProcessor;
import com.merlab.signals.nn.trainer.BackpropLogisticTrainer;
import com.merlab.signals.nn.trainer.LogisticTrainer;
import com.merlab.signals.nn.trainer.SimpleBackpropLogisticTrainer;
import com.merlab.signals.nn.trainer.SimpleLogisticTrainer;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Ejemplo de regresión logística sintética (círculos concéntricos),
 * dibujando los puntos y la frontera p≈0.5 con Plotly en un WebView/Browser.
 */
public class LogisticRegressionEnhancedExample2 {

    public static void main(String[] args) throws Exception {
        // --------------------------------------------
        // 1) Generar datos sintéticos
        // --------------------------------------------
        int N = 200;
        double innerRadius = 1.0, outerRadius = 2.0;
        double[][] raw = new double[N][3]; // cada fila: [x, y, label]
        Random rnd = new Random(42);

        for (int i = 0; i < N; i++) {
            double angle = rnd.nextDouble() * 2 * Math.PI;
            double r = (i < N / 2)
                    ? innerRadius * Math.sqrt(rnd.nextDouble())
                    : outerRadius + rnd.nextDouble() * 0.5;
            double x = r * Math.cos(angle);
            double y = r * Math.sin(angle);
            int label = (r <= innerRadius) ? 0 : 1;
            raw[i] = new double[]{ x, y, label };
        }
        // Construimos DataSet: las dos primeras columnas son features (x,y), la tercera es la etiqueta (0/1).
        DataSet ds = DataSetBuilder.fromArray(raw, 2);

        // --------------------------------------------
        // 2) Construir el procesador de regresión logística
        //    (una sola capa con activación sigmoide)
        // --------------------------------------------
        // Capa de 1 neurona (salida), 2 entradas (x,y), con Sigmoid
        Layer logLayer = initLayer(1, 2, new Random(1), ActivationFunctions.SIGMOID);
        SimpleLogisticRegressionProcessor logreg = new SimpleLogisticRegressionProcessor(logLayer);

        // --------------------------------------------
        // 3) Entrenar con BackpropLogisticTrainer
        // --------------------------------------------
        SimpleLogisticTrainer trainer = new SimpleBackpropLogisticTrainer();
        int epochs = 2000;
        double lr = 0.5;
        int batchSize = 16;

        List<Integer> idx = IntStream.range(0, N).boxed().collect(Collectors.toList());
        for (int e = 1; e <= epochs; e++) {
            Collections.shuffle(idx, rnd);

            for (int i = 0; i < N; i += batchSize) {
                int end = Math.min(N, i + batchSize);
                // Sub-lista de índices [i, end)
                List<Integer> batchIdx = idx.subList(i, end);

                // Construimos DataSet de mini‐batch:
                List<Signal> xb = batchIdx.stream()
                        .map(ds.getInputs()::get)
                        .collect(Collectors.toList());
                List<Signal> yb = batchIdx.stream()
                        .map(ds.getTargets()::get)
                        .collect(Collectors.toList());
                DataSet batch = new DataSet(xb, yb);

                // Llamamos a train() para entrenar 1 epoch sobre ese mini‐batch
                logreg = (SimpleLogisticRegressionProcessor) trainer.train(logreg, batch, 1, lr);
            }

            if (e % 500 == 0) {
                double acc = computeAccuracy(logreg, ds);
                System.out.printf("Epoch %4d/%d  acc=%.2f%%%n", e, epochs, acc * 100);
            }
        }

        // --------------------------------------------
        // 4) Calcular accuracy final y puntos para graficar
        // --------------------------------------------
        double accuracy = computeAccuracy(logreg, ds) * 100.0;
        System.out.printf("Accuracy final: %.2f%%%n", accuracy);

        // Dividimos los puntos originales en dos grupos: clase 0 y clase 1
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            Signal in = ds.getInputs().get(i);
            int trueLabel = ds.getTargets().get(i).getValues().get(0).intValue();
            if (trueLabel == 0) {
                x0.add(in.getValues().get(0));
                y0.add(in.getValues().get(1));
            } else {
                x1.add(in.getValues().get(0));
                y1.add(in.getValues().get(1));
            }
        }

	     // --- Después de haber entrenado ’logreg’ (SimpleLogisticRegressionProcessor) ---
	     // w1  = peso para x
	     // w2  = peso para y
	     // b   = bias
	     //
	     // Toma el rango de dibujo de tu ejemplo; acostumbramos usar [-2, 2] en x e y.
         // HTML2
	     double R = 2.0;
	     int G = 100; // número de divisiones por eje (puedes subirlo para más “resolución”)
	
	     List<Double> fx = new ArrayList<>();
	     List<Double> fy = new ArrayList<>();
	
	     for (int i = 0; i < G; i++) {
	         double x = -R + (2*R) * i / (G-1);
	         for (int j = 0; j < G; j++) {
	             double y = -R + (2*R) * j / (G-1);
	
	             // Calcula la probabilidad predicha en (x,y)
	             Signal s = new Signal();
	             s.add(x);
	             s.add(y);
	             double p = logreg.predict(s).getValues().get(0);
	
	             // Si ‘p’ está cerca de 0.5, consideramos que ese punto está en la frontera
	             if (Math.abs(p - 0.5) < 0.01) {
	                 fx.add(x);
	                 fy.add(y);
	             }
	         }
	     }

     
        // Calcular “frontera” aproximada (p≈0.5) iterando solo sobre y=0 para cada x
        // (por simplicidad, tomamos solo el eje horizontal y marcamos si p≈0.5)
	    // HTML
	     
        List<Double> fx0 = new ArrayList<>(), fy0 = new ArrayList<>();
        List<Double> fx1 = new ArrayList<>(), fy1 = new ArrayList<>();

        // Creamos una malla 1D en x ∈ [−outerRadius, outerRadius], para y=0
        for (double xx = -outerRadius; xx <= outerRadius; xx += 0.02) {
            Signal s = new Signal();
            s.add(xx);
            s.add(0.0);
            double p = logreg.predict(s).getValues().get(0);
            if (Math.abs(p - 0.5) < 0.02) {
                fx0.add(xx);
                fy0.add(0.0);
            }
        }
        
        // (Opcional) Para reforzar visualmente, podrías añadir también algunos puntos exactamente
        // en y cercana a ±0 para cada x, si quieres una “línea más gruesa”. 
        // Pero con este conjunto basta para ver la curva horizontal (y≈0) donde p≈0.5.

        // --------------------------------------------
        // 5) Generar HTML con Plotly (serie “frontera” unificada y más grande)
        // --------------------------------------------
        String html = """
            <html>
            <head>
              <script src="plotly.min.js"></script>
            </head>
            <body>
              <div id="chart" style="width:800px;height:600px;"></div>
              <script>
                // Series de puntos reales
                const x0_data = %s;
                const y0_data = %s;
                const x1_data = %s;
                const y1_data = %s;

                // “Frontera” aproximada: unificamos fx0+fx1 y fy0+fy1
                const fx_data = %s.concat(%s);
                const fy_data = %s.concat(%s);

                const trace0 = {
                  x: x0_data,
                  y: y0_data,
                  mode: 'markers',
                  name: 'Clase 0',
                  marker: { color: 'blue', size: 6, opacity: 0.7 }
                };
                const trace1 = {
                  x: x1_data,
                  y: y1_data,
                  mode: 'markers',
                  name: 'Clase 1',
                  marker: { color: 'orange', size: 6, opacity: 0.7 }
                };
                const traceF = {
                  x: fx_data,
                  y: fy_data,
                  mode: 'markers',
                  name: 'Frontera p≈0.5',
                  marker: { color: 'black', size: 8, opacity: 0.8 }
                };

                Plotly.newPlot('chart', [trace0, trace1, traceF], {
                  title: 'Logistic Regression (sintético círculos)',
                  xaxis: { title: 'x' },
                  yaxis: { title: 'y' }
                });
              </script>
            </body>
            </html>
            """.formatted(
                x0.toString(), y0.toString(),
                x1.toString(), y1.toString(),
                fx0.toString(), fx1.toString(),
                fy0.toString(), fy1.toString()
            );

        String html2 = """
        		  <html>
        		  <head>
        		    <script src="plotly.min.js"></script>
        		  </head>
        		  <body>
        		    <div id="chart" style="width:800px;height:600px;"></div>
        		    <script>
        		      // Datos de clase 0 y clase 1 (igual que antes)...
        		      const x0_data = %s;
        		      const y0_data = %s;
        		      const x1_data = %s;
        		      const y1_data = %s;

        		      // DATOS DE LA FRONTERA 2D:
        		      const fx_data = %s;
        		      const fy_data = %s;

        		      const trace0 = {
        		        x: x0_data, y: y0_data,
        		        mode: 'markers', name: 'Clase 0',
        		        marker: { color:'blue', size:6, opacity:0.7 }
        		      };
        		      const trace1 = {
        		        x: x1_data, y: y1_data,
        		        mode: 'markers', name: 'Clase 1',
        		        marker: { color:'orange', size:6, opacity:0.7 }
        		      };
        		      const traceF = {
        		        x: fx_data, y: fy_data,
        		        mode: 'markers', name: 'Frontera p≈0.5',
        		        marker: { color:'black', size:5, opacity:0.8 }
        		      };

        		      Plotly.newPlot('chart', [trace0, trace1, traceF], {
        		        title: 'Logistic Regression (sintético círculos)',
        		        xaxis: { title: 'x' },
        		        yaxis: { title: 'y' }
        		      });
        		    </script>
        		  </body>
        		  </html>
        		  """.formatted(
        		    x0.toString(), y0.toString(),
        		    x1.toString(), y1.toString(),
        		    fx.toString(), fy.toString()
        		  );

        		

        		
        PlotlyBrowserViewer.showInBrowser(html2);
    }

    private static double computeAccuracy(SimpleLogisticRegressionProcessor model, DataSet ds) {
        long correct = 0;
        int total = ds.getInputs().size();
        for (int i = 0; i < total; i++) {
            Signal in = ds.getInputs().get(i);
            int trueLabel = ds.getTargets().get(i).getValues().get(0).intValue();
            double p = model.predict(in).getValues().get(0);
            int predLabel = (p > 0.5) ? 1 : 0;
            if (predLabel == trueLabel) correct++;
        }
        return correct / (double) total;
    }

    private static Layer initLayer(int neurons, int inputs, Random rnd,
                                   ActivationFunctions act) {
        double[][] w = new double[neurons][inputs];
        double[] b   = new double[neurons];
        for (int i = 0; i < neurons; i++) {
            b[i] = 0.0;
            for (int j = 0; j < inputs; j++) {
                w[i][j] = (rnd.nextDouble() * 2 - 1) * 0.1;
            }
        }
        return new Layer(w, b, act);
    }
}
