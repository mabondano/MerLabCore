package com.merlab.signals.nn.manager;

import java.util.Objects;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalStack;
import com.merlab.signals.nn.processor.NeuralNetworkProcessor;
import com.merlab.signals.persistence.DatabaseManager;



/**
 * Orquesta la inferencia de la red neuronal sobre la señal en el stack.
 * Mantiene separado el crecimiento de la lógica de NN.
 */
public class NeuralNetworkManager {

    private final SignalStack signalStack;
    private final NeuralNetworkProcessor nnProcessor;
    private final DatabaseManager databaseManager;

    /**
     * Construye un manager de red neuronal.
     *
     * @param signalStack     el stack que contiene la señal de entrada (features)
     * @param nnProcessor     la implementación de inferencia de la red neuronal
     * @param databaseManager gestor para persistir la señal final
     */
    public NeuralNetworkManager(SignalStack signalStack,
                               NeuralNetworkProcessor nnProcessor,
                                DatabaseManager databaseManager) {
        this.signalStack     = Objects.requireNonNull(signalStack, "signalStack");
        this.nnProcessor     = Objects.requireNonNull(nnProcessor, "nnProcessor");
        this.databaseManager = Objects.requireNonNull(databaseManager, "databaseManager");
    }

    /**
     * Ejecuta la inferencia sobre la última señal en el stack y la apila.
     *
     * @return la señal resultante de la inferencia
     */
    public Signal runInference() {
        if (signalStack.isEmpty()) {
            throw new IllegalStateException("No hay señal en el stack para inferir");
        }
        Signal input  = signalStack.peek();
        Signal output = nnProcessor.predict(input);
        signalStack.push(output);
        return output;
    }

    /**
     * Persiste la señal resultante de la inferencia en la base de datos.
     */
    public void saveResult() {
        Signal result = signalStack.peek();
        databaseManager.saveSignal(result);
    }
}
