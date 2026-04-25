// src/main/java/com/merlab/signals/data/DatabaseDataLoader.java
package com.merlab.signals.data;

import com.merlab.signals.core.Signal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga datos de una tabla SQL y los convierte en DataSet de Signals.
 */
public class SimpleDataSetDatabaseLoader{

    /**
     * Carga un DataSet completo desde la base de datos.
     *
     * @param jdbcUrl        URL JDBC (p.ej. "jdbc:mariadb://localhost:3306/test")
     * @param user           usuario de la BD
     * @param password       contraseña de la BD
     * @param table          nombre de la tabla
     * @param inputColumns   lista de columnas de entrada (features)
     * @param targetColumns  lista de columnas objetivo
     * @return DataSet con listas de Signals
     */
    public static DataSet loadFromDatabase(
            String jdbcUrl,
            String user,
            String password,
            String table,
            List<String> inputColumns,
            List<String> targetColumns
    ) throws SQLException {

        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();

        String allCols = String.join(", ",
                inputColumns) + ", " +
                String.join(", ", targetColumns);
        String query = "SELECT " + allCols + " FROM " + table;

        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(query)) {

            while (rs.next()) {
                Signal in = new Signal();
                Signal tg = new Signal();

                for (String col : inputColumns) {
                    in.add(rs.getDouble(col));
                }
                for (String col : targetColumns) {
                    tg.add(rs.getDouble(col));
                }
                inputs .add(in);
                targets.add(tg);
            }
        }

        return new DataSet(inputs, targets);
    }


}
