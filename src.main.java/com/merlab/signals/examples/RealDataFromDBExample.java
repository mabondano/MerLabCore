package com.merlab.signals.examples;

// en tu RealDataExample.java o uno nuevo
import com.merlab.signals.data.SimpleDataSetDatabaseLoader;
import com.merlab.signals.data.DataSet;

import java.util.List;

public class RealDataFromDBExample {
    public static void main(String[] args) throws Exception {
        DataSet data = SimpleDataSetDatabaseLoader.loadFromDatabase(
            // parámetros de tu MariaDB
            "jdbc:mariadb://localhost:3306/test",
            "tu_usuario",
            "tu_contraseña",
            "house_prices",                 // tabla con columnas sqft, bedrooms, age, price
            List.of("sqft", "bedrooms", "age"),
            List.of("price")
        );

        // Continua igual: entrenar, inferir...
        // Por ejemplo:
        var inputs  = data.getInputs();
        var targets = data.getTargets();
        // ...
    }
}
