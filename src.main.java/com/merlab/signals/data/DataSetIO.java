package com.merlab.signals.data;

import com.merlab.signals.core.Signal;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.merlab.signals.persistence.DatabaseManager;

/**
 * Utilidad de entrada/salida para DataSet.
 */
public class DataSetIO {
	

    /**
     * Guarda el DataSet en un archivo CSV.
     */
    public static void saveToCsv(DataSet ds, Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        List<String> lines = new ArrayList<>();
        // Header dinámico: in0,in1,...,out0,out1,...
        int numIn  = ds.getInputs().get(0).getValues().size();
        int numOut = ds.getTargets().get(0).getValues().size();
        String headerIn  = IntStream.range(0, numIn)
                            .mapToObj(i -> "in" + i)
                            .collect(Collectors.joining(","));
        String headerOut = IntStream.range(0, numOut)
                            .mapToObj(i -> "out" + i)
                            .collect(Collectors.joining(","));
        lines.add(headerIn + "," + headerOut);

        // Filas
        for (int idx = 0; idx < ds.getInputs().size(); idx++) {
            Stream<Double> inStream  = ds.getInputs().get(idx).getValues().stream();
            Stream<Double> outStream = ds.getTargets().get(idx).getValues().stream();
            String row = Stream.concat(inStream, outStream)
                               .map(Object::toString)
                               .collect(Collectors.joining(","));
            lines.add(row);
        }

        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Carga un DataSet desde un archivo CSV.
     */
    public static DataSet loadFromCsv(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        String[] headers = lines.get(0).split(",");
        int numIn  = (int) Stream.of(headers).filter(h -> h.startsWith("in")).count();
        int numOut = headers.length - numIn;

        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            Signal in = new Signal();
            for (int j = 0; j < numIn; j++) {
                in.add(Double.parseDouble(parts[j]));
            }
            inputs.add(in);

            Signal out = new Signal();
            for (int j = 0; j < numOut; j++) {
                out.add(Double.parseDouble(parts[numIn + j]));
            }
            targets.add(out);
        }
        return new DataSet(inputs, targets);
    }

    /**
     * Convierte un DataSet a un array double[n][m], donde m = numIn + numOut.
     */
    public static double[][] toArray(DataSet ds) {
        int n = ds.getInputs().size();
        int m = ds.getInputs().get(0).getValues().size() + ds.getTargets().get(0).getValues().size();
        double[][] arr = new double[n][m];
        for (int i = 0; i < n; i++) {
            int idx = 0;
            for (Double v : ds.getInputs().get(i).getValues()) {
                arr[i][idx++] = v;
            }
            for (Double v : ds.getTargets().get(i).getValues()) {
                arr[i][idx++] = v;
            }
        }
        return arr;
    }

    /**
     * Guarda un DataSet en la base de datos mediante DatabaseManager.
     */
    public static void saveToDatabase(DataSet ds, String tableName, DatabaseManager db) throws SQLException {
        db.saveDataSet(ds, tableName);
    }

    /**
     * Carga un DataSet desde la base de datos con DatabaseManager.
     */
    public static DataSet loadFromDatabase(String tableName, DatabaseManager db) throws SQLException {
        return db.loadDataSet(tableName);
    }
    
    

    /**
     * Guarda el DataSet en un archivo CSV.
     */
    /*
    public static void saveToCsv(DataSet ds, Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        List<String> lines = new ArrayList<>();
        lines.add("x,y,label");
        for (int i = 0; i < ds.getInputs().size(); i++) {
            Signal in = ds.getInputs().get(i);
            Signal out = ds.getTargets().get(i);
            lines.add(in.getValues().get(0) + "," + in.getValues().get(1) + "," + out.getValues().get(0));
        }
        Files.write(path, lines);
    }
     */
    /**
     * Carga un DataSet desde un archivo CSV.
     */
    /*
    public static DataSet loadFromCsv(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        List<Signal> inputs = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            Signal in = new Signal();
            in.add(Double.parseDouble(parts[0]));
            in.add(Double.parseDouble(parts[1]));
            inputs.add(in);
            Signal out = new Signal();
            out.add(Double.parseDouble(parts[2]));
            targets.add(out);
        }
        return new DataSet(inputs, targets);
    }
     */
    /**
     * Convierte un DataSet a un array double[n][3].
     */
    /*
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
    */

}
