package com.merlab.signals.persistence;

import com.merlab.signals.data.DataSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.merlab.signals.core.Signal;

/**
 * Gestor de persistencia para DataSet y raw arrays.
 */
public class DatabaseManager {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseManager(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    // Método para guardar una señal en la base de datos
    public void saveSignal(Signal signal) {
    	// Simula el guardado: solo imprime mensaje.
        System.out.println("[Save] Signal with " + signal.getValues().size() + " points saved to DB (" + url + ")");
        
        String query = "INSERT INTO signals (x, y) VALUES (?, ?)"; // Suponiendo que la tabla tiene columnas 'x' y 'y'
        
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
             
            for (int i = 0; i < signal.size(); i++) {
                statement.setInt(1, i + 1); // Suponemos que 'x' es el índice
                statement.setDouble(2, signal.get(i)); // 'y' es el valor de la señal
                statement.addBatch(); // Agregamos a un lote
            }

            statement.executeBatch(); // Ejecutamos el lote para insertar todas las señales
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Guarda un array raw [n][3] como tabla en la base de datos (x, y, label).
     */
    public void saveRawArray(double[][] raw, String tableName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, user, password);
             var stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS " + tableName + " ("
                       + "x DOUBLE, y DOUBLE, label INT)");
            String sql = "INSERT INTO " + tableName + " (x, y, label) VALUES (?, ?, ?)";
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
    
    /**
     * Guarda un DataSet completo en la tabla indicada.
     */
    public void saveDataSet(DataSet ds, String tableName) throws SQLException {
        int n = ds.getInputs().size();
        double[][] raw = new double[n][3];
        for (int i = 0; i < n; i++) {
            var in = ds.getInputs().get(i);
            var out = ds.getTargets().get(i);
            raw[i][0] = in.getValues().get(0);
            raw[i][1] = in.getValues().get(1);
            raw[i][2] = out.getValues().get(0);
        }
        saveRawArray(raw, tableName);
    }
    
    /**
     * Carga un DataSet desde la tabla indicada.
     */
    public DataSet loadDataSet(String tableName) throws SQLException {
        String sql = "SELECT x, y, label FROM " + tableName;
        List<com.merlab.signals.core.Signal> inputs = new ArrayList<>();
        List<com.merlab.signals.core.Signal> targets = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                var in = new com.merlab.signals.core.Signal();
                in.add(rs.getDouble("x"));
                in.add(rs.getDouble("y"));
                inputs.add(in);

                var out = new com.merlab.signals.core.Signal();
                out.add(rs.getDouble("label"));
                targets.add(out);
            }
        }
        return new DataSet(inputs, targets);
    }
    

}

