package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataLoader;
import com.merlab.signals.data.DataLoaderConfig;
import com.merlab.signals.data.DataLoaderFactory;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.trainer.BackpropLogisticTrainer;
import com.merlab.signals.nn.trainer.LogisticTrainer;
import com.merlab.signals.plot.PlotlyBrowserViewer;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MedicalLogisticRegressionExample2 {

    public static void main(String[] args) throws Exception {
        // 1) Carga CSV: cada fila: edad,glicemia,tensión,label(0/1)
        DataLoaderConfig cfg = new DataLoaderConfig();
        cfg.setCsvPath("src/main/resources/data/medical_data.csv");
        cfg.setNumInputs(2);   // solo dos features para plot 2D
        cfg.setNumTargets(1);
        DataLoader loader = DataLoaderFactory.create(
            DataLoaderFactory.Type.CSV, cfg
        );
        DataSet ds = loader.load();

        // 2) Construye el procesador logístico (pesos iniciales aleatorios)
        Random rnd = new Random(42);
        // w0,w1 inicializados pequeños; bias 0
        double[] w0 = { rnd.nextGaussian()*0.1, rnd.nextGaussian()*0.1 };
        LogisticRegressionProcessor model = new LogisticRegressionProcessor(w0, 0.0);

        // 3) Entrena con backprop (ajusta w,b)
        LogisticTrainer trainer = new BackpropLogisticTrainer();
        int epochs = 2000;
        double lr   = 0.1;
        LogisticRegressionProcessor trained =
            trainer.train(model, ds, epochs, lr);

        // 4) Extrae puntos y frontera de decisión
        int correct = 0;
        
        List<Double> xs0 = new ArrayList<>(), ys0 = new ArrayList<>();
        List<Double> xs1 = new ArrayList<>(), ys1 = new ArrayList<>();
        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal in = ds.getInputs().get(i);
            double label = ds.getTargets().get(i).getValues().get(0);
            double x = in.getValues().get(0), y = in.getValues().get(1);
            if (label < 0.5) { xs0.add(x); ys0.add(y); }
            else            { xs1.add(x); ys1.add(y); }
            
        }
        
        
        double accuracy = 100.0 * correct / ds.getInputs().size();
        System.out.printf("Accuracy final: %.2f%%%n", accuracy);
        
        // 5) Reporte del modelo
        ModelInfo info = new ModelInfo.Builder("Regresión Logística Médica")
            .addLayer( // para regresión logística, consideramos "capa única"
                /* inputs */ 2,
                /* outputs */ 2,
                /* descripción */ "Sigmoide"
            )
            .epochs(epochs)
            .learningRate(lr)
            .accuracy(accuracy)
            .build();
        ModelReporter.report(info);



        // genera grid para frontera
        int G = 50;
        double[] xGrid = IntStream.range(0, G)
                .mapToDouble(i -> -3 + 6.0*i/(G-1)).toArray();
        double[] yGrid = xGrid;
        List<Double> fx = new ArrayList<>(), fy = new ArrayList<>();
        for (double gx : xGrid) for (double gy : yGrid) {
            Signal s = new Signal(); s.add(gx); s.add(gy);
            double p = trained.predict(s).getValues().get(0);
            // marcar cerca de la frontera p≈0.5
            if (Math.abs(p-0.5) < 0.02) {
                fx.add(gx); fy.add(gy);
            }
        }

        // 5) Monta HTML Plotly inline
        String html = """
            <html><head>
              <script src="plotly.min.js"></script>
            </head><body>
              <div id="chart" style="width:800px;height:600px;"></div>
              <script>
                var trace0 = {
                  x:%s, y:%s, mode:'markers',
                  name:'Sanos', marker:{color:'blue'}
                };
                var trace1 = {
                  x:%s, y:%s, mode:'markers',
                  name:'Enfermos', marker:{color:'red'}
                };
                var trace2 = {
                  x:%s, y:%s, mode:'markers',
                  name:'Frontera p=0.5', marker:{size:4,color:'black',opacity:0.5}
                };
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
                fx.toString(), fy.toString()
            );

        // 6) abre en navegador
        PlotlyBrowserViewer.showInBrowser(html);
    }
}
