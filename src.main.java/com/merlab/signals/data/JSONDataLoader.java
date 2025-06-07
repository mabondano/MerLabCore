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

package com.merlab.signals.data;

import com.merlab.signals.core.Signal;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * DataLoader que lee un array JSON de objetos,
 * donde las propiedades inputCols y targetCols definen
 * las columnas de features y targets respectivamente.
 */
public class JSONDataLoader implements DataLoader {
    private final Path jsonPath;
    private final List<String> inputCols;
    private final List<String> targetCols;

    public JSONDataLoader(Path jsonPath,
                          List<String> inputCols,
                          List<String> targetCols) {
        this.jsonPath   = jsonPath;
        this.inputCols  = inputCols;
        this.targetCols = targetCols;
    }

    @Override
    public DataSet load() throws Exception {
        String content = Files.readString(jsonPath);
        JSONArray array = new JSONArray(content);

        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Signal in = new Signal();
            Signal tg = new Signal();
            for (String col : inputCols) {
                in.add(obj.getDouble(col));
            }
            for (String col : targetCols) {
                tg.add(obj.getDouble(col));
            }
            inputs .add(in);
            targets.add(tg);
        }
        return new DataSet(inputs, targets);
    }
}
