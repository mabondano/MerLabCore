package com.merlab.signals.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalGenerator;
import com.merlab.signals.core.SignalManager;
import com.merlab.signals.core.SignalPlotter;
import com.merlab.signals.core.SignalProcessor;
import com.merlab.signals.core.SignalProvider;
import com.merlab.signals.core.SignalStack;
import com.merlab.signals.core.StatisticalProcessor;
import com.merlab.signals.core.SignalGenerator.Type;
import com.merlab.signals.features.FeatureExtractor;
import com.merlab.signals.nn.processor.DefaultNeuralNetworkProcessor;
import com.merlab.signals.persistence.DatabaseManager;

public class BasicPipelineExample {
    public static void main(String[] args) {
        // Parámetros JDBC
        String url      = "jdbc:mariadb://localhost:3306/test";
        String user     = "root";
        String password = "root";

        // Fuente de señal (aquí sintética)
        SignalProvider provider = new SignalGenerator(
            SignalGenerator.Type.SINE, 100, 1.0, 1.0, 0.0, true, 0, 1, null
        );
        // SignalProvider provider = new DatabaseLoader(url, user, password);

        // Instancias
        SignalStack     stack      = new SignalStack();
        DatabaseManager db         = new DatabaseManager(url, user, password);

        // Flags de etapa
        boolean doStats    = true;
        boolean doFeatures = true;
        boolean doNN       = false;

        SignalManager manager = new SignalManager(
            provider, stack, db, doStats, doFeatures, doNN
        );

        // 1. Señal cruda
        Signal raw = provider.getSignal();
        SignalPlotter.plotSignal("Señal Original (Raw)", raw);
        stack.push(raw);

        // 2. Procesamiento básico
        Signal processed = SignalProcessor.process(raw);
        SignalPlotter.plotSignal("Señal Procesada", processed);
        stack.push(processed);

        // 3. Estadísticas
        if (doStats) {
            Signal stats = StatisticalProcessor.extractStats(processed);
            SignalPlotter.plotSignal("Media & Varianza", stats);
            stack.push(stats);
        }

        // 4. Extracción de características
        if (doFeatures) {
            // Usa stats si doStats==true, si no, processed
            Signal base = doStats ? stack.peek() : processed;
            Signal features = FeatureExtractor.extractFeatures(base);
            SignalPlotter.plotSignal("Características", features);
            stack.push(features);
        }

        // 5. Inferencia de red neuronal (placeholder)
        if (doNN) {
            Signal last = stack.peek();
            Signal nnOut = DefaultNeuralNetworkProcessor.predict2(last);
            SignalPlotter.plotSignal("Salida NN", nnOut);
            stack.push(nnOut);
        }

        // 6. Guardar resultado final
        manager.saveLastSignal();
    }
}
