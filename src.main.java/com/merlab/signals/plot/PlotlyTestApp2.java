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

