package com.merlab.signals.plot;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Abre una página HTML temporal en el navegador por defecto.
 */
public class PlotlyBrowserViewer {

    /**
     * Crea un archivo HTML temporal con el contenido dado y lo abre en el navegador.
     *
     * @param htmlContent contenido completo de la página (incluyendo <html>…</html>).
     * @throws IOException si hay error al escribir o abrir el archivo.
     */
	//Copiar también plotly.min.js al directorio temporal
	public static void showInBrowser(String htmlContent) throws IOException {
	    // 1) Crear directorio temporal
	    Path dir = Files.createTempDirectory("plotly_tmp");
	    // 2) Copiar el JS desde resources a ese dir
	    try (InputStream in = 
	           PlotlyBrowserViewer.class.getResourceAsStream("/web/plotly.min.js")) {
	        if (in == null) throw new IOException("plotly.min.js no encontrado en recursos");
	        Files.copy(in, dir.resolve("plotly.min.js"));
	    }
	    // 3) Escribir el HTML en el mismo dir
	    Path html = dir.resolve("index.html");
	    Files.writeString(html, htmlContent);
	    // 4) Abrir index.html
	    Desktop.getDesktop().browse(html.toUri());
	}
	
	//Incrustar plotly.min.js inline en el HTML
	public static void showInBrowser2(String htmlTemplate) throws IOException {
	    // 1) Leer plotly.min.js de resources
	    String plotlyJs;
	    try (InputStream in = 
	           PlotlyBrowserViewer.class.getResourceAsStream("/web/plotly.min.js")) {
	        plotlyJs = new String(in.readAllBytes(), StandardCharsets.UTF_8);
	    }
	    // 2) Construir HTML embebiendo la librería
	    String html = """
	      <html>
	       <head><meta charset="utf-8"><script>""" 
	      + plotlyJs + """
	       </script></head>
	       <body>""" 
	      + htmlTemplate + """
	       </body>
	      </html>
	      """;
	    // 3) Crear y abrir el .html
	    Path temp = Files.createTempFile("plotly_inline_", ".html");
	    Files.writeString(temp, html);
	    Desktop.getDesktop().browse(temp.toUri());
	}


}
