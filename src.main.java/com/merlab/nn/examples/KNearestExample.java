package com.merlab.nn.examples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.ml.DistanceMetric;
import com.merlab.signals.ml.KNearestProcessor2;
import com.merlab.signals.nn.distance.DistanceMetrics.Metric;
import com.merlab.signals.nn.trainer.Trainer2;
import com.merlab.signals.nn.trainer.TrainerFactory2;
import com.merlab.signals.nn.trainer.TrainerFactory2.Algorithm;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 * Ejemplo de clasificación binaria con KNN (2D sintético linealmente separable),
 * usando el Trainer2 y TrainerFactory2 para instanciar un KNearestTrainer2 trivial.
 */
public class KNearestExample {

    public static void main(String[] args) throws Exception {
        // 1) Crear datos sintéticos (dos clases, linealmente separables)
        int N = 200;
        double[][] raw = new double[N][3];
        Random rnd = new Random(123);

        for (int i = 0; i < N; i++) {
            double x = rnd.nextDouble() * 4 - 2;  // en [-2,2]
            double y = rnd.nextDouble() * 4 - 2;  // en [-2,2]
            // Etiqueta según la recta y = 0.5*x - 0.2
            int label = (y > 0.5 * x - 0.2) ? 1 : 0;
            raw[i] = new double[]{ x, y, label };
        }

        // 2) Construir DataSet a partir de raw
        // DataSetBuilder.fromArray(raw, 2) asume [x, y, label], donde 2 indica
        // que las primeras 2 columnas son features y la última es la etiqueta.
        DataSet ds = DataSetBuilder.fromArray(raw, 2);

        // 3) Obtener un "trainer" trivial para KNN desde TrainerFactory2.
        //    El KNearestTrainer2 no necesita entrenamiento real (solo guarda datos),
        //    así que basta con instanciarlo vía el factory.
        Trainer2<KNearestProcessor2> knnTrainer =
            TrainerFactory2.create(Algorithm.KNN);

        // 4) Instanciar el Processor KNN con k = 5 y métrica Euclidiana
        int k = 5;
        KNearestProcessor2 knnModel = new KNearestProcessor2(ds, k, Metric.EUCLIDEAN); //, DistanceMetric.EUCLIDEAN

        // 5) “Entrenar” el KNN (en realidad solo almacena el DataSet internamente).
        //    Por convención, el método train de KNearestTrainer2 devolverá el mismo modelo.
        KNearestProcessor2 knnTrained = knnTrainer.train(knnModel, ds, 1, 0.0);

        // 6) Calcular accuracy final sobre todo el conjunto y recopilar puntos por clase
        int correct = 0;
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();

        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal in = ds.getInputs().get(i);
            int trueLabel = ds.getTargets().get(i).getValues().get(0).intValue();

            // Predict: knnTrained.predict devuelve un Signal con el label predicho como 0/1
            int predLabel = knnTrained.predict(in).getValues().get(0).intValue();
            if (predLabel == trueLabel) correct++;

            // Separar puntos para graficar
            double xi = in.getValues().get(0);
            double yi = in.getValues().get(1);
            if (trueLabel == 0) {
                x0.add(xi);
                y0.add(yi);
            } else {
                x1.add(xi);
                y1.add(yi);
            }
        }

        double accuracy = 100.0 * correct / ds.getInputs().size();
        System.out.printf("Accuracy KNN (k=%d): %.2f%%%n", k, accuracy);

        // 7) Graficar con XChart: Clase 0 en azul, Clase 1 en naranja
        XYChart chart = new XYChartBuilder()
            .width(700).height(600)
            .title("KNN Ejemplo (k=" + k + ")")
            .xAxisTitle("x").yAxisTitle("y")
            .theme(ChartTheme.XChart)
            .build();

        chart.addSeries("Clase 0", x0, y0)
             .setMarker(SeriesMarkers.CIRCLE);
        chart.addSeries("Clase 1", x1, y1)
             .setMarker(SeriesMarkers.CROSS);

        new SwingWrapper<>(chart).displayChart();
    }
}
