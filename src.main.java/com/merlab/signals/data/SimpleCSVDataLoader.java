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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SimpleCSVDataLoader {

    /**
     * Carga un CSV donde las primeras nColsInput columnas son features
     * y las siguientes nColsTarget son targets. Salta la primera línea (header).
     */
    public static DataSet load(Path csvPath, int nColsInput, int nColsTarget) throws IOException {
        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();

        List<String> lines = Files.readAllLines(csvPath);
        // Saltar la cabecera:
        for (int idx = 1; idx < lines.size(); idx++) {
            String line = lines.get(idx).trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split(",");
            Signal in = new Signal();
            Signal tg = new Signal();

            // Parsear features
            for (int i = 0; i < nColsInput; i++) {
                in.add(Double.parseDouble(tokens[i]));
            }
            // Parsear target(s)
            for (int j = 0; j < nColsTarget; j++) {
                tg.add(Double.parseDouble(tokens[nColsInput + j]));
            }
            inputs .add(in);
            targets.add(tg);
        }

        return new DataSet(inputs, targets);
    }


}
