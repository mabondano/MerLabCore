package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;
import com.merlab.signals.nn.processor.Layer;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.trainer.BackpropMLPTrainer;
import com.merlab.signals.nn.trainer.MLPTrainer;
import com.merlab.signals.plot.SignalPlotter;
import com.merlab.signals.plot.ChartType;
import org.knowm.xchart.style.Styler.ChartTheme;

import java.util.List;
import java.util.stream.IntStream;

/**
 * MLPRegressionExample6:
 * - Construye un MLP de 3 capas.
 * - Lo entrena con BackpropMLPTrainer.
 * - Grafica Real vs Predicción.
 */
public class MLPRegressionExample6 {

    public static void main(String[] args) throws Exception {
        // 1) Dataset sintético (x, sin(x))
        int n = 100;
        double[][] raw = new double[n][2];
        for (int i = 0; i < n; i++) {
            double x = 2 * Math.PI * i / (n - 1);
            raw[i][0] = x;
            raw[i][1] = Math.sin(x);
        }
        DataSet ds = DataSetBuilder.fromArray(raw, 1);

        // 2) Definir MLP inicial
        double[][] wH = new double[10][1];
        double[]   bH = new double[10];
        double[][] wO = new double[1][10];
        double[]   bO = new double[1];
        
        // inicializar a valores pequeños aleatorios
        java.util.Random rnd = new java.util.Random(42);
        for(int i=0;i<10;i++){
            wH[i][0] = (rnd.nextDouble()*2-1)*0.1;
            bH[i]     = (rnd.nextDouble()*2-1)*0.1;
            wO[0][i]  = (rnd.nextDouble()*2-1)*0.1;
        }
        bO[0] = (rnd.nextDouble()*2-1)*0.1;

        Layer hidden = new Layer(wH, bH, ActivationFunctions.RELU);
        Layer output = new Layer(wO, bO, ActivationFunctions.IDENTITY);
        MultiLayerPerceptronProcessor initial =
            new MultiLayerPerceptronProcessor(List.of(hidden, output));

        // 3) Entrenar con backpropagation
        BackpropMLPTrainer trainer = new BackpropMLPTrainer();
        MultiLayerPerceptronProcessor trained =
            trainer.train(initial, ds, /*epochs*/500, /*lr*/0.01);
        
        

        // 4) Predecir y graficar
        Signal real = new Signal();
        Signal pred = new Signal();
        for (Signal in : ds.getInputs()) {
            real.add(ds.getTargets().get(ds.getInputs().indexOf(in)).getValues().get(0));
            pred.add(trained.predict(in).getValues().get(0));
        }
        SignalPlotter.plotSignal2("sin(x) Real",  real, ChartTheme.XChart, ChartType.LINE);
        SignalPlotter.plotSignal2("sin(x) Pred", pred, ChartTheme.XChart, ChartType.LINE);
    }
}


/*
Aumentar número de épocas
Pasa de 500 a 5 000 o 10 000 épocas. El entrenamiento batch (gradiente descendente completo) suele necesitar más iteraciones para redes tan pequeñas.

Reducir la tasa de aprendizaje
Prueba valores más pequeños (0.005, 0.001). Una tasa alta puede saltarse el mínimo, y una demasiado baja será muy lento.

Normalizar los datos
Lleva tu 𝑥 x a rango [−1,1] o media 0 / desviación 1. A MLPs les viene bien inputs centrados:
double xNorm = (x - Math.PI) / Math.PI;  // pasa [0,2π] a [-1,1]
Luego ajustas tu dataset con esos xNorm.

Cambiar tamaño de la capa oculta
Pasa de 10 neuronas a 20 o 30. A veces con pocas neuronas no hay capacidad suficiente para modelar la curvatura.

Mini-batch en lugar de batch
Entrenar en grupos de, por ejemplo, 10 muestras a la vez mejora la estabilidad y velocidad:

Segmenta tu DataSet en trozos.

Para cada trozo, calcula gradientes y actualiza inmediatamente.

Monitorear la función de pérdida
Imprime, cada 100 épocas, el MSE (error cuadrático medio):
Así sabrás si mejora y cuándo parar.



*/