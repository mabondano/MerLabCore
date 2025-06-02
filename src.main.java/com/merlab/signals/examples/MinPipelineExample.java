package com.merlab.signals.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalGenerator;
import com.merlab.signals.core.SignalManager;
import com.merlab.signals.core.SignalPlotter;
import com.merlab.signals.core.SignalProvider;
import com.merlab.signals.core.SignalStack;
import com.merlab.signals.core.SignalGenerator.Type;
import com.merlab.signals.persistence.DatabaseManager;

/**
 * Clase principal que configura y ejecuta el pipeline de procesamiento de señales.
 */
public class MinPipelineExample {
    public static void main(String[] args) {
        // Parámetros de conexión a la base de datos
    	String mi_base_de_datos = "test";
        String url = "jdbc:mariadb://localhost:3306/" + mi_base_de_datos;
        String user = "root";
        String pass = "root";

        // Selección de la fuente de señales (descomenta la opción deseada)
        //SignalProvider provider = new DatabaseLoader(url, user, pass);
        SignalProvider provider = new SignalGenerator(
            SignalGenerator.Type.SINE,   // Tipo de señal
            100,                         // Tamaño (número de muestras)
            1.0,                         // Amplitud
            1.0,                         // Frecuencia
            0.0,                         // Fase (solo para SINE o TRIANGLE)
            true,
            0,                           // Posición para DELTA
            1,							 // Para DC
            null                         // Ruta de archivo (solo para FROM_FILE)
        );

        // Instancias
        // Inicializar el stack y el gestor de base de datos
        SignalStack stack = new SignalStack();
        DatabaseManager dbManager = new DatabaseManager(url, user, pass);

        // Flags de qué etapas ejecutar
        boolean doStats    = false;  // calcular estadísticas
        boolean doFeatures = false;  // extraer características
        boolean doNN       = false;  // ejecutar red neuronal

        // Crear y ejecutar SignalManager
        SignalManager manager = new SignalManager(
            provider,
            stack,
            dbManager,
            doStats,
            doFeatures,
            doNN
        );
        manager.runPipeline();

        // Mostrar el contenido del stack por consola
        manager.showStack();
        
        // SUPONIENDO que `manager` ya ejecutó runPipeline() y quieres graficar la última señal:
        Signal finalSignal = manager.getLastSignal();
        SignalPlotter.plotSignal("Resultado final", finalSignal);
    }
}
