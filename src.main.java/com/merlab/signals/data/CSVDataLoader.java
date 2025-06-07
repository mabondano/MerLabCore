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

/**
 * DataLoader que lee un CSV con header. 
 * Las primeras nColsInput columnas son las features,
 * las siguientes nColsTarget son los targets.
 */
public class CSVDataLoader implements DataLoader {
    private final Path csvPath;
    private final int nColsInput;
    private final int nColsTarget;

    public CSVDataLoader(Path csvPath, int nColsInput, int nColsTarget) {
        this.csvPath      = csvPath;
        this.nColsInput   = nColsInput;
        this.nColsTarget  = nColsTarget;
    }

    @Override
    public DataSet load() throws IOException {
        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();

        List<String> lines = Files.readAllLines(csvPath);
        // Saltamos la primera línea (header)
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split(",");
            Signal in = new Signal();
            Signal tg = new Signal();
            for (int j = 0; j < nColsInput; j++) {
                in.add(Double.parseDouble(tokens[j]));
            }
            for (int j = 0; j < nColsTarget; j++) {
                tg.add(Double.parseDouble(tokens[nColsInput + j]));
            }
            inputs .add(in);
            targets.add(tg);
        }
        return new DataSet(inputs, targets);
    }
}
