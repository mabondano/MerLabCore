package com.merlab.signals.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalGenerator;
import com.merlab.signals.core.SignalPlotter;
import com.merlab.signals.core.SignalProcessor;
import com.merlab.signals.core.SignalProvider;
import com.merlab.signals.persistence.DatabaseManager;

public class PlotPersistPipelineExample {
    public static void main(String[] args) {
        // Simulamos la conexión
        String url = "jdbc:mariadb://localhost:3306/test";
        String user = "root";
        String password = "root";

        // Generamos una señal de ejemplo
        SignalProvider provider = new SignalGenerator(
            SignalGenerator.Type.SINE, 32, 1.0, 1.0, 0.0, true, 0, 1, null
        );
        Signal raw = provider.getSignal();

        // Ploteo simulado
        SignalPlotter.plotSignal("Raw Signal", raw);

        // Simula procesamiento: (por ejemplo, normalización)
        Signal processed = new Signal(
            SignalProcessor.normalizeTo(raw.getValues(), 1.0)
        );
        SignalPlotter.plotSignal("Normalized Signal", processed);

        // Persistencia simulada
        DatabaseManager db = new DatabaseManager(url, user, password);
        db.saveSignal(processed);

        // Otras etapas… puedes encadenar como en el ejemplo original
    }
}
