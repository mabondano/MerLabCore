package com.merlab.signals.data;

import com.merlab.signals.core.Signal;
import java.util.ArrayList;
import java.util.List;

/**
 * Ejemplo de cómo poblar un DataSet a partir de un array double[][]
 */
public class DataSetBuilder {
	
    /**
     * Construye un DataSet a partir de un array de filas [in..., out...].
     * @param data      matriz n×(in + out)
     * @param numInputs número de columnas de entrada
     * @return DataSet con listas de Signals de inputs y targets
     */
    public static DataSet fromArray(double[][] data, int numInputs) {
        int numCols   = data[0].length;
        int numTargets = numCols - numInputs;
        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();
        for (double[] row : data) {
            Signal in = new Signal();
            for (int j = 0; j < numInputs; j++) {
                in.add(row[j]);
            }
            inputs.add(in);

            Signal out = new Signal();
            for (int j = 0; j < numTargets; j++) {
                out.add(row[numInputs + j]);
            }
            targets.add(out);
        }
        return new DataSet(inputs, targets);
    }

    /**
     * Versión original: asume que la última columna es el target.
     */
    public static DataSet fromArray(double[][] data) {
        return fromArray(data, data[0].length - 1);
    }
    
    // Dentro de DataSetBuilder
    public static DataSet fromSignals(List<Signal> inputs, List<Signal> targets) {
        return new DataSet(inputs, targets);
    }


    /**
     * Construye un DataSet a partir de un array de filas [x, y, label]
     * @param data matriz n×3 con columnas [x, y, label]
     * @return DataSet con listas de Signals de inputs y targets
     */
    public static DataSet fromArray2(double[][] data) {
        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();
        
        for (double[] row : data) {
            Signal in = new Signal();
            in.add(row[0]);
            in.add(row[1]);
            inputs.add(in);

            Signal out = new Signal();
            out.add(row[2]);
            targets.add(out);
        }
        return new DataSet(inputs, targets);
    }

    public static void main(String[] args) {
        // Ejemplo de uso con SyntheticCirclesDataset
        double[][] raw = com.merlab.nn.examples.SyntheticCirclesDataset.generate(200, 1.0, 2.5);
        DataSet ds = fromArray(raw);
        System.out.println("Inputs: " + ds.getInputs().size() + ", Targets: " + ds.getTargets().size());
        // Imprime primer input/target
        System.out.println("Primer input: " + ds.getInputs().get(0).getValues());
        System.out.println("Primer target: " + ds.getTargets().get(0).getValues());
    }
}
