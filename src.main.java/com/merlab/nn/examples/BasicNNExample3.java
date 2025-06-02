// src/main/java/com/merlab/signals/examples/BasicNNExample3.java
package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.features.FeatureExtractor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.ConfigPerceptronProcessor;
import com.merlab.signals.nn.manager.NeuralNetworkManager;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.trainer.LinearRegressionTrainer3;
import com.merlab.signals.nn.trainer.ModelTrainer;
import com.merlab.signals.core.SignalStack;
import com.merlab.signals.persistence.DatabaseManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Ejemplo 3: usa LinearRegressionTrainer3 agnóstico,
 * luego modifica la activación con setActivation().
 */
public class BasicNNExample3 {

    public static void main(String[] args) throws Exception {
        // 1. Datos sintéticos: y = 2*x + 1
        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();
        for (double x = 1; x <= 5; x++) {
            Signal s = new Signal();
            s.add(x);
            inputs.add(s);
            Signal t = new Signal();
            t.add(2 * x + 1);
            targets.add(t);
        }

        // 2. Extraer features (media)
        List<Signal> feats = new ArrayList<>();
        for (Signal raw : inputs) {
            feats.add(FeatureExtractor.extractFeaturesMean(raw));
        }

        // 3. Entrenar con trainer3 (devuelve perceptrón Identity)
        ModelTrainer trainer = new LinearRegressionTrainer3();
        Path modelPath = Paths.get("model3.txt");
        NeuralNetworkProcessor processor = trainer.train(feats, targets, modelPath);
        System.out.println("Processor inicial: " + processor.getClass().getSimpleName());

        // 4. Cambiar función de activación a SIGMOID
        if (processor instanceof ConfigPerceptronProcessor) {
            ConfigPerceptronProcessor cpp = (ConfigPerceptronProcessor) processor;
            cpp.setActivation(ActivationFunctions.SIGMOID);
            System.out.println("Activación cambiada a SIGMOID");
        }

        // 5. Preparar NN Manager y stack
        SignalStack stack = new SignalStack();
        DatabaseManager db = new DatabaseManager("jdbc:mariadb://localhost:3306/test", "root", "root"); // tu implementación
        NeuralNetworkManager nnManager = new NeuralNetworkManager(stack, processor, db);

        // 6. Inferencia con x = 10
        Signal newRaw = new Signal();
        newRaw.add(10.0);
        Signal newFeat = FeatureExtractor.extractFeaturesMean(newRaw);
        stack.push(newFeat);

        Signal prediction = nnManager.runInference();
        System.out.println("Predicción para x=10: " + prediction.getValues());

        // 7. Guardar resultado
        nnManager.saveResult();
    }
}
