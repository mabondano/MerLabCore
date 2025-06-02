package com.merlab.signals.examples;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.ChartType;
import com.merlab.signals.core.SignalPlotter;
import org.knowm.xchart.style.Styler.ChartTheme;

/**
 * Ejemplo de cómo graficar 30 datos usando los tres tipos de gráfico.
 */
public class MultiStyleChartExample {

    public static void main(String[] args) {
        // 1) Datos manuales: aquí sólo un ejemplo incremental
        double[] rawData = new double[30];
        for (int i = 0; i < rawData.length; i++) {
            rawData[i] = Math.sin(i * 0.2) * 10 + 20;  // solo para variar
        }

        // 2) Construye tu Signal
        Signal signal = new Signal();
        for (double v : rawData) {
            signal.add(v);
        }

        // 3) Grafica en LINE
        SignalPlotter.plotSignal2(
            "Thirty Points — LINE",
            signal,
            ChartTheme.XChart,
            ChartType.LINE
        );

        // 4) Grafica en SCATTER
        SignalPlotter.plotSignal2(
            "Thirty Points — SCATTER",
            signal,
            ChartTheme.GGPlot2,
            ChartType.SCATTER
        );

        // 5) Grafica en BAR
        SignalPlotter.plotSignal2(
            "Thirty Points — BAR",
            signal,
            ChartTheme.Matlab,
            ChartType.BAR
        );
    }
}
