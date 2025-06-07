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

package com.merlab.signals.llm.manager;

import com.merlab.signals.llm.loader.LLMModelLoader;

public class LLMManager {
    private LLMModelLoader loader;

    public void setLoader(LLMModelLoader loader) {
        this.loader = loader;
    }

    public void loadModel(String path) {
        if (loader != null) loader.loadModel(path);
    }

    public String askLLM(String prompt) {
        if (loader != null) return loader.infer(prompt);
        return "No loader set.";
    }

    public void unloadModel() {
        if (loader != null) loader.unloadModel();
    }
}
