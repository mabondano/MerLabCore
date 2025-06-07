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

package com.merlab.signals.llm.loader;

/**
 * Loader que utiliza JNI/JNA para llamar a llama.cpp.
 */
public class LlamaCppLoader implements LLMModelLoader {
	
    // Carga la librería nativa al iniciar la clase
    static {
        System.loadLibrary("LlamaCppLoader"); // Se busca LlamaCppLoader.dll en el PATH
    }
    
    // Métodos nativos (deben tener implementación en C)
    public native long initModel(String modelPath);
    public native String infer(long modelPtr, String prompt, int maxTokens, double temperature);
    public native void freeModel(long modelPtr);

    // Implementación del interface (simplificada)
    private long modelPtr = 0;
    
    @Override
    public void loadModel(String path) {
        modelPtr = initModel(path);
    }

    @Override
    public String infer(String prompt) {
        // Hardcodeamos params para empezar, luego puedes conectar con LLMRequest
        return infer(modelPtr, prompt, 64, 0.7);
    }
    
    @Override
    public String infer(String prompt, int maxTokens, double temperature) {
        return infer(modelPtr, prompt, maxTokens, temperature);
    }

    @Override
    public void unloadModel() {
        freeModel(modelPtr);
        modelPtr = 0;
    }
}
