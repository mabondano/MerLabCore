package com.merlab.signals.examples;

import com.merlab.signals.data.DataLoader;
import com.merlab.signals.data.DataLoaderFactory;
import com.merlab.signals.data.DataLoaderFactory.Type;
import com.merlab.signals.data.DataLoaderConfig;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.manager.NeuralNetworkManager;
import com.merlab.signals.nn.model.RegressionModel;
import com.merlab.signals.nn.processor.LinearProcessor;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.nn.trainer.LinearRegressionTrainer4;
import com.merlab.signals.nn.trainer.RegressionTrainer;
import com.merlab.signals.persistence.DatabaseManager;
import com.merlab.signals.core.SignalStack;

import java.nio.file.Paths;
import java.util.List;

/**
 * Ejemplo de carga vía HTTP (JSON o CSV) usando DataLoaderFactory.
 */
public class RealDataHTTPFactoryExample {

    public static void main(String[] args) throws Exception {
        // 1. Configurar el DataLoader para HTTP + JSON
        DataLoaderConfig cfg = new DataLoaderConfig();
        cfg.setHttpUrl("http://localhost:4567/data/json");
        cfg.setHttpIsJson(true);
        cfg.setInputCols("sqft,bedrooms,age");
        cfg.setTargetCols("price");

        DataLoader loader = DataLoaderFactory.create(Type.HTTP, cfg);

        // 2. Cargar el DataSet
        DataSet data = loader.load();
        var inputs  = data.getInputs();
        var targets = data.getTargets();

        // 3. Entrenar regresión lineal
        RegressionTrainer trainer = new LinearRegressionTrainer4();
        RegressionModel model = trainer.train(
            inputs, targets, Paths.get("model_http_json.txt")
        );

        // 4. Preparar el procesador lineal y el manager
        NeuralNetworkProcessor proc =
            new LinearProcessor(model.getWeights(), model.getBias());
        SignalStack stack = new SignalStack();
        DatabaseManager db = new DatabaseManager("jdbc:mariadb://localhost:3306/test", "root", "root");
        NeuralNetworkManager nnManager = new NeuralNetworkManager(stack, proc, db);

        // 5. Inferencia de ejemplo
        Signal sample = new Signal();
        sample.add(2000.0);
        sample.add(3.0);
        sample.add(20.0);
        stack.push(sample);

        Signal prediction = nnManager.runInference();
        System.out.println("Predicción precio [HTTP JSON]: " + prediction.getValues());

        nnManager.saveResult();


        // 6. Ahora probamos HTTP + CSV
        cfg.setHttpUrl("http://localhost:4567/data/csv");
        cfg.setHttpIsJson(false);

        loader = DataLoaderFactory.create(Type.HTTP, cfg);
        data   = loader.load();
        inputs = data.getInputs();
        targets = data.getTargets();

        // Re-entrenar o reutilizar: aquí solo hacemos otra inferencia
        stack.clear();
        stack.push(sample);
        prediction = nnManager.runInference();
        System.out.println("Predicción precio [HTTP CSV]: " + prediction.getValues());
    }
}

//http://localhost:4567/data/json