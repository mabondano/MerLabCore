package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.features.FeatureExtractor;
import com.merlab.signals.nn.manager.NeuralNetworkManager;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.trainer.ModelTrainer;
import com.merlab.signals.nn.trainer.simple.SimpleLinearRegressionTrainer;
import com.merlab.signals.nn.trainer.simple.SimpleModelTrainer;
import com.merlab.signals.core.SignalStack;
import com.merlab.signals.persistence.DatabaseManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BasicNNExample {

    public static void main(String[] args) throws Exception {
        // 1. Datos sintéticos: señales y targets
        List<Signal> inputs = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();

        // Ejemplo: relacion lineal y = 2*x + 1
        for (double x = 1; x <= 5; x++) {
            Signal raw = new Signal();
            raw.add(x);
            inputs.add(raw);
            // target es 2*x + 1
            Signal t = new Signal();
            t.add(2 * x + 1);
            targets.add(t);
        }

        // 2. Extraer features (media y varianza)
        List<Signal> featureVectors = new ArrayList<>();
        for (Signal raw : inputs) {
            featureVectors.add(FeatureExtractor.extractFeaturesMean(raw));
        }

        // 3. Entrenar modelo (regresión lineal → LinearProcessor)
        SimpleModelTrainer trainer = new SimpleLinearRegressionTrainer();
        Path modelPath = Paths.get("model.txt");
        NeuralNetworkProcessor processor = trainer.train(featureVectors, targets, modelPath);

        // 4. Preparar el manager con placeholder de BD y stack
        SignalStack stack = new SignalStack();
        DatabaseManager dbManager = new DatabaseManager("jdbc:mariadb://localhost:3306/test", "root", "root");   // ajusta según tu implementación
        NeuralNetworkManager nnManager = new NeuralNetworkManager(stack, processor, dbManager);

        // 5. Inferencia sobre un nuevo valor x = 10
        Signal newRaw = new Signal();
        newRaw.add(10.0);
        Signal newFeatures = FeatureExtractor.extractFeaturesMean(newRaw);
        stack.push(newFeatures);                 // apilamos el vector de features

        Signal prediction = nnManager.runInference();
        System.out.println("Predicción para x=10: " + prediction.getValues());

        // 6. Guardar resultado
        nnManager.saveResult();
    }
}
