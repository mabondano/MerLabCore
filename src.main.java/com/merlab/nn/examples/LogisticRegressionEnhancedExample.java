package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataLoader;
import com.merlab.signals.data.DataLoaderConfig;
import com.merlab.signals.data.DataLoaderFactory;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.trainer.BackpropLogisticTrainer;
import com.merlab.signals.nn.trainer.LogisticTrainer;
import com.merlab.signals.plot.PlotlyBrowserViewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Ejemplo “mejorado” de Regresión Logística para círculos concéntricos:
 *   - Creamos características x, y y z = x^2 + y^2
 *   - Entrenamos un único nodo con sigmoide usando BackpropLogisticTrainer
 *   - Graficamos con Plotly la separación real y la frontera p=0.5
 */
public class LogisticRegressionEnhancedExample {

    public static void main(String[] args) throws Exception {
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

        // Construimos un DataSet “temporal” solo con x,y para luego rehacerlo con la nueva característica z.
        DataSet ds2D = DataSetBuilder.fromArray(raw, 2);

        // 2) Transformar cada (x,y) en (x, y, z=x^2+y^2)
        List<Signal> inputs3 = new ArrayList<>(N);
        List<Signal> targets  = ds2D.getTargets();
        for (int i = 0; i < N; i++) {
            double x = raw[i][0];
            double y = raw[i][1];
            double z = x*x + y*y;  // nueva variable
            Signal s = new Signal();
            s.add(x);
            s.add(y);
            s.add(z);
            inputs3.add(s);
        }
        DataSet ds = new DataSet(inputs3, targets);

        // 3) Crear el procesador de regresión logística: 3 entradas + sigmoide
        double[] w0 = { rnd.nextGaussian()*0.1, rnd.nextGaussian()*0.1, rnd.nextGaussian()*0.1 };
        LogisticRegressionProcessor logreg = new LogisticRegressionProcessor(w0, 0.0);

        // 4) Entrenar con BackpropLogisticTrainer (mini-batch)
        LogisticTrainer trainer = new BackpropLogisticTrainer();
        int epochs   = 2000;
        double lr    = 0.5;
        int batchSize = 16;

        List<Integer> idx = IntStream.range(0, N)
                                     .boxed()
                                     .collect(Collectors.toList());
        for (int e = 1; e <= epochs; e++) {
            Collections.shuffle(idx, rnd);
            for (int i = 0; i < N; i += batchSize) {
                int end = Math.min(N, i + batchSize);
                List<Signal> xb = idx.subList(i, end).stream()
                                     .map(ds.getInputs()::get)
                                     .collect(Collectors.toList());
                List<Signal> yb = idx.subList(i, end).stream()
                                     .map(ds.getTargets()::get)
                                     .collect(Collectors.toList());
                DataSet batch = new DataSet(xb, yb);
                logreg = (LogisticRegressionProcessor) trainer.train(logreg, batch, 1, lr);
            }
            if (e % 500 == 0) {
                double acc = computeAccuracy(logreg, ds);
                System.out.printf("Epoch %4d/%d  acc=%.2f%%%n", e, epochs, acc*100);
            }
        }

        // 5) Clasificación final y separación de puntos para graficar
        List<Double> xs0 = new ArrayList<>(), ys0 = new ArrayList<>();
        List<Double> xs1 = new ArrayList<>(), ys1 = new ArrayList<>();
        List<Double> fx0 = new ArrayList<>(), fy0 = new ArrayList<>();  // puntos frontera clase 0
        List<Double> fx1 = new ArrayList<>(), fy1 = new ArrayList<>();  // puntos frontera clase 1

        for (int i = 0; i < N; i++) {
            double x = raw[i][0];
            double y = raw[i][1];
            double z = x*x + y*y;
            Signal s = new Signal();
            s.add(x); s.add(y); s.add(z);

            double prob = logreg.predict(s).getValues().get(0);
            int predLabel = (prob > 0.5) ? 1 : 0;
            int trueLabel = (raw[i][2] == 0) ? 0 : 1;

            // Separar en dos listas para graficar
            if (trueLabel == 0) {
                xs0.add(x);
                ys0.add(y);
            } else {
                xs1.add(x);
                ys1.add(y);
            }

            // Si la probabilidad está cerca de 0.5, anotamos en “frontera”
            if (Math.abs(prob - 0.5) < 0.02) {
                if (predLabel == 0) {
                    fx0.add(x);
                    fy0.add(y);
                } else {
                    fx1.add(x);
                    fy1.add(y);
                }
            }
        }

        // 6) Graficar con Plotly: Clase 0 (azul), Clase 1 (naranja), Frontera (violeta)
        String html = """
            <html>
            <head>
              <script src="plotly.min.js"></script>
            </head>
            <body>
              <div id="chart" style="width:800px;height:600px;"></div>
              <script>
                const x0   = %s;
                const y0   = %s;
                const x1   = %s;
                const y1   = %s;
                const fx0  = %s;
                const fy0  = %s;
                const fx1  = %s;
                const fy1  = %s;

                const trace0 = {
                  x: x0, y: y0,
                  mode:'markers',
                  name:'Clase 0',
                  marker:{color:'blue', size:6, opacity:0.7}
                };
                const trace1 = {
                  x: x1, y: y1,
                  mode:'markers',
                  name:'Clase 1',
                  marker:{color:'orange', size:6, opacity:0.7}
                };
                const traceF0 = {
                  x: fx0, y: fy0,
                  mode:'markers',
                  name:'Frontera p≈0.5 (→ 0)',
                  marker:{color:'purple', size:4, opacity:0.5}
                };
                const traceF1 = {
                  x: fx1, y: fy1,
                  mode:'markers',
                  name:'Frontera p≈0.5 (→ 1)',
                  marker:{color:'magenta', size:4, opacity:0.5}
                };

                Plotly.newPlot('chart',
                  [trace0, trace1, traceF0, traceF1],
                  {
                    title:'Regresión Logística Enriquecida (círculos)',
                    xaxis:{ title:'x' },
                    yaxis:{ title:'y' }
                  }
                );
              </script>
            </body>
            </html>
            """.formatted(
                xs0.toString(), ys0.toString(),
                xs1.toString(), ys1.toString(),
                fx0.toString(), fy0.toString(),
                fx1.toString(), fy1.toString()
            );
        
     // 6) Graficar con Plotly: Clase 0 (azul), Clase 1 (naranja), Frontera (negra grande)
        String html2 = """
            <html>
            <head>
              <script src="plotly.min.js"></script>
            </head>
            <body>
              <div id="chart" style="width:800px;height:600px;"></div>
              <script>
                const x0   = %s;
                const y0   = %s;
                const x1   = %s;
                const y1   = %s;
                // Unificamos la frontera en un único array (fx = fx0.concat(fx1), fy = fy0.concat(fy1))
                const fx  = %s.concat(%s);
                const fy  = %s.concat(%s);

                const trace0 = {
                  x: x0, y: y0,
                  mode:'markers',
                  name:'Clase 0',
                  marker:{color:'blue', size:6, opacity:0.7}
                };
                const trace1 = {
                  x: x1, y: y1,
                  mode:'markers',
                  name:'Clase 1',
                  marker:{color:'orange', size:6, opacity:0.7}
                };
                const traceF = {
                  x: fx, y: fy,
                  mode:'markers',
                  name:'Frontera p≈0.5',
                  marker:{color:'black', size:8, opacity:0.8}
                };

                Plotly.newPlot('chart',
                  [trace0, trace1, traceF],
                  {
                    title:'Regresión Logística Enriquecida (círculos)',
                    xaxis:{ title:'x' },
                    yaxis:{ title:'y' }
                  }
                );
              </script>
            </body>
            </html>
            """.formatted(
                xs0.toString(), ys0.toString(),
                xs1.toString(), ys1.toString(),
                fx0.toString(), fx1.toString(),
                fy0.toString(), fy1.toString()
            );

        // 7) Mostrar en navegador
        PlotlyBrowserViewer.showInBrowser(html2);
    }

    private static double computeAccuracy(LogisticRegressionProcessor model, DataSet ds) {
        long correct = 0;
        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal in = ds.getInputs().get(i);
            double prob = model.predict(in).getValues().get(0);
            int pred  = (prob > 0.5) ? 1 : 0;
            int actual = ds.getTargets().get(i).getValues().get(0).intValue();
            if (pred == actual) correct++;
        }
        return correct / (double) ds.getInputs().size();
    }

    private static Layer initLayer(int neurons, int inputs, Random rnd, ActivationFunctions act) {
        double[][] w = new double[neurons][inputs];
        double[]   b = new double[neurons];
        for (int i = 0; i < neurons; i++) {
            b[i] = 0.0;
            for (int j = 0; j < inputs; j++) {
                w[i][j] = (rnd.nextDouble() * 2 - 1) * 0.1;
            }
        }
        return new Layer(w, b, act);
    }
}


/*
public class LogisticRegressionEnhacedExample {

    public static void main(String[] args) throws IOException {

    	// 1) Dataset sintético de dos círculos
    	int N = 200;
    	double innerRadius = 1.0, outerRadius = 2.0;
    	double[][] raw = new double[N][3]; // x,y,label
    	Random rnd = new Random(42);

    	for (int i = 0; i < N; i++) {
    	    double angle = rnd.nextDouble() * 2 * Math.PI;
    	    double r = (i < N/2)
    	             ? innerRadius * Math.sqrt(rnd.nextDouble())
    	             : outerRadius + rnd.nextDouble() * 0.5;
    	    double x = r * Math.cos(angle);
    	    double y = r * Math.sin(angle);
    	    int label = (r <= innerRadius) ? 0 : 1;
    	    raw[i] = new double[] { x, y, label };
    	}
    	// NOTA: raw tiene [x, y, label], todavía sin la nueva variable r
    	DataSet ds = DataSetBuilder.fromArray(raw, 2);

    	// 2) Construir “features extendidos” para regresión logística:
    	//    	    Vamos a reusar el mismo DataSet, pero construyendo 
    	//    	    un nuevo DataSet donde cada señal de entrada es [x, y, x^2+y^2].

    	List<Signal> inputs3 = new ArrayList<>(N);
    	List<Signal> targets = ds.getTargets();
    	for (int i = 0; i < N; i++) {
    	    Signal original = ds.getInputs().get(i); // contiene [x, y]
    	    double x = original.getValues().get(0);
    	    double y = original.getValues().get(1);
    	    double z = x*x + y*y;                   // NUEVA característica
    	    Signal s = new Signal();
    	    s.add(x);
    	    s.add(y);
    	    s.add(z);
    	    inputs3.add(s);
    	}
    	DataSet ds3 = new DataSet(inputs3, targets); // Ahora hay 3 variables de entrada

    	// 3) Crear el modelo de regresión logística con 3 entradas:
    	double[] w0 = { rnd.nextGaussian()*0.1, rnd.nextGaussian()*0.1, rnd.nextGaussian()*0.1 };
    	LogisticRegressionProcessor logreg = new LogisticRegressionProcessor(w0, 0.0);

    	// 4) Entrenar exactamente igual que antes (backprop):
    	LogisticTrainer trainer = new BackpropLogisticTrainer();
    	int epochs = 2000;
    	double lr = 0.5;
    	List<Integer> idx = IntStream.range(0, N).boxed().collect(Collectors.toList());
    	for (int e = 1; e <= epochs; e++) {
    	    Collections.shuffle(idx, rnd);
    	    for (int i = 0; i < N; i += 16) {
    	        int end = Math.min(N, i + 16);
    	        List<Signal> xb = idx.subList(i, end).stream()
    	                             .map(ds3.getInputs()::get)
    	                             .collect(Collectors.toList());
    	        List<Signal> yb = idx.subList(i, end).stream()
    	                             .map(ds3.getTargets()::get)
    	                             .collect(Collectors.toList());
    	        DataSet batch = new DataSet(xb, yb);
    	        logreg = (LogisticRegressionProcessor) trainer.train(logreg, batch, 1, lr);
    	    }
    	    if (e % 500 == 0) {
    	        double acc = computeAccuracy(logreg, ds3);
    	        System.out.printf("Epoch %4d/%d  acc=%.2f%%%n", e, epochs, acc*100);
    	    }
    	}

    	// 5) Al predecir, hay que pasar siempre 3 variables: [x, y, x^2+y^2]:
    	List<Double> xs0 = new ArrayList<>(), ys0 = new ArrayList<>();
    	List<Double> xs1 = new ArrayList<>(), ys1 = new ArrayList<>();
    	List<Double> fx = new ArrayList<>(), fy = new ArrayList<>(); // para frontera
    	for (int i = 0; i < N; i++) {
    	    double x = raw[i][0];
    	    double y = raw[i][1];
    	    double z = x*x + y*y;
    	    Signal s = new Signal(); 
    	    s.add(x); s.add(y); s.add(z);
    	    double p = logreg.predict(s).getValues().get(0);
    	    int pred = (p > 0.5) ? 1 : 0;
    	    int realLabel = (raw[i][2] == 0) ? 0 : 1;
    	    if (realLabel == 0) { xs0.add(x); ys0.add(y); }
    	    else               { xs1.add(x); ys1.add(y); }
    	    if (pred == 1)     { fx.add(x);  fy.add(y); } // puntos donde p≈0.5 (aprox)
    	}

    	// 6) Graficar con Plotly:        
    	//    	    “xs0, ys0” y “xs1, ys1” para los puntos reales,
    	//    	    “fx, fy” para la “frontera estimada” (p≈0.5).


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
        fx = new ArrayList<>(); fy = new ArrayList<>();
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
     *
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

    /** Calcula la accuracy (%) sobre todo el DataSet *
    private static double computeAccuracy(LogisticRegressionProcessor model, DataSet ds) {
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

}

*/





    


