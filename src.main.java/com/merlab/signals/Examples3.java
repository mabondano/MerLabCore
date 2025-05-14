package com.merlab.signals;

public class Examples3 {
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
            Signal nnOut = NeuralNetworkProcessor.predict(last);
            SignalPlotter.plotSignal("Salida NN", nnOut);
            stack.push(nnOut);
        }

        // 6. Guardar resultado final
        manager.saveLastSignal();
    }
}
