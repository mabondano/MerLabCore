package com.merlab.signals.plot;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class PlotlyTestApp extends Application {
  @Override
  public void start(Stage stage) {
    WebView web = new WebView();
    web.getEngine().loadContent("""
      <html>
        <head>
    	   <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
         
        </head>
        <body>
          <div id="chart" style="width:600px;height:400px;"></div>
          <script>
            Plotly.newPlot('chart',
              [{ x: [1,2,3], y: [2,6,3], mode:'lines+markers' }]
            );
          </script>
        </body>
      </html>
    """);
    BorderPane root = new BorderPane(web);
    stage.setScene(new Scene(root, 620, 440));
    stage.setTitle("Plotly.js en JavaFX WebView");
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}

//  <script src="plotly.min.js"></script>
/*
--module-path "C:\javafx-sdk-17.0.15\lib" 
--add-modules=javafx.controls,javafx.web
-Dprism.order=sw
-Dprism.verbose=true
-Djava.library.path="C:\javafx-sdk-17.0.15\bin"
*/