package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.*;
import com.merlab.signals.nn.processor.*;
import com.merlab.signals.nn.trainer.*;
import com.merlab.signals.preprocessing.StandardScaler;
import com.merlab.signals.reporter.*;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * HousePriceRegressionExample13
 * - Normaliza X e Y
 * - Entrena MLP con BatchNorm + Dropout
 * - Desnormaliza y grafica
 */
public class HousePriceRegressionExample13 {

    public static void main(String[] args) throws Exception {
        // 1) Cargar CSV
        DataLoaderConfig cfg = new DataLoaderConfig();
        cfg.setCsvPath("src/main/resources/data/house_prices_large.csv");
        cfg.setNumInputs(3);
        cfg.setNumTargets(1);
        DataLoader loader = DataLoaderFactory.create(
            DataLoaderFactory.Type.CSV, cfg
        );
        DataSet ds = loader.load();

        // 1b) Normalizar X e Y
        StandardScaler scalerX = new StandardScaler();
        scalerX.fit(ds.getInputs());
        List<Signal> normX = scalerX.transform(ds.getInputs());

        StandardScaler scalerY = new StandardScaler();
        scalerY.fit(ds.getTargets());
        List<Signal> normY = scalerY.transform(ds.getTargets());

        DataSet dsNorm = new DataSet(normX, normY);

        // 2) Definir MLP: 3→32(ReLU)→BN→DO→16(ReLU)→BN→DO→1
        Random rnd = new Random(123);
        Layer l1 = initLayer(32, 3, rnd, ActivationFunctions.RELU);
        Layer bn1 = new BatchNormLayer(32);
        Layer do1 = new DropoutLayer(0.8,  42L, 32);

        Layer l2 = initLayer(16, 32, rnd, ActivationFunctions.RELU);
        Layer bn2 = new BatchNormLayer(16);
        Layer do2 = new DropoutLayer(0.8,  42L, 16);

        Layer out = initLayer(1, 16, rnd, ActivationFunctions.IDENTITY);

        MultiLayerPerceptronProcessor mlp = new MultiLayerPerceptronProcessor(
            List.of(l1, bn1, do1, l2, bn2, do2, out)
        );

        // 3) Entrenamiento
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        int epochs = 3000, batchSize = 16, N = dsNorm.getInputs().size();
        double lr = 0.01;
        List<Integer> idx = IntStream.range(0, N).boxed().collect(Collectors.toList());

        for (int e = 1; e <= epochs; e++) {
            mlp.setMode(Mode.TRAIN);
            Collections.shuffle(idx, rnd);

            for (int i = 0; i < N; i += batchSize) {
                int end = Math.min(N, i + batchSize);
                List<Signal> inB = idx.subList(i, end).stream()
                    .map(dsNorm.getInputs()::get).toList();
                List<Signal> tgB = idx.subList(i, end).stream()
                    .map(dsNorm.getTargets()::get).toList();
                DataSet batch = new DataSet(inB, tgB);
                mlp = trainer.train(mlp, batch, 1, lr);
            }

            if (e == 1 || e == epochs || e % 500 == 0) {
                mlp.setMode(Mode.INFERENCE);
                double mse = computeMSE(mlp, dsNorm);
                System.out.printf("Epoch %4d/%d  lr=%.3f  MSE=%.4f%n",
                                  e, epochs, lr, mse);
            }
        }

        // 4) Evaluación final e informe
        mlp.setMode(Mode.INFERENCE);
        double finalMse = computeMSE(mlp, dsNorm);
        System.out.printf("MSE final (norm): %.4f%n", finalMse);
        
        double r2 = computeRSquared(mlp, dsNorm);
        ModelInfo info = new ModelInfo.Builder("HousePrice MLP BN+DO")
            .addLayer(3, 32, "ReLU + BN + Dropout(0.8)")
            .addLayer(32, 16, "ReLU + BN + Dropout(0.8)")
            .addLayer(16, 1,  "Identity")
            .epochs(epochs)
            .learningRate(lr)
            .mse(finalMse)
            .r2(r2)            // si añades este método al Builder
            .build();

        ModelReporter.report(info);

        // 5) Predecir, desnormalizar y graficar
        List<Signal> predsNorm = dsNorm.getInputs().stream()
            .map(mlp::predict).toList();
        List<Signal> preds     = scalerY.inverseTransform(predsNorm);

        List<Double> xData = new ArrayList<>(N),
                       yReal = new ArrayList<>(N),
                       yPred = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            xData.add((double) i);
            yReal.add(ds.getTargets().get(i).getValues().get(0));
            yPred.add(preds.get(i).getValues().get(0));
        }
        /*
        String html = """
            <html>
            <head>
              <script src="plotly.min.js"></script>
            </head><body>
              <div id="chart" style="width:800px;height:600px;"></div>
              <script>
                const x     = %s;
                const yReal = %s;
                const yPred = %s;
                Plotly.newPlot('chart',[
                  { x:x, y:yReal, mode:'lines+markers', name:'Real',  line:{color:'blue'} },
                  { x:x, y:yPred, mode:'lines+markers', name:'Pred.', line:{color:'red'}  }
                ],{ title:'Precio Casas con BN+DO', xaxis:{title:'Índice'}, yaxis:{title:'Precio'} });
              </script>
            </body></html>
            """.formatted(xData, yReal, yPred);
            */
        /*
        String html = """
        	    <html>
        	    <head>
        	      <script src="plotly.min.js"></script>
        	    </head><body>
        	      <div id="chart" style="width:800px;height:600px;"></div>
        	      <script>
        	        const x     = %s;
        	        const yReal = %s;
        	        const yPred = %s;
        	        Plotly.newPlot('chart',[
        	          { x:x, y:yReal, mode:'markers', name:'Real',  marker:{color:'blue'} },
        	          { x:x, y:yPred, mode:'markers', name:'Pred.', marker:{color:'red'}  }
        	        ],{ title:'Precio Casas con BN+DO (Scatter)',
        	            xaxis:{title:'Índice'},
        	            yaxis:{title:'Precio'} });
        	      </script>
        	    </body></html>
        	    """.formatted(xData, yReal, yPred);
        */
        
        String html = """
        		<html>
        		<head>
        		  <script src="plotly.min.js"></script>
        		  <style>
        		    #chart1, #chart2 { width:800px; height:400px; margin:0 auto; }
        		  </style>
        		</head>
        		<body>
        		  <h2 style="text-align:center">Precio Casas con BN+DO (Scatter)</h2>
        		  <div id="chart1"></div>
        		  <h2 style="text-align:center">True vs Predicted</h2>
        		  <div id="chart2"></div>
        		  <script>
        		    const x     = %s;
        		    const yReal = %s;
        		    const yPred = %s;

        		    /* Primer gráfico */
        		    Plotly.newPlot('chart1', [
        		      { x:x, y:yReal, mode:'markers', name:'Real',  marker:{color:'blue'} },
        		      { x:x, y:yPred, mode:'markers', name:'Pred.', marker:{color:'red'} }
        		    ], {
        		      title:'Precio Casas con BN+DO (Scatter)',
        		      xaxis:{title:'Índice'},
        		      yaxis:{title:'Precio'}
        		    });

        		    /* Segundo gráfico con línea y=x */
        		    const minVal = Math.min(...yReal.concat(yPred));
        		    const maxVal = Math.max(...yReal.concat(yPred));

        		    Plotly.newPlot('chart2', [
        		      { x:yReal, y:yPred, mode:'markers', name:'Pred vs True', marker:{color:'green'} }
        		    ], {
        		      title:'True vs Predicted',
        		      xaxis:{title:'True Price', range:[minVal, maxVal]},
        		      yaxis:{title:'Predicted Price', range:[minVal, maxVal]},
        		      shapes: [
        		        {
        		          type: 'line',
        		          x0: minVal, y0: minVal,
        		          x1: maxVal, y1: maxVal,
        		          line:{ dash:'dashdot', width:2, color:'black' }
        		        }
        		      ]
        		    });
        		  </script>
        		</body>
        		</html>
        		""".formatted(
        		    xData.toString(),
        		    yReal.toString(),
        		    yPred.toString()
        		);

        PlotlyBrowserViewer.showInBrowser2(html);
    }

    private static double computeMSE(MultiLayerPerceptronProcessor mlp, DataSet ds) {
        double sum = 0;
        int M = ds.getInputs().size();
        for (int i = 0; i < M; i++) {
            double y = ds.getTargets().get(i).getValues().get(0);
            double p = mlp.predict(ds.getInputs().get(i)).getValues().get(0);
            sum += Math.pow(y - p, 2);
        }
        return sum / M;
    }

    private static Layer initLayer(int neurons, int inputs, Random rnd,
                                   ActivationFunction act) {
        double[][] w = new double[neurons][inputs];
        double[]   b = new double[neurons];
        for (int i = 0; i < neurons; i++) {
            b[i] = (rnd.nextDouble()*2 - 1)*0.1;
            for (int j = 0; j < inputs; j++) {
                w[i][j] = (rnd.nextDouble()*2 - 1)*0.1;
            }
        }
        return new Layer(w, b, act);
    }
    
    private static double computeRSquared(MultiLayerPerceptronProcessor mlp, DataSet ds) {
        double ssTot = 0, ssRes = 0;
        int N = ds.getInputs().size();
        // media de y reales
        double meanY = ds.getTargets().stream()
                         .mapToDouble(s -> s.getValues().get(0))
                         .average()
                         .orElse(0);
        for (int i = 0; i < N; i++) {
            double y   = ds.getTargets().get(i).getValues().get(0);
            double ŷ = mlp.predict(ds.getInputs().get(i)).getValues().get(0);
            ssRes += (y - ŷ)*(y - ŷ);
            ssTot += (y - meanY)*(y - meanY);
        }
        return 1 - ssRes/ssTot;
    }

}

/*
 * 

Interpretación de las métricas

A. MSE (Error Cuadrático Medio)
Te da el error promedio al cuadrado en la escala normalizada. Aquí, MSE(normalizado)=0.1883 significa que el error típico es √0.1883 ≈ 0.434 desviaciones estándar de tu Y original.

	1.En la escala de precio real, eso es:
	√0.1883 × σ(Y) ≈ 0.434 × 50 000 € (si la desviación real fuese 50 000€) ≈ 21 700 €.
	2. Decide si este error medio (p.e. ±20 k€) es aceptable según el dominio.

B. R² (Coeficiente de determinación)
R²=0.8117 indica que el modelo explica el 81 % de la varianza de los precios.

	1. R² cerca de 1 ⇒ buen ajuste.
	2. R² cerca de 0 ⇒ no explica nada mejor que predecir la media.
	3. R² negativo ⇒ peor que predecir la media.

*
*/
