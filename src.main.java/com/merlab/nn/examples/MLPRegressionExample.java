package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.data.DataSetIO;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler.ChartTheme;
import com.merlab.signals.plot.ChartType;
import com.merlab.signals.plot.SignalPlotter;

/**
 * MLPRegressionExample:
 * - Genera un dataset de 100 puntos (x, sin(x))
 * - Define un MLP de 3 capas: 10 neuronas ReLU + 1 neurona identidad
 * - Grafica la línea real vs predicciones
 */
public class MLPRegressionExample {

    public static void main(String[] args) throws Exception {
        // 1) Generar dataset sintético: x en [0, 2π]
        int n = 100;
        double[][] raw = new double[n][2];
        for (int i = 0; i < n; i++) {
            double x = 2 * Math.PI * i / (n - 1);
            raw[i][0] = x;
            raw[i][1] = Math.sin(x);
        }
        DataSet ds = DataSetBuilder.fromArray(
            java.util.stream.IntStream.range(0, n)
                .mapToObj(i -> new double[]{ raw[i][0], raw[i][1], raw[i][1] })
                .toArray(double[][]::new)
        );

        // 2) Definir MLP
        List<Layer> layers = List.of(
            // Capa oculta: 10 neuronas con ReLU (pesos aleatorios)
            new Layer(
                new double[10][1],  // aquí colocarás pesos entrenados
                new double[10],     // biases iniciales
                ActivationFunctions.RELU
            ),
            // Capa de salida: 1 neurona con identidad
            new Layer(
                new double[][] { { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 } },
                new double[] { 0.0 },
                ActivationFunctions.IDENTITY
            )
        );
        NeuralNetworkProcessor mlp = new MultiLayerPerceptronProcessor(layers);

        // 3) Obtener predicciones
        Signal real = new Signal();
        Signal pred = new Signal();
        for (Signal in : ds.getInputs()) {
            double x = in.getValues().get(0);
            real.add(Math.sin(x));
            pred.add(mlp.predict(in).getValues().get(0));
        }

        // 4) Guardar y graficar
        DataSetIO.saveToCsv(ds, Path.of("out/sin_real.csv"));
        
        // Graficar real vs predicción
        System.out.println("Real (primeros 5): " + real.getValues().subList(0,5));
        System.out.println("Pred (primeros 5): " + pred.getValues().subList(0,5));
        
        // Si prefieres un gráfico de línea:
        SignalPlotter.plotSignal2(			//com.merlab.signals.plot.SignalPlotter.plotSignal2
            "Regresión sin(x): Real vs Predicción",
            real, ChartTheme.XChart, com.merlab.signals.plot.ChartType.LINE
        );
        SignalPlotter.plotSignal2(			//com.merlab.signals.plot.SignalPlotter.plotSignal2
            "Regresión sin(x): Predicción",
            pred, ChartTheme.GGPlot2, com.merlab.signals.plot.ChartType.LINE
        );
    }
}
