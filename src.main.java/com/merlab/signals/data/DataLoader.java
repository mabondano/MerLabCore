package com.merlab.signals.data;

/**
 * Contrato común para cargar un DataSet desde cualquier origen.
 */
public interface DataLoader {
    /**
     * Carga y devuelve un DataSet de señales de entrada y objetivo.
     */
    DataSet load() throws Exception;
}
