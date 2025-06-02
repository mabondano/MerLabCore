package com.merlab.signals.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import com.merlab.signals.core.Signal;

public class DatabaseManager {

    private String url;
    private String user;
    private String password;

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
}
