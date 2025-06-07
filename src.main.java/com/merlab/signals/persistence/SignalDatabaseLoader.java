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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalProvider;

/**
 * Carga una señal completa desde la tabla 'signals' en la base de datos.
 */
public class SignalDatabaseLoader implements SignalProvider {
    private final String url;
    private final String user;
    private final String password;

    public SignalDatabaseLoader(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Signal getSignal() {
        Signal signal = new Signal();
        String sql = "SELECT y FROM signals ORDER BY x";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Asumimos que 'y' es el valor de la señal
                signal.add(rs.getDouble("y"));
            }
        } catch (Exception e) {
            // Manejo simplificado; puedes mejorar con logs o rethrow
            e.printStackTrace();
            throw new RuntimeException("Error cargando la señal de la BD", e);
        }
        return signal;
    }
    

}
