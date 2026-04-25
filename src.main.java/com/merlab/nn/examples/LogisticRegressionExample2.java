package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.LogisticRegressionProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.processor.SimpleLogisticRegressionProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.trainer.BackpropLogisticTrainer;
import com.merlab.signals.nn.trainer.BackpropMLPTrainer;
import com.merlab.signals.nn.trainer.LogisticTrainer;
import com.merlab.signals.nn.trainer.MLPTrainer;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.None;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Ejemplo de regresión logística sintética (2D, etiquetas 0/1).
 */
public class LogisticRegressionExample2 {
	/*

    public static void main(String[] args) {
        // 1) Generar datos sintéticos
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
            raw[i] = new double[]{ x, y, label };
        }
        DataSet ds = DataSetBuilder.fromArray(raw, 2);

        // 2) Crear el procesador de regresión logística
        //Layer lrLayer = initLayer(1, 2, new Random(1), ActivationFunctions.SIGMOID);
        //LogisticRegressionProcessor logreg = new LogisticRegressionProcessor(lrLayer);
        
        
        // 2) Construye el procesador logístico (pesos iniciales aleatorios, bias=0)
        //Random rnd = new Random(42);
        double[] w0 = { rnd.nextGaussian() * 0.1, rnd.nextGaussian() * 0.1 };
        LogisticRegressionProcessor model = new LogisticRegressionProcessor(w0, 0.0);

        // 3) Entrena con backprop (ajusta w,b)
        LogisticTrainer trainer = new BackpropLogisticTrainer();
        int epochs = 2000;
        double lr   = 0.1;
        LogisticRegressionProcessor trained = trainer.train(model, ds, epochs, lr);

        // 3) Entrenar con backprop
        //BackpropLogisticTrainer trainer = new BackpropLogisticTrainer();
        //int epochs = 2000;
        //double lr = 0.5;
        int batchSize = 16;
        
        List<Integer> idx = IntStream.range(0, N).boxed().collect(Collectors.toList());

        for (int e = 1; e <= epochs; e++) {
            Collections.shuffle(idx, rnd);
            for (int i = 0; i < N; i += batchSize) {
                int end = Math.min(N, i + batchSize);
                List<Signal> xb = idx.subList(i, end).stream()
                    .map(ds.getInputs()::get).collect(Collectors.toList());
                List<Signal> yb = idx.subList(i, end).stream()
                    .map(ds.getTargets()::get).collect(Collectors.toList());
                DataSet batch = new DataSet(xb, yb);
                //logreg = (LogisticRegressionProcessor) trainer.train(logreg, batch, 1, lr);
                logreg = (LogisticRegressionProcessor) trainer.train(
                	    (NeuralNetworkProcessor) logreg,
                	    batch,
                	    1,
                	    lr
                	);

            }
            if (e % 500 == 0) {
                double acc = computeAccuracy(logreg, ds);
                System.out.printf("Epoch %4d/%d  acc=%.2f%%%n", e, epochs, acc*100);
            }
        }

        // 4) Preparar datos para graficar
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();
        List<Double> xp0 = new ArrayList<>(), yp0 = new ArrayList<>();
        List<Double> xp1 = new ArrayList<>(), yp1 = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            Signal in = ds.getInputs().get(i);
            int trueL = (int) ds.getTargets().get(i).getValues().get(0).doubleValue();
            double p = logreg.predict(in).getValues().get(0);
            int predL = p > 0.5 ? 1 : 0;

            if (trueL==0) { x0.add(in.getValues().get(0)); y0.add(in.getValues().get(1)); }
                       else { x1.add(in.getValues().get(0)); y1.add(in.getValues().get(1)); }
            if (predL==0){ xp0.add(in.getValues().get(0)); yp0.add(in.getValues().get(1)); }
                       else{  xp1.add(in.getValues().get(0)); yp1.add(in.getValues().get(1)); }
        }

        // 5) Dibujar con XChart
        XYChart chart = new XYChartBuilder()
            .width(700).height(600)
            .title("Logistic Regression (synthetic)")
            .xAxisTitle("x").yAxisTitle("y")
            .theme(ChartTheme.Matlab)
            .build();

        // Clase real
        chart.addSeries("Real 0", x0, y0)
             .setMarker(SeriesMarkers.CIRCLE);
        chart.addSeries("Real 1", x1, y1)
             .setMarker(SeriesMarkers.CROSS);

        // Predicción
        chart.addSeries("Pred 0", xp0, yp0)
             .setMarker(SeriesMarkers.DIAMOND);
        chart.addSeries("Pred 1", xp1, yp1)
             .setMarker(SeriesMarkers.SQUARE);

        // Frontera (grid y sigmoid=0.5)
        List<Double> gx = new ArrayList<>(), gy = new ArrayList<>();
        for (double xx = -outerRadius; xx <= outerRadius; xx += 0.05) {
            Signal s = new Signal();
            s.add(xx); s.add(0.0);
            double p = logreg.predict(s).getValues().get(0);
            if (Math.abs(p-0.5) < 0.02) { // aproximación
                gx.add(xx); gy.add(0.0);
            }
        }
        if (!gx.isEmpty()) {
        	XYSeries border = chart.addSeries("Frontera", gx, gy);
        	border.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        	border.setMarker(new None()); // o no marcar
        }

        new SwingWrapper<>(chart).displayChart();
    }

    private static double computeAccuracy(LogisticRegressionProcessor model, DataSet ds) {
        long correct = 0;
        for (int i = 0; i < ds.getInputs().size(); i++) {
            double p = model.predict(ds.getInputs().get(i)).getValues().get(0);
            int pred = p>0.5 ? 1 : 0;
            int trueL = (int) ds.getTargets().get(i).getValues().get(0).doubleValue();
            if (pred == trueL) correct++;
        }
        return correct / (double) ds.getInputs().size();
    }

    private static Layer initLayer(int neurons, int inputs, Random rnd,
                                   ActivationFunctions act) {
        double[][] w = new double[neurons][inputs];
        double[] b   = new double[neurons];
        for (int i = 0; i < neurons; i++) {
            b[i] = 0.0;
            for (int j = 0; j < inputs; j++) {
                w[i][j] = (rnd.nextDouble()*2 - 1)*0.1;
            }
        }
        return new Layer(w, b, act);
    }
    
    */
}
