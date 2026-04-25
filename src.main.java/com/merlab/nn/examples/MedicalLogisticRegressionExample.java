package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataLoader;
import com.merlab.signals.data.DataLoaderConfig;
import com.merlab.signals.data.DataLoaderFactory;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.trainer.BackpropLogisticTrainer;
import com.merlab.signals.nn.trainer.LogisticTrainer;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class MedicalLogisticRegressionExample {

    public static void main(String[] args) throws Exception {
        // 1) Carga CSV: cada fila: feature1,feature2,label(0/1)
        DataLoaderConfig cfg = new DataLoaderConfig();
        cfg.setCsvPath("src/main/resources/data/medical_data.csv");
        cfg.setNumInputs(2);
        cfg.setNumTargets(1);
        DataLoader loader = DataLoaderFactory.create(DataLoaderFactory.Type.CSV, cfg);
        DataSet ds = loader.load();

        // 2) Construye el procesador logístico (pesos iniciales aleatorios, bias=0)
        Random rnd = new Random(42);
        double[] w0 = { rnd.nextGaussian() * 0.1, rnd.nextGaussian() * 0.1 };
        LogisticRegressionProcessor model = new LogisticRegressionProcessor(w0, 0.0);

        // 3) Entrena con backprop (ajusta w,b)
        LogisticTrainer trainer = new BackpropLogisticTrainer();
        int epochs = 2000;
        double lr   = 0.1;
        LogisticRegressionProcessor trained = trainer.train(model, ds, epochs, lr);

        // 4) Evalúa accuracy y separa puntos por clase
        int correct = 0;
        List<Double> xs0 = new ArrayList<>(), ys0 = new ArrayList<>();
        List<Double> xs1 = new ArrayList<>(), ys1 = new ArrayList<>();
        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal in = ds.getInputs().get(i);
            int trueLabel = ds.getTargets().get(i).getValues().get(0).intValue();
            double p = trained.predict(in).getValues().get(0);
            int predLabel = p > 0.5 ? 1 : 0;
            if (predLabel == trueLabel) correct++;
            if (trueLabel == 0) {
                xs0.add(in.getValues().get(0));
                ys0.add(in.getValues().get(1));
            } else {
                xs1.add(in.getValues().get(0));
                ys1.add(in.getValues().get(1));
            }
        }
        double accuracy = 100.0 * correct / ds.getInputs().size();
        System.out.printf("Accuracy final: %.2f%%%n", accuracy);

        // 5) Reporte del modelo
        ModelInfo info = new ModelInfo.Builder("Regresión Logística Médica")
            .addLayer(2, 1, "Sigmoide")   // una sola "capa"
            .epochs(epochs)
            .learningRate(lr)
            .accuracy(accuracy)
            .build();
        ModelReporter.report(info);

        // 6) Genera grid para frontera de decisión p ≈ 0.5
        int G = 50;
        double min = ds.getInputs().stream()
                      .mapToDouble(s -> s.getValues().get(0)).min().orElse(-3);
        double max = ds.getInputs().stream()
                      .mapToDouble(s -> s.getValues().get(0)).max().orElse( 3);
        List<Double> fx = new ArrayList<>(), fy = new ArrayList<>();
        for (double gx : IntStream.range(0, G).mapToDouble(i -> min + (max-min)*i/(G-1)).toArray()) {
            for (double gy : IntStream.range(0, G).mapToDouble(i -> min + (max-min)*i/(G-1)).toArray()) {
                Signal s = new Signal(); s.add(gx); s.add(gy);
                double p = trained.predict(s).getValues().get(0);
                if (Math.abs(p - 0.5) < 0.02) {
                    fx.add(gx);
                    fy.add(gy);
                }
            }
        }

        // 7) Monta HTML Plotly inline
        String html = """
            <html><head>
              <script src="plotly.min.js"></script>
            </head><body>
              <div id="chart" style="width:800px;height:600px;"></div>
              <script>
                var trace0 = { x:%s, y:%s, mode:'markers', name:'Sanos',   marker:{color:'blue'} };
                var trace1 = { x:%s, y:%s, mode:'markers', name:'Enfermos',marker:{color:'red'}  };
                var trace2 = { x:%s, y:%s, mode:'markers', name:'Frontera',marker:{color:'black',size:4,opacity:0.5} };
                Plotly.newPlot('chart',[trace0,trace1,trace2], {
                  title:'Regresión Logística Médica',
                  xaxis:{title:'Feature 1'},
                  yaxis:{title:'Feature 2'}
                });
              </script>
            </body></html>
            """.formatted(
                xs0.toString(), ys0.toString(),
                xs1.toString(), ys1.toString(),
                fx.toString(),  fy.toString()
            );

        // 8) Abre en el navegador
        PlotlyBrowserViewer.showInBrowser(html);
    }
}
