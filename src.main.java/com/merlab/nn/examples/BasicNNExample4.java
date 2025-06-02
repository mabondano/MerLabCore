// src/main/java/com/merlab/signals/examples/BasicNNExample4.java
package com.merlab.nn.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.features.FeatureExtractor;
import com.merlab.signals.nn.model.RegressionModel;
import com.merlab.signals.nn.trainer.LinearRegressionTrainer4;
import com.merlab.signals.nn.trainer.RegressionTrainer;
import com.merlab.signals.nn.processor.LinearProcessor;
import com.merlab.signals.nn.processor.ConfigPerceptronProcessor;
import com.merlab.signals.nn.processor.ActivationFunctions;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.manager.NeuralNetworkManager;
import com.merlab.signals.core.SignalStack;
import com.merlab.signals.persistence.DatabaseManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BasicNNExample4 {
    public static void main(String[] args) throws Exception {
        // 1. Datos sintéticos
        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();
        for (double x = 1; x <= 5; x++) {
            Signal s = new Signal(); s.add(x);
            inputs.add(s);
            Signal t = new Signal(); t.add(2*x + 1);
            targets.add(t);
        }

        // 2. Features (media)
        List<Signal> feats = new ArrayList<>();
        for (Signal raw : inputs) {
            feats.add(FeatureExtractor.extractFeaturesMean(raw));
        }

        // 3. Entrenamiento: obtenemos solo el modelo
        RegressionTrainer trainer = new LinearRegressionTrainer4();
        Path modelPath = Paths.get("model4.txt");
        RegressionModel model = trainer.train(feats, targets, modelPath);

        // 4.a) Inferencia lineal (raw)
        NeuralNetworkProcessor linProc =
            new LinearProcessor(model.getWeights(), model.getBias());

        // 4.b) Inferencia perceptrón (sigmoide)
        NeuralNetworkProcessor percProc =
            new ConfigPerceptronProcessor(
                model.getWeights(), model.getBias(),
                ActivationFunctions.SIGMOID
            );
        
        //
        NeuralNetworkProcessor procLeaky =
        	    new ConfigPerceptronProcessor(
        	        model.getWeights(),
        	        model.getBias(),
        	        ActivationFunctions.LEAKY_RELU
        	    );

    	NeuralNetworkProcessor procSoftplus =
    	    new ConfigPerceptronProcessor(
    	        model.getWeights(),
    	        model.getBias(),
    	        ActivationFunctions.SOFTPLUS
    	    );

        System.out.println("LinearProcessor output: "
            + infer( linProc, 10.0));
        System.out.println("PerceptronProcessor output: "
            + infer( percProc, 10.0));
        
        //
        System.out.println("LeakyReLU:    " + infer(procLeaky, 10.0));
        System.out.println("Softplus:     " + infer(procSoftplus, 10.0));

        // 5. Si quieres usar el manager:
        SignalStack stack = new SignalStack();
        DatabaseManager db = new DatabaseManager("jdbc:mariadb://localhost:3306/test", "root", "root");
        NeuralNetworkManager nnm = new NeuralNetworkManager(stack, percProc, db);

        Signal feat10 = FeatureExtractor.extractFeaturesMean(new Signal(){{
            add(10.0);
        }});
        stack.push(feat10);
        System.out.println("Manager output: "
            + nnm.runInference().getValues());
    }

    private static List<Double> infer(NeuralNetworkProcessor proc, double x) {
        Signal sig = new Signal();
        sig.add(x);
        Signal f = FeatureExtractor.extractFeaturesMean(sig);
        return proc.predict(f).getValues();
    }
}
