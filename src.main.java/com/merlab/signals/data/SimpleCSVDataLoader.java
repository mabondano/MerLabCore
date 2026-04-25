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
