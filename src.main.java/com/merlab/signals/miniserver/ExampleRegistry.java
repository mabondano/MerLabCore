package com.merlab.signals.miniserver;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Catalog of runnable examples in MerLabCore.
 *
 * Each entry maps a short command key to an ExampleEntry. The MiniServer uses
 * this registry as the source of truth for the CLI index and run commands.
 */
public class ExampleRegistry {

    public record ExampleEntry(String className, String category, String description) {}

    /** Returns the full ordered catalog. Key = command used in CLI. */
    public static Map<String, ExampleEntry> all() {
        Map<String, ExampleEntry> map = new LinkedHashMap<>();

        // Neural network basics
        add(map, "BasicNN", "com.merlab.nn.examples.BasicNNExample", "Neural Networks", "Basic neural network demo");
        add(map, "BasicNN2", "com.merlab.nn.examples.BasicNNExample2", "Neural Networks", "Basic neural network demo 2");
        add(map, "BasicNN3", "com.merlab.nn.examples.BasicNNExample3", "Neural Networks", "Basic neural network demo 3");
        add(map, "BasicNN4", "com.merlab.nn.examples.BasicNNExample4", "Neural Networks", "Basic neural network with linear processor");
        add(map, "BasicPerceptron", "com.merlab.nn.examples.BasicPerceptronExample", "Neural Networks", "Basic perceptron demo");

        // Regression
        add(map, "HousePriceReg", "com.merlab.nn.examples.HousePriceRegressionExample", "Regression", "House price regression");
        add(map, "HousePriceReg12", "com.merlab.nn.examples.HousePriceRegressionExample12", "Regression", "House price regression 12");
        add(map, "HousePriceReg12b", "com.merlab.nn.examples.HousePriceRegressionExample12b", "Regression", "House price regression 12b");
        add(map, "HousePriceReg13", "com.merlab.nn.examples.HousePriceRegressionExample13", "Regression", "House price regression 13");
        add(map, "MLPReg1", "com.merlab.nn.examples.MLPRegressionExample", "Regression", "MLP regression - sin(x) dataset");
        add(map, "MLPReg2", "com.merlab.nn.examples.MLPRegressionExample2", "Regression", "MLP regression - house prices");
        add(map, "MLPReg3", "com.merlab.nn.examples.MLPRegressionExample3", "Regression", "MLP regression - mini-batches");
        add(map, "MLPReg4", "com.merlab.nn.examples.MLPRegressionExample4", "Regression", "MLP regression - StandardScaler");
        add(map, "MLPReg5", "com.merlab.nn.examples.MLPRegressionExample5", "Regression", "MLP regression - BatchNorm");
        add(map, "MLPReg5b", "com.merlab.nn.examples.MLPRegressionExample5b", "Regression", "MLP regression - BatchNorm variant");
        add(map, "MLPReg6", "com.merlab.nn.examples.MLPRegressionExample6", "Regression", "MLP regression - Dropout");
        add(map, "MLPReg7", "com.merlab.nn.examples.MLPRegressionExample7", "Regression", "MLP regression - learning rate schedule");
        add(map, "MLPReg8", "com.merlab.nn.examples.MLPRegressionExample8", "Regression", "MLP regression - MSE log visualization");
        add(map, "MLPReg9", "com.merlab.nn.examples.MLPRegressionExample9", "Regression", "MLP regression - full pipeline with Plotly");
        add(map, "MLPReg9CDN", "com.merlab.nn.examples.MLPRegressionExample9_WithPlotlyCDN", "Regression", "MLP regression - Plotly CDN");
        add(map, "MLPReg9CDN2", "com.merlab.nn.examples.MLPRegressionExample9_WithPlotlyCDN2", "Regression", "MLP regression - Plotly CDN 2");
        add(map, "MLPReg9Inline", "com.merlab.nn.examples.MLPRegressionExample9_WithPlotlyInline", "Regression", "MLP regression - inline Plotly");
        add(map, "MLPReg12", "com.merlab.nn.examples.MLPRegressionExample12", "Regression", "MLP regression example 12");

        // Classification
        add(map, "MLPClass1", "com.merlab.nn.examples.MLPClassifyExample", "Classification", "MLP classification - basic");
        add(map, "MLPClass10", "com.merlab.nn.examples.MLPClassifyExample10", "Classification", "MLP classification example 10");
        add(map, "MLPClass10b", "com.merlab.nn.examples.MLPClassifyExample10b", "Classification", "MLP classification example 10b");
        add(map, "MLPClass11", "com.merlab.nn.examples.MLPClassifyExample11", "Classification", "MLP classification example 11");
        add(map, "MLPClass11Log", "com.merlab.nn.examples.MLPClassifyExample11_WithLogging", "Classification", "MLP classification with logging");
        add(map, "MLPClass11Log2", "com.merlab.nn.examples.MLPClassifyExample11_WithLogging2", "Classification", "MLP classification with logging 2");
        add(map, "MLPClass11Log3", "com.merlab.nn.examples.MLPClassifyExample11_WithLogging3", "Classification", "MLP classification with logging 3");
        add(map, "LogReg", "com.merlab.nn.examples.LogisticRegressionExample", "Classification", "Logistic regression basic");
        add(map, "LogReg2", "com.merlab.nn.examples.LogisticRegressionExample2", "Classification", "Logistic regression concentric raw x,y");
        add(map, "LogRegEnh", "com.merlab.nn.examples.LogisticRegressionEnhancedExample", "Classification", "Logistic regression enhanced with Plotly");
        add(map, "LogRegEnh2", "com.merlab.nn.examples.LogisticRegressionEnhancedExample2", "Classification", "Logistic regression enhanced v2");
        add(map, "LogRegLinear", "com.merlab.nn.examples.LogisticRegressionLinearExample", "Classification", "Logistic regression linear separation");
        add(map, "LogRegRadial", "com.merlab.nn.examples.LogisticRegressionRadialFeatureExample", "Classification", "Logistic regression with radial feature");
        add(map, "LogRegSimple", "com.merlab.nn.examples.LogisticRegressionSimpleExample", "Classification", "Logistic regression simple");
        add(map, "LogRegSimple2", "com.merlab.nn.examples.LogisticRegressionSimpleExample2", "Classification", "Logistic regression simple v2");
        add(map, "MedLogReg", "com.merlab.nn.examples.MedicalLogisticRegressionExample", "Classification", "Medical logistic regression");
        add(map, "MedLogReg2", "com.merlab.nn.examples.MedicalLogisticRegressionExample2", "Classification", "Medical logistic regression 2");
        add(map, "LogicGateChart", "com.merlab.nn.examples.LogicGateChartExample", "Classification", "Logic gate chart example");
        add(map, "LogicGateScatter", "com.merlab.nn.examples.LogicGateScatterExample", "Classification", "Logic gate scatter example");
        add(map, "XORMLP", "com.merlab.nn.examples.XORMLPExample", "Classification", "XOR MLP example");
        add(map, "XORMLPPlot", "com.merlab.nn.examples.XORMLPExamplePlot", "Classification", "XOR MLP Plotly example");

        // Clustering and neighbors
        add(map, "KMeans", "com.merlab.nn.examples.KMeansExample2", "Clustering", "K-Means clustering with centroids");
        add(map, "KMedians", "com.merlab.nn.examples.KMediansExample2", "Clustering", "K-Medians clustering");
        add(map, "SOM", "com.merlab.nn.examples.SOMExample2", "Clustering", "Self-Organizing Map (Kohonen)");
        add(map, "KNN", "com.merlab.nn.examples.KNearestExample", "Neighbors", "K-Nearest Neighbors basic");
        add(map, "KNN2", "com.merlab.nn.examples.KNearestExample2", "Neighbors", "K-Nearest Neighbors with custom metrics");
        add(map, "KNNCentroids", "com.merlab.nn.examples.KNearestWithCentroidsExample", "Neighbors", "KNN with centroids");
        add(map, "KNNCentroids2", "com.merlab.nn.examples.KNearestWithCentroidsExample2", "Neighbors", "KNN with centroids 2");
        add(map, "Hierarchical2", "com.merlab.nn.examples.HierarchicalExample2", "Clustering", "Hierarchical clustering example");

        // Data and datasets
        add(map, "SyntheticCircles", "com.merlab.nn.examples.SyntheticCirclesDataset", "Data", "Synthetic circles dataset demo");
        add(map, "RealData", "com.merlab.signals.examples.RealDataExample", "Data", "Real data loading from CSV");
        add(map, "RealDataFactory", "com.merlab.signals.examples.RealDataFactoryExample", "Data", "Real data loading from CSV factory");
        add(map, "RealDataDBFactory", "com.merlab.signals.examples.RealDataFactoryFromDBExample", "Data", "Real data loading from database factory");
        add(map, "RealDataFromDB", "com.merlab.signals.examples.RealDataFromDBExample", "Data", "Real data loading from database");
        add(map, "RealDataHTTP", "com.merlab.signals.examples.RealDataHTTPFactoryExample", "Data", "Real data loading from HTTP factory");
        add(map, "RealDataJSON", "com.merlab.signals.examples.RealDataJSONFactoryExample", "Data", "Real data loading from JSON");

        // Signal, pipeline, RPN and plotting examples
        add(map, "Main", "com.merlab.signals.examples.Main", "Signal Processing", "Main signal processing pipeline");
        add(map, "MultiStyleChart", "com.merlab.signals.examples.MultiStyleChartExample", "Plotting", "Multi-style chart example");
        add(map, "BasicPipeline", "com.merlab.signals.examples.BasicPipelineExample", "Pipeline", "Basic pipeline example");
        add(map, "MinPipeline", "com.merlab.signals.examples.MinPipelineExample", "Pipeline", "Minimal pipeline example");
        add(map, "PlotPersistPipeline", "com.merlab.signals.examples.PlotPersistPipelineExample", "Pipeline", "Plot and persist pipeline example");
        add(map, "RPNUsage", "com.merlab.signals.examples.ExampleRPNUsage", "RPN", "RPN usage example");
        add(map, "RPNParser", "com.merlab.signals.examples.ExampleWithParser", "RPN", "RPN parser example");
        add(map, "RPNEngine", "com.merlab.signals.examples.RPNEngineExample", "RPN", "RPN engine example");
        add(map, "RPNEngine2", "com.merlab.signals.examples.RPNEngineExample2", "RPN", "RPN engine example 2");
        add(map, "RPNSignalPlotSave", "com.merlab.signals.examples.RPNSignalPlotSaveExample", "RPN", "RPN signal plot/save example");
        add(map, "RPNStack", "com.merlab.signals.examples.RPNStackExample", "RPN", "RPN stack example");
        add(map, "PrintHelp", "com.merlab.signals.examples.PrintHelpExample", "Utilities", "Print help example");
        add(map, "Examples", "com.merlab.signals.examples.Examples", "Utilities", "General examples collection");
        add(map, "TestDLL", "com.merlab.signals.examples.TestDLL", "Utilities", "Native DLL test example");

        return map;
    }

    /** Returns a formatted list for CLI help output. */
    public static String listFormatted() {
        StringBuilder sb = new StringBuilder();
        sb.append("Available examples:\n");
        sb.append(String.format("%-20s %-20s %s%n", "COMMAND", "CATEGORY", "DESCRIPTION"));
        sb.append("-".repeat(80)).append("\n");

        String lastCategory = "";
        for (var entry : all().entrySet()) {
            String cat = entry.getValue().category();
            if (!cat.equals(lastCategory)) {
                sb.append("\n[").append(cat).append("]\n");
                lastCategory = cat;
            }
            sb.append(String.format("  run %-17s %s%n",
                entry.getKey(),
                entry.getValue().description()));
        }
        return sb.toString();
    }

    private static void add(
            Map<String, ExampleEntry> map,
            String key,
            String className,
            String category,
            String description) {

        map.put(key, new ExampleEntry(className, category, description));
    }
}
