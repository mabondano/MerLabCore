// src/main/java/com/merlab/signals/data/DataSetDatabaseLoader.java
package com.merlab.signals.data;

import com.merlab.signals.core.Signal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DataLoader que lee un conjunto de datos desde tabla SQL.
 * inputCols y targetCols definen qué columnas usar.
 */
public class DataSetDatabaseLoader implements DataLoader {
    private final String            jdbcUrl;
    private final String            user;
    private final String            password;
    private final String            table;
    private final List<String>      inputCols;
    private final List<String>      targetCols;

    public DataSetDatabaseLoader(
            String jdbcUrl,
            String user,
            String password,
            String table,
            List<String> inputCols,
            List<String> targetCols
    ) {
        this.jdbcUrl    = jdbcUrl;
        this.user       = user;
        this.password   = password;
        this.table      = table;
        this.inputCols  = inputCols;
        this.targetCols = targetCols;
    }

    @Override
    public DataSet load() throws SQLException {
        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();

        String allCols = String.join(", ", inputCols) + ", " + String.join(", ", targetCols);
        String sql     = "SELECT " + allCols + " FROM " + table;

        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Signal in = new Signal();
                Signal tg = new Signal();
                for (String col : inputCols) {
                    in.add(rs.getDouble(col));
                }
                for (String col : targetCols) {
                    tg.add(rs.getDouble(col));
                }
                inputs .add(in);
                targets.add(tg);
            }
        }
        return new DataSet(inputs, targets);
    }
}
