package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.ml.SVMProcessor2;
import com.merlab.signals.ml.SVMTrainer3;
import com.merlab.signals.ml.KernelPerceptronTrainer;
import com.merlab.signals.reporter.ModelInfo;
import com.merlab.signals.reporter.ModelReporter;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.io.IOException;
import java.util.*;

public class KernelSVMRBFExample2 {
    public static void main(String[] args) throws IOException {
        Random rand = new Random(123);
        int N = 40;
        List<Signal> inputs = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();
        // Clase +1: círculo interno
        for (int i = 0; i < N; i++) {
            double r = 0.5 + 0.15 * rand.nextGaussian();
            double theta = 2 * Math.PI * rand.nextDouble();
            double x = r * Math.cos(theta);
            double y = r * Math.sin(theta);
            inputs.add(new Signal(Arrays.asList(x, y)));
            targets.add(new Signal(Arrays.asList(1.0)));
        }
        // Clase -1: anillo externo
        for (int i = 0; i < N; i++) {
            double r = 1.2 + 0.15 * rand.nextGaussian();
            double theta = 2 * Math.PI * rand.nextDouble();
            double x = r * Math.cos(theta);
            double y = r * Math.sin(theta);
            inputs.add(new Signal(Arrays.asList(x, y)));
            targets.add(new Signal(Arrays.asList(-1.0)));
        }

        DataSet data = DataSetBuilder.fromSignals(inputs, targets);

        // Inicializa SVMProcessor2
        SVMProcessor2 svm = new SVMProcessor2(2);


        // Entrena con kernel RBF
        double gamma = 2.0;   // Puedes ajustar  0.5, 1.0, 2.0, 5.0
        int epochs = 20;
        //KernelPerceptronTrainer trainer = new KernelPerceptronTrainer(epochs, gamma);
        
        SVMTrainer3 trainer = new SVMTrainer3(
        	    500,    // maxEpochs  100
        	    10.0,    // C (regularización) 1.0
        	    1e-3,   // tol
        	    2.0     // gamma
        	);
        svm = trainer.train(svm, data, 100, 2.0);
        //svm = trainer.train(svm, data, epochs, gamma);

        // Evaluación: accuracy en training set
        int correct = 0;
        for (int i = 0; i < inputs.size(); i++) {
            double[] xi = {inputs.get(i).get(0), inputs.get(i).get(1)};
            double pred = svm.predictKernelWithBias(xi); // implementa este método abajo
            double yTrue = targets.get(i).get(0);
            double yPred = pred >= 0 ? 1.0 : -1.0;
            if (yPred == yTrue) correct++;
        }
        double accuracy = correct / (double) inputs.size();

        // Reporte
        ModelInfo info = new ModelInfo.Builder("Kernel Perceptron (RBF)")
                .epochs(epochs)
                .learningRate(gamma)
                .accuracy(accuracy)
                .mse(0.0)
                .build();
        ModelReporter.report(info);

        // Visualización de frontera
        List<Double> xPlus = new ArrayList<>(), yPlus = new ArrayList<>();
        List<Double> xMinus = new ArrayList<>(), yMinus = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            double label = targets.get(i).get(0);
            double x = inputs.get(i).get(0);
            double y = inputs.get(i).get(1);
            if (label == 1.0) { xPlus.add(x); yPlus.add(y); }
            else              { xMinus.add(x); yMinus.add(y); }
        }

        // Crea un grid para visualizar la frontera
        int gridN = 100;
        double[] xs = new double[gridN];
        double[] ys = new double[gridN];
        for (int i = 0; i < gridN; i++) {
            xs[i] = -2 + 4.0 * i / (gridN - 1);
            ys[i] = -2 + 4.0 * i / (gridN - 1);
        }
        double[][] z = new double[gridN][gridN];
        for (int ix = 0; ix < gridN; ix++) {
            for (int iy = 0; iy < gridN; iy++) {
                double[] pt = {xs[ix], ys[iy]};
                z[iy][ix] = svm.predictKernelWithBias(pt);
            }
        }
        // Genera un string para Plotly (nivel intermedio)
        StringBuilder gridData = new StringBuilder();
        gridData.append("[");
        for (int iy = 0; iy < gridN; iy++) {
            gridData.append("[");
            for (int ix = 0; ix < gridN; ix++) {
                gridData.append(String.format("%.6f", z[iy][ix]));
                if (ix < gridN - 1) gridData.append(",");
            }
            gridData.append("]");
            if (iy < gridN - 1) gridData.append(",");
        }
        gridData.append("]");
        



        // Plotly HTML
        String html = """
        <html>
          <head>
            <meta charset="utf-8"/>
            <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
          </head>
          <body>
            <div id="chart" style="width:800px;height:700px;"></div>
            <script>
              const x = %s;
              const y = %s;
              const z = %s;
              const plus = {x: %s, y: %s, mode: 'markers', type: 'scatter', name: '+1', marker: {color:'blue', size:7}};
              const minus = {x: %s, y: %s, mode: 'markers', type: 'scatter', name: '-1', marker: {color:'red', size:7}};
              const contour = {
        		  x: x, y: y, z: z, type: 'contour',
        		  showscale: false, colorscale: 'RdBu',
        		  contours: {
        		    showlines: true,
        		    coloring: 'lines',
        		    start: 0, end: 0, size: 1
        		  },
        		  line: {width: 3, color: 'black'},
        		  zmin: -2, zmax: 2  // Limita el rango de color para que no haya saturación
        	  };
              Plotly.newPlot('chart', [contour, plus, minus], {
                title:'Kernel SVM RBF: Non-linear Decision Boundary',
                xaxis: {title:'x1', range:[-2,2]},
                yaxis: {title:'x2', range:[-2,2]}
              });
            </script>
          </body>
        </html>
        """.formatted(
                Arrays.toString(xs),
                Arrays.toString(ys),
                gridData,
                xPlus.toString(), yPlus.toString(),
                xMinus.toString(), yMinus.toString()
        );

        PlotlyBrowserViewer.showInBrowser(html);
    }
}

/*
const contour = {
        x: x, y: y, z: z, type: 'contour',
        showscale: false, colorscale:'RdBu',
        contours: {showlines: true, start:0, end:0, size:1},
        line: {width: 3, color:'black'}
      };
*/
