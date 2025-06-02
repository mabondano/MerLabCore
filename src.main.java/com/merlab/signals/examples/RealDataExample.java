package com.merlab.signals.examples;

import com.merlab.signals.data.SimpleCSVDataLoader;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.features.FeatureExtractor;
import com.merlab.signals.nn.manager.NeuralNetworkManager;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.trainer.LinearRegressionTrainer4;
import com.merlab.signals.nn.trainer.RegressionTrainer;
import com.merlab.signals.core.SignalStack;
import com.merlab.signals.persistence.DatabaseManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class RealDataExample {

    public static void main(String[] args) throws Exception {
        // 1. Cargar CSV real (dos columnas: x, y)
    	Path csvPath = Paths.get("src/main/resources/data/house_prices_csv_Sample.csv");    	
    	//DataSet data = CSVDataLoader.load(csvPath, 3, 1);

        DataSet data = SimpleCSVDataLoader.load(
        		csvPath,
            /*nColsInput=*/3,    // p.ej. sqft, habitaciones, edad
            /*nColsTarget=*/1    // precio
        );

        // 2. Extraer features (en este caso ya son vectores completos)
        //    ó aplicar transformaciones adicionales si quieres
        List<com.merlab.signals.core.Signal> inputs  = data.getInputs();
        List<com.merlab.signals.core.Signal> targets = data.getTargets();

        // 3. Entrenar
        RegressionTrainer trainer = new LinearRegressionTrainer4();
        var model = trainer.train(inputs, targets, Paths.get("model_real.txt"));

        // 4. Inferencia
        NeuralNetworkProcessor proc = 
            new com.merlab.signals.nn.processor.LinearProcessor(
                model.getWeights(), model.getBias()
            );
        SignalStack stack = new SignalStack();
        DatabaseManager db = new DatabaseManager("jdbc:mariadb://localhost:3306/test", "root", "root");
        NeuralNetworkManager nnm = new NeuralNetworkManager(stack, proc, db);

        // 5. Predecir con un nuevo vector de características
        com.merlab.signals.core.Signal sample = new com.merlab.signals.core.Signal();
        sample.add(2000.0);  // sqft
        sample.add(3.0);     // habitaciones
        sample.add(20.0);    // edad
        stack.push(sample);
        var prediction = nnm.runInference();
        System.out.println("Precio estimado: " + prediction.getValues());
    }
}
