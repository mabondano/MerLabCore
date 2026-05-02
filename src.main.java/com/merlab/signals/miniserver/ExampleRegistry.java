package com.merlab.signals.miniserver;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Catalog of all runnable examples in MerLabCore.
 *
 * Each entry maps a short command key → ExampleEntry (class name + description).
 * Add new examples here as they are created.
 */
public class ExampleRegistry {

    public record ExampleEntry(String className, String category, String description) {}

    /** Returns the full ordered catalog. Key = command used in CLI. */
    public static Map<String, ExampleEntry> all() {
        Map<String, ExampleEntry> map = new LinkedHashMap<>();

        // ── com.merlab.nn.examples ──────────────────────────────────────
        map.put("BasicNN",
            new ExampleEntry("com.merlab.nn.examples.BasicNNExample",
                "Neural Networks", "Basic neural network demo"));

        map.put("BasicNN4",
            new ExampleEntry("com.merlab.nn.examples.BasicNNExample4",
                "Neural Networks", "Basic neural network with linear processor"));

        map.put("MLPReg1",
            new ExampleEntry("com.merlab.nn.examples.MLPRegressionExample",
                "Neural Networks", "MLP Regression - sin(x) dataset"));

        map.put("MLPReg2",
            new ExampleEntry("com.merlab.nn.examples.MLPRegressionExample2",
                "Neural Networks", "MLP Regression - house prices"));

        map.put("MLPReg3",
            new ExampleEntry("com.merlab.nn.examples.MLPRegressionExample3",
                "Neural Networks", "MLP Regression - with mini-batches"));

        map.put("MLPReg4",
            new ExampleEntry("com.merlab.nn.examples.MLPRegressionExample4",
                "Neural Networks", "MLP Regression - with StandardScaler"));

        map.put("MLPReg5",
            new ExampleEntry("com.merlab.nn.examples.MLPRegressionExample5",
                "Neural Networks", "MLP Regression - with BatchNorm"));

        map.put("MLPReg6",
            new ExampleEntry("com.merlab.nn.examples.MLPRegressionExample6",
                "Neural Networks", "MLP Regression - with Dropout"));

        map.put("MLPReg7",
            new ExampleEntry("com.merlab.nn.examples.MLPRegressionExample7",
                "Neural Networks", "MLP Regression - learning rate schedule"));

        map.put("MLPReg8",
            new ExampleEntry("com.merlab.nn.examples.MLPRegressionExample8",
                "Neural Networks", "MLP Regression - MSE log visualization"));

        map.put("MLPReg9",
            new ExampleEntry("com.merlab.nn.examples.MLPRegressionExample9",
                "Neural Networks", "MLP Regression - full pipeline with Plotly"));

        map.put("MLPClass1",
            new ExampleEntry("com.merlab.nn.examples.MLPClassifyExample",
                "Neural Networks", "MLP Classification - basic"));

        map.put("MLPClass2",
            new ExampleEntry("com.merlab.nn.examples.MLPClassifyExample10",
                "Neural Networks", "MLP Classification - example 10"));

        map.put("MLPClass3",
            new ExampleEntry("com.merlab.nn.examples.MLPClassifyExample11",
                "Neural Networks", "MLP Classification - example 11"));

        map.put("LogReg",
            new ExampleEntry("com.merlab.nn.examples.LogisticRegressionExample",
                "Neural Networks", "Logistic Regression basic"));

        map.put("LogRegEnh",
            new ExampleEntry("com.merlab.nn.examples.LogisticRegressionEnhancedExample",
                "Neural Networks", "Logistic Regression enhanced with Plotly"));

        map.put("LogRegLinear",
            new ExampleEntry("com.merlab.nn.examples.LogisticRegressionLinearExample",
                "Neural Networks", "Logistic Regression linear separation"));

        map.put("LogRegRadial",
            new ExampleEntry("com.merlab.nn.examples.LogisticRegressionRadialFeatureExample",
                "Neural Networks", "Logistic Regression with radial feature"));

        map.put("LogRegSimple",
            new ExampleEntry("com.merlab.nn.examples.LogisticRegressionSimpleExample2",
                "Neural Networks", "Logistic Regression simple v2"));

        map.put("MedLogReg",
            new ExampleEntry("com.merlab.nn.examples.MedicalLogisticRegressionExample",
                "Neural Networks", "Medical Logistic Regression"));

        map.put("KNN",
            new ExampleEntry("com.merlab.nn.examples.KNearestExample2",
                "Neural Networks", "K-Nearest Neighbors with custom metrics"));

        // ── Clustering ──────────────────────────────────────────────────
        map.put("KMeans",
            new ExampleEntry("com.merlab.nn.examples.KMeansExample2",
                "Clustering", "K-Means clustering with centroids"));

        map.put("KMedians",
            new ExampleEntry("com.merlab.nn.examples.KMediansExample2",
                "Clustering", "K-Medians clustering"));

        map.put("SOM",
            new ExampleEntry("com.merlab.nn.examples.SOMExample2",
                "Clustering", "Self-Organizing Map (Kohonen)"));

        // ── com.merlab.signals.examples ─────────────────────────────────
        map.put("RealData",
            new ExampleEntry("com.merlab.signals.examples.RealDataExample",
                "Data", "Real data loading from CSV"));

        map.put("RealDataFactory",
            new ExampleEntry("com.merlab.signals.examples.RealDataFactoryExample",
                "Data", "Real data loading from CSV factory"));

        map.put("RealDataDBFactory",
            new ExampleEntry("com.merlab.signals.examples.RealDataFactoryFromDBExample",
                "Data", "Real data loading from database factory"));

        map.put("RealDataHTTP",
            new ExampleEntry("com.merlab.signals.examples.RealDataHTTPFactoryExample",
                "Data", "Real data loading from HTTP factory"));

        map.put("RealDataJSON",
            new ExampleEntry("com.merlab.signals.examples.RealDataJSONFactoryExample",
                "Data", "Real data loading from JSON"));

        return map;
    }

    /** Returns a formatted list for CLI help output. */
    public static String listFormatted() {
        StringBuilder sb = new StringBuilder();
        sb.append("Available examples:\n");
        sb.append(String.format("%-15s %-20s %s%n", "COMMAND", "CATEGORY", "DESCRIPTION"));
        sb.append("-".repeat(70)).append("\n");

        String lastCategory = "";
        for (var entry : all().entrySet()) {
            String cat = entry.getValue().category();
            if (!cat.equals(lastCategory)) {
                sb.append("\n[").append(cat).append("]\n");
                lastCategory = cat;
            }
            sb.append(String.format("  run %-12s %s%n",
                entry.getKey(),
                entry.getValue().description()));
        }
        return sb.toString();
    }
}
