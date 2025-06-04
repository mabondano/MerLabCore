package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.ml.SVMProcessor2;
import com.merlab.signals.nn.trainer.Trainer2;
import com.merlab.signals.nn.trainer.TrainerFactory2;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.io.IOException;
import java.util.*;

public class SVMTrainerExample {
    public static void main(String[] args) throws IOException {
        Random rand = new Random(123);
        int N = 15;
        List<Signal> inputs = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            double x = 2.0 + 0.6 * rand.nextGaussian();
            double y = 2.0 + 0.6 * rand.nextGaussian();
            inputs.add(new Signal(Arrays.asList(x, y)));
            targets.add(new Signal(Arrays.asList(1.0)));
        }
        for (int i = 0; i < N; i++) {
            double x = -2.0 + 0.6 * rand.nextGaussian();
            double y = -2.0 + 0.6 * rand.nextGaussian();
            inputs.add(new Signal(Arrays.asList(x, y)));
            targets.add(new Signal(Arrays.asList(-1.0)));
        }

        DataSet data = DataSetBuilder.fromSignals(inputs, targets);

        // Inicializa el SVM
        SVMProcessor2 svm = new SVMProcessor2(2);
        svm.setWeights(new double[]{0.0, 0.0});
        svm.setBias(0.0);

        // Entrenamiento
        Trainer2<SVMProcessor2> trainer = TrainerFactory2.create(TrainerFactory2.Algorithm.SVM_LINEAR);
        int epochs = 60;
        double learningRate = 0.01;
        svm = trainer.train(svm, data, epochs, learningRate);

        double[] w = svm.getWeights();
        double b = svm.getBias();

        // --- Calcular Accuracy ---
        int correct = 0;
        for (int i = 0; i < inputs.size(); i++) {
            double[] features = new double[]{inputs.get(i).get(0), inputs.get(i).get(1)};
            double yTrue = targets.get(i).get(0);
            double pred = (w[0]*features[0] + w[1]*features[1] + b >= 0) ? 1.0 : -1.0;
            if (pred == yTrue) correct++;
        }
        double accuracy = correct / (double) inputs.size();

        // --- Construir ModelInfo ---
        ModelInfo info = new ModelInfo.Builder("SVM Lineal (Entrenado)")
                .epochs(epochs)
                .learningRate(learningRate)
                .accuracy(accuracy)
                .mse(0.0) // No aplica para clasificación binaria, pero requerido por builder
                .build();

        // --- Reporte en consola ---
        ModelReporter.report(info);

        // --- Visualización (idéntica a la anterior) ---
        List<Double> xPlus = new ArrayList<>(), yPlus = new ArrayList<>();
        List<Double> xMinus = new ArrayList<>(), yMinus = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            double label = targets.get(i).get(0);
            double x = inputs.get(i).get(0);
            double y = inputs.get(i).get(1);
            if (label == 1.0) { xPlus.add(x); yPlus.add(y); }
            else              { xMinus.add(x); yMinus.add(y); }
        }
        double[] xLine = {-4.0, 4.0};
        double[] yDecision = new double[2], yMargin1 = new double[2], yMargin2 = new double[2];
        for (int i = 0; i < xLine.length; i++) {
            yDecision[i] = (w[1] == 0.0) ? 0.0 : (-w[0]*xLine[i] - b)/w[1];
            yMargin1[i] = (w[1] == 0.0) ? 0.0 : (-w[0]*xLine[i] - b + 1)/w[1];
            yMargin2[i] = (w[1] == 0.0) ? 0.0 : (-w[0]*xLine[i] - b - 1)/w[1];
        }

        String html = """
        <html>
          <head>
            <meta charset="utf-8"/>
            <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
          </head>
          <body>
            <div id="chart" style="width:800px;height:600px;"></div>
            <script>
              const plus = {x: %s, y: %s, mode: 'markers', type: 'scatter', name: '+1', marker: {color:'blue', size:8}};
              const minus = {x: %s, y: %s, mode: 'markers', type: 'scatter', name: '-1', marker: {color:'red', size:8}};
              const frontier = {x: [%f,%f], y: [%f,%f], mode:'lines', type:'scatter', name:'Decision Boundary', line:{color:'green', width:3}};
              const margin1 = {x: [%f,%f], y: [%f,%f], mode:'lines', type:'scatter', name:'Margin +1', line:{color:'black', width:2, dash:'dash'}};
              const margin2 = {x: [%f,%f], y: [%f,%f], mode:'lines', type:'scatter', name:'Margin -1', line:{color:'black', width:2, dash:'dash'}};
              Plotly.newPlot('chart', [plus, minus, frontier, margin1, margin2], {
                title:'SVM Decision Boundary & Margins (Trained)',
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

