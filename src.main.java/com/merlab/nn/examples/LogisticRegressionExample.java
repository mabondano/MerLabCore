package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.processor.SimpleLogisticRegressionProcessor;
import com.merlab.signals.nn.trainer.BackpropLogisticTrainer;
import com.merlab.signals.nn.trainer.LogisticTrainer;
import com.merlab.signals.nn.trainer.SimpleBackpropLogisticTrainer;
import com.merlab.signals.nn.trainer.SimpleLogisticTrainer;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Ejemplo completo de regresión logística (una capa + sigmoide) sobre datos sintéticos 2D.
 */
public class LogisticRegressionExample {

    public static void main(String[] args) throws IOException {
        // 1) Generar datos sintéticos de círculos concéntricos
        int N = 200;
        double innerRadius = 1.0, outerRadius = 2.0;
        double[][] raw = new double[N][3]; // [x, y, label]
        Random rnd = new Random(42);

        for (int i = 0; i < N; i++) {
            double angle = rnd.nextDouble() * 2 * Math.PI;
            double r = (i < N/2)
                     ? innerRadius * Math.sqrt(rnd.nextDouble())
                     : outerRadius + rnd.nextDouble() * 0.5;
            double x = r * Math.cos(angle);
            double y = r * Math.sin(angle);
            int label = (r <= innerRadius) ? 0 : 1;
            raw[i] = new double[]{ x, y, label };
        }
        DataSet ds = DataSetBuilder.fromArray(raw, 2);

        // 2) Crear el Layer de una neurona con SIGMOID: w·x + b → σ(·)
        Layer lrLayer = initLayer(
            /* neurons= */ 1,
            /* inputs=  */ 2,
            new Random(1),
            com.merlab.signals.nn.processor.ActivationFunctions.SIGMOID
        );
        // 3) Construir el procesador de regresión logística a partir de ese Layer
        SimpleLogisticRegressionProcessor logreg =
            new SimpleLogisticRegressionProcessor(Objects.requireNonNull(lrLayer));

        // 4) Entrenar con backprop
        SimpleLogisticTrainer trainer = new SimpleBackpropLogisticTrainer();
        int epochs    = 2000;
        double lr     = 0.5;
        int batchSize = 16;
        List<Integer> idx = IntStream.range(0, N).boxed().collect(Collectors.toList());

        for (int e = 1; e <= epochs; e++) {
            Collections.shuffle(idx, rnd);
            for (int i = 0; i < N; i += batchSize) {
                int end = Math.min(N, i + batchSize);
                DataSet batch = new DataSet(
                    idx.subList(i, end).stream()
                          .map(ds.getInputs()::get)
                          .collect(Collectors.toList()),
                    idx.subList(i, end).stream()
                          .map(ds.getTargets()::get)
                          .collect(Collectors.toList())
                );
                logreg = (SimpleLogisticRegressionProcessor) trainer.train(
                    logreg, batch, 1, lr
                );
            }
            if (e % 500 == 0) {
                double acc = computeAccuracy(logreg, ds);
                System.out.printf("Epoch %4d/%d  acc=%.2f%%%n", e, epochs, acc * 100);
            }
        }

        // 5) Recoger puntos reales por etiqueta
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            Signal in = ds.getInputs().get(i);
            int t = ds.getTargets().get(i).getValues().get(0).intValue();
            if (t == 0) {
                x0.add(in.getValues().get(0));
                y0.add(in.getValues().get(1));
            } else {
                x1.add(in.getValues().get(0));
                y1.add(in.getValues().get(1));
            }
        }

        // 6) Generar la frontera de decisión (p≈0.5)
        List<Double> fx = new ArrayList<>(), fy = new ArrayList<>();
        double step = 0.05;
        for (double xx = -outerRadius; xx <= outerRadius; xx += step) {
            for (double yy = -outerRadius; yy <= outerRadius; yy += step) {
                Signal s = new Signal();
                s.add(xx);
                s.add(yy);
                double p = logreg.predict(s).getValues().get(0);
                if (Math.abs(p - 0.5) < 0.02) {
                    fx.add(xx);
                    fy.add(yy);
                }
            }
        }

        // 7) Montar HTML con Plotly inline
        String html = """
            <html><head>
              <script src="plotly.min.js"></script>
            </head><body>
              <div id="chart" style="width:800px;height:600px;"></div>
              <script>
                var real0 = { x:%s, y:%s, mode:'markers', name:'Clase 0', marker:{color:'blue'} };
                var real1 = { x:%s, y:%s, mode:'markers', name:'Clase 1', marker:{color:'orange'} };
                var border = { x:%s, y:%s, mode:'markers',
                               name:'Frontera p=0.5', marker:{color:'purple',size:4,opacity:0.5} };
                Plotly.newPlot('chart',[real0, real1, border], {
                  title:'Regresión Logística (sintético)',
                  xaxis:{title:'x'}, yaxis:{title:'y'}
                });
              </script>
            </body></html>
            """.formatted(
              x0.toString(), y0.toString(),
              x1.toString(), y1.toString(),
              fx.toString(), fy.toString()
            );

        // 8) Mostrar en el navegador
        PlotlyBrowserViewer.showInBrowser(html);
    }

    /**
     * Crea un Layer randomizado:
     *  - neurons: cuántas salidas (aquí 1)
     *  - inputs: cuántas entradas (aquí 2)
     *  - rnd: semilla para inicializar pesos en [-0.1, +0.1]
     *  - act: ActivationFunctions.SIGMOID
     */
    private static Layer initLayer(int neurons, int inputs, Random rnd,
                                   com.merlab.signals.nn.processor.ActivationFunction act) {
        double[][] w = new double[neurons][inputs];
        double[]   b = new double[neurons];
        for (int i = 0; i < neurons; i++) {
            b[i] = 0.0; // bias inicial = 0
            for (int j = 0; j < inputs; j++) {
                w[i][j] = (rnd.nextDouble() * 2 - 1) * 0.1;
            }
        }
        return new Layer(w, b, act);
    }

    /** Calcula la accuracy (%) sobre todo el DataSet */
    private static double computeAccuracy(SimpleLogisticRegressionProcessor model, DataSet ds) {
        long correct = 0;
        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal in    = ds.getInputs().get(i);
            int trueLbl  = ds.getTargets().get(i).getValues().get(0).intValue();
            double p     = model.predict(in).getValues().get(0);
            int predLbl  = (p > 0.5) ? 1 : 0;
            if (predLbl == trueLbl) {
                correct++;
            }
        }
        return correct / (double) ds.getInputs().size();
    }
   /*   
    private static double computeAccuracy2(LogisticRegressionProcessor model, DataSet ds) {
        long correct = 0;
        for (int i = 0; i < ds.getInputs().size(); i++) {
            double p    = model.predict(ds.getInputs().get(i)).getValues().get(0);
            int pred    = p > 0.5 ? 1 : 0;
            int actual  = ds.getTargets().get(i).getValues().get(0).intValue();
            if (pred == actual) correct++;
        }
        return correct / (double) ds.getInputs().size();
    }
    */
}





    


