/*
 * Copyright 2025 Merly Abondano
 *
 * Created:   2025-06-07
 * Author:    Merly Abondano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.merlab.signals.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una señal como una lista de valores numéricos.
 */
public class Signal {
    private final List<Double> values;

    /**
     * Crea una señal vacía.
     */
    public Signal() {
        this.values = new ArrayList<>();
    }

    /**
     * Crea una señal inicializada con una lista dada.
     * Se realiza una copia defensiva para proteger la lista original.
     *
     * @param initial valores iniciales de la señal
     */
    public Signal(List<Double> initial) {
        this.values = new ArrayList<>(initial);
    }

    /**
     * Agrega un nuevo valor al final de la señal.
     *
     * @param value valor a agregar
     */
    public void add(double value) {
        values.add(value);
    }

    /**
     * Obtiene el valor en la posición i.
     *
     * @param i índice de la muestra
     * @return valor en la posición i
     */
    public double get(int i) {
        return values.get(i);
    }

    /**
     * Devuelve el número de muestras en la señal.
     *
     * @return tamaño de la señal
     */
    public int size() {
        return values.size();
    }

    /**
     * Devuelve una vista inmutable de los valores de la señal.
     *
     * @return lista no modificable de valores
     */
    public List<Double> getValues() {
        return Collections.unmodifiableList(values);
    }
    
    
    // Setter 
    /*
    public void setValues(List<Double> values) {
        this.values = values;
    }
    */
    
    /**
     * Reemplaza los valores de la señal con una nueva lista.
     * Se limpia la lista interna y se agregan todos los valores de la nueva lista.
     *
     * @param newValues lista de valores para actualizar la señal
     */
    public void setValues(List<Double> newValues) {
        values.clear();
        values.addAll(newValues);
    }


    public void print() {
        for (int i = 0; i < values.size(); i++) {
            System.out.printf("x=%d, y=%.2f%n", i + 1, values.get(i));
        }
    }
    
    // Método para imprimir las señales
    public void println() {
        values.forEach(System.out::println);
    }
    
    /**
     * Devuelve una copia de la lista interna de datos.
     * Así evitas exponer la lista mutable directamente.
     */
    public List<Double> getData() {
        return getValues();
    }
}







