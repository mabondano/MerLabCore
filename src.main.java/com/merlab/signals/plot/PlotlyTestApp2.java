package com.merlab.signals.plot;

import com.merlab.signals.plot.PlotlyBrowserViewer;

/**
 * Demo mínimo que usa Plotly.js (local) abriendo el HTML resultante en el navegador.
 * Asegúrate de copiar "plotly.min.js" en el mismo directorio donde se abra el HTML,
 * o usar un src absoluto/file:// si lo prefieres.
 */
public class PlotlyTestApp2 {

    public static void main(String[] args) {
        // 1) HTML que referencia plotly.min.js (debe estar junto al temporal HTML)
        String html = """
            <html>
              <head>
                <meta charset="utf-8"/>
                <script src="plotly.min.js"></script>
              </head>
              <body>
                <div id="chart" style="width:800px;height:600px;"></div>
                <script>
                  Plotly.newPlot('chart',
                    [{ x: [1,2,3], y: [2,6,3], mode:'lines+markers' }]
                  );
                </script>
              </body>
            </html>
            """;

        try {
            // 2) Abre el HTML generado en el navegador
            PlotlyBrowserViewer.showInBrowser2(html);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("No se pudo abrir la gráfica en el navegador.");
        }
    }
}

/*
--module-path "C:\javafx-sdk-17.0.15\lib" 
--add-modules=javafx.controls,javafx.web
-Dprism.order=sw
-Dprism.verbose=true
-Djava.library.path="C:\javafx-sdk-17.0.15\bin"
*/

