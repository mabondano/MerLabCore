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

package com.merlab.signals.persistence;

import com.merlab.signals.core.Signal;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Ejemplos de persistencia y carga de DataSet.
 */
public class DataSetPersistenceExample {

    // A) Guardar array raw en BD
    public static void saveRawArray(
            double[][] raw,
            String tableName,
            String url,
            String user,
            String password
    ) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            // Crear tabla
            stmt.execute("CREATE TABLE IF NOT EXISTS " + tableName + " ("
                       + "x DOUBLE, y DOUBLE, label INT)");
            // Insertar filas
            String sql = "INSERT INTO " + tableName + " (x,y,label) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (double[] row : raw) {
                    ps.setDouble(1, row[0]);
                    ps.setDouble(2, row[1]);
                    ps.setInt(3, (int) row[2]);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    // B) Guardar un DataSet en BD
    public static void saveDataSet(
            DataSet ds,
            String tableName,
            String url,
            String user,
            String password
    ) throws SQLException {
        double[][] raw = new double[ds.getInputs().size()][3];
        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal in = ds.getInputs().get(i);
            Signal out = ds.getTargets().get(i);
            raw[i][0] = in.getValues().get(0);
            raw[i][1] = in.getValues().get(1);
            raw[i][2] = out.getValues().get(0);
        }
        saveRawArray(raw, tableName, url, user, password);
    }

    // C) DatabaseLoader ya existe para cargar SignalProvider pero aquí adaptamos a DataSet
    public static DataSet loadFromDatabase(
            String tableName,
            String url,
            String user,
            String password
    ) throws SQLException {
        List<Signal> inputs = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();
        String sql = "SELECT x,y,label FROM " + tableName;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Signal in = new Signal();
                in.add(rs.getDouble("x"));
                in.add(rs.getDouble("y"));
                inputs.add(in);
                Signal out = new Signal();
                out.add(rs.getDouble("label"));
                targets.add(out);
            }
        }
        return new DataSet(inputs, targets);
    }

    // D) Guardar DataSet en CSV
    public static void saveDataSetToCsv2(
            DataSet ds,
            Path path
    ) throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add("x,y,label");
        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal in = ds.getInputs().get(i);
            Signal out = ds.getTargets().get(i);
            lines.add(
                in.getValues().get(0) + "," +
                in.getValues().get(1) + "," +
                out.getValues().get(0)
            );
        }
        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    // D) Guardar DataSet en CSV (con creación de carpeta)
    public static void saveDataSetToCsv(
            DataSet ds,
            Path path
    ) throws IOException {
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        List<String> lines = new ArrayList<>();
        lines.add("x,y,label");
        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal in = ds.getInputs().get(i);
            Signal out = ds.getTargets().get(i);
            lines.add(
                in.getValues().get(0) + "," +
                in.getValues().get(1) + "," +
                out.getValues().get(0)
            );
        }
        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }    

    // E) Convertir DataSet de DB a array
    public static double[][] toArray(DataSet ds) {
        int n = ds.getInputs().size();
        double[][] arr = new double[n][3];
        for (int i = 0; i < n; i++) {
            Signal in = ds.getInputs().get(i);
            Signal out = ds.getTargets().get(i);
            arr[i][0] = in.getValues().get(0);
            arr[i][1] = in.getValues().get(1);
            arr[i][2] = out.getValues().get(0);
        }
        return arr;
    }

    public static void main(String[] args) throws Exception {
        double[][] raw = com.merlab.nn.examples.SyntheticCirclesDataset.generate(100, 1.0, 2.5);
        String url = "jdbc:mariadb://localhost:3306/test";
        String user = "root";
        String pass = "root";
        String table = "synthetic_circles";

        // A) raw -> DB
        saveRawArray(raw, table, url, user, pass);

        // B) DataSet -> DB
        DataSet ds = DataSetBuilder.fromArray(raw);
        saveDataSet(ds, table + "_ds", url, user, pass);

        // C) DB -> DataSet
        DataSet loaded = loadFromDatabase(table, url, user, pass);
        System.out.println("Loaded size: " + loaded.getInputs().size());

        // D) DataSet -> CSV
        //saveDataSetToCsv2(loaded, Path.of("out/circles.csv"));
        // Ajuste: especificar carpeta existente o crear 'out' antes de escribir
        Path outCsv = Path.of("out", "circles.csv");
        saveDataSetToCsv(loaded, outCsv);

        // E) DataSet -> array
        double[][] backArray = toArray(loaded);
        System.out.println("Back array[0]: " +
            backArray[0][0] + "," + backArray[0][1] + "," + backArray[0][2]);
    }
    

}
