// src/main/java/com/merlab/signals/data/CSVDataLoader.java
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
