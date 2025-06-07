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

package com.merlab.signals.plot;

import com.merlab.signals.core.Signal;
import com.merlab.signals.nn.processor.MultiLayerPerceptronProcessor;
import org.knowm.xchart.*;
import org.knowm.xchart.internal.series.MarkerSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities to plot classified scatterplots (puntos + frontera).
 */
public class LogicGateScatterExample {

    /**
     * Dibuja:
     *  - Los puntos raw[i][0], raw[i][1] coloreados según raw[i][2] (0 o 1).
     *  - La frontera de decisión muestreada sobre una malla.
     */
    public static void plot(String title, double[][] raw, MultiLayerPerceptronProcessor mlp) {
        // Separar clases
        List<Double> x0 = new ArrayList<>(), y0 = new ArrayList<>();
        List<Double> x1 = new ArrayList<>(), y1 = new ArrayList<>();
        for (double[] r : raw) {
            if ((int) r[2] == 0) { x0.add(r[0]); y0.add(r[1]); }
            else               { x1.add(r[0]); y1.add(r[1]); }
        }

        // Crear chart
        XYChart chart = new XYChartBuilder()
            .width(800).height(600)
            .title(title)
            .xAxisTitle("x")
            .yAxisTitle("y")
            .build();

        // Clase 0
        MarkerSeries ms0 = chart.addSeries("Clase 0", x0, y0);
        ms0.setMarker(SeriesMarkers.CIRCLE);

        // Clase 1
        MarkerSeries ms1 = chart.addSeries("Clase 1", x1, y1);
        ms1.setMarker(SeriesMarkers.CROSS);

        // Frontera de decisión
        int grid = 100;
        double minX = min(raw, 0), maxX = max(raw, 0);
        double minY = min(raw, 1), maxY = max(raw, 1);
        double stepX = (maxX - minX) / grid;
        double stepY = (maxY - minY) / grid;

        List<Double> fx = new ArrayList<>(), fy = new ArrayList<>();
        for (int i = 0; i <= grid; i++) {
            for (int j = 0; j <= grid; j++) {
                double xp = minX + i * stepX;
                double yp = minY + j * stepY;
                Signal s = new Signal();
                s.add(xp); s.add(yp);
                var out = mlp.predict(s).getValues();
                // argmax sobre dos salidas
                if (out.get(0) > out.get(1)) {
                    fx.add(xp);
                    fy.add(yp);
                }
            }
        }
        if (!fx.isEmpty()) {
            MarkerSeries border = chart.addSeries("Frontera", fx, fy);
            border.setMarker(SeriesMarkers.NONE);
        }

        // Display
        new SwingWrapper<>(chart).displayChart();
    }

    private static double min(double[][] raw, int col) {
        double m = Double.POSITIVE_INFINITY;
        for (var r : raw) m = Math.min(m, r[col]);
        return m;
    }
    private static double max(double[][] raw, int col) {
        double m = Double.NEGATIVE_INFINITY;
        for (var r : raw) m = Math.max(m, r[col]);
        return m;
    }
}
