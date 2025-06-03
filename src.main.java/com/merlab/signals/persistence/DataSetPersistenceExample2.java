package com.merlab.signals.persistence;

import com.merlab.signals.data.DataLoader;
import com.merlab.signals.data.DataLoaderConfig;
import com.merlab.signals.data.DataLoaderFactory;
import com.merlab.signals.data.DataSet;
import com.merlab.signals.data.DataSetIO;
import com.merlab.signals.core.Signal;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

/**
 * Ejemplo integral con nueva arquitectura:
 * - DataLoaderFactory
 * - DataSetIO
 * - DatabaseManager
 */
public class DataSetPersistenceExample2 {

    public static void main(String[] args) throws Exception {
        // 1) Configuración del loader sintético
        DataLoaderConfig cfg = new DataLoaderConfig();
        cfg.setSyntheticN(200);
        cfg.setSyntheticRInt(1.0);
        cfg.setSyntheticRExt(2.5);

        // 2) Instancia el loader y carga el DataSet
        DataLoader loader = DataLoaderFactory.create(
            DataLoaderFactory.Type.SYNTHETIC_CIRCLES, cfg
        );
        DataSet ds = loader.load();
        System.out.println("Generated DataSet size: " + ds.getInputs().size());

        // 3) Exportar a CSV
        Path csvPath = Path.of("out/circles2.csv");
        DataSetIO.saveToCsv(ds, csvPath);
        System.out.println("Saved CSV to: " + csvPath);

        // 4) Guardar en DB
        String url = "jdbc:mariadb://localhost:3306/test";
        DatabaseManager db = new DatabaseManager(url, "root", "root");
        db.saveDataSet(ds, "synthetic_ds2");
        System.out.println("Saved DataSet to DB table: synthetic_ds2");

        // 5) Cargar desde DB
        DataSet loaded = db.loadDataSet("synthetic_ds2");
        System.out.println("Loaded DataSet size: " + loaded.getInputs().size());

        // 6) Exportar DataSet cargado
        Path csvLoaded = Path.of("out/circles2_loaded.csv");
        DataSetIO.saveToCsv(loaded, csvLoaded);
        System.out.println("Saved loaded CSV to: " + csvLoaded);

        // 7) Convertir a array y mostrar primer registro
        double[][] arr = DataSetIO.toArray(loaded);
        System.out.printf("First row: [%.3f, %.3f, %.0f]\n",
            arr[0][0], arr[0][1], arr[0][2]
        );
    }
}
