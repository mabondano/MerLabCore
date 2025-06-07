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
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class SignalStack {
    private LinkedList<Signal> stack;
    //private final List<Signal> stack = new ArrayList<>();

    // Constructor
    public SignalStack() {
        this.stack = new LinkedList<>();
    }

    // Método para agregar una señal al stack
    public void push(Signal signal) {
        stack.push(signal);
    }
    
    /** Apila una señal */
    public void push2(Signal s) {
        stack.add(0, s);
    }

    // Método para eliminar la última señal agregada
    public Signal pop2() {
        if (!stack.isEmpty()) {
            return stack.pop();
        }
        return null; // Si el stack está vacío
    }
    
    /** Quita y devuelve la señal superior; si está vacío, lanza excepción */
    public Signal pop() {
        if (stack.isEmpty()) {
            throw new NoSuchElementException("SignalStack vacío");
        }
        //return stack.remove(0);
        return stack.pop();
    }    

    // Método para ver la última señal sin eliminarla
    public Signal peek() {
        if (!stack.isEmpty()) {
            return stack.peek();
        }
        return null; // Si el stack está vacío
    }
    
    /**
     * Devuelve la segunda señal desde el tope, sin remover nada.
     * @throws NoSuchElementException si no hay al menos dos señales.
     */
    public Signal peekSecond() {
        if (stack.size() < 2) {
            throw new NoSuchElementException("Se requieren ≥2 señales en el stack");
        }
        // Como push añade en head, index 0 = top, index 1 = segundo
        return stack.get(1);
    }

    // Método para obtener el tamaño del stack
    public int size() {
        return stack.size();
    }

    // Método para limpiar el stack
    public void clear() {
        stack.clear();
    }

    // Método para verificar si el stack está vacío
    public boolean isEmpty() {
        return stack.isEmpty();
    }
    
    // Función que devuelve el stack completo
    public List<Signal> getStack2() {
        return new ArrayList<>(stack); // Devolver una copia del stack como una lista
    }

    /** Devuelve una copia inmodificable del contenido actual del stack en orden LIFO */
    public List<Signal> getStack() {
        return Collections.unmodifiableList(new ArrayList<>(stack));
    }
    


}
