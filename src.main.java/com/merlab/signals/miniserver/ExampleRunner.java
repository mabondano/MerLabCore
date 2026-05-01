package com.merlab.signals.miniserver;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

/**
 * Executes the main() method of a registered example via reflection.
 *
 * Captures:
 *   1. stdout (System.out) → returned as console log text
 *   2. Plotly HTML → via PlotlyCapture thread-local
 *
 * The original System.out is always restored, even on error.
 */
public class ExampleRunner {

    public record RunResult(String consoleOutput, String chartHtml, boolean success) {}

    /**
     * Runs the example identified by the given command key.
     *
     * @param commandKey  key from ExampleRegistry (e.g. "KMeans", "MLPReg1")
     * @return RunResult with console text, optional chart HTML, and success flag
     */
    public static RunResult run(String commandKey) {
        ExampleRegistry.ExampleEntry entry = ExampleRegistry.all().get(commandKey);
        if (entry == null) {
            return new RunResult(
                "ERROR: Unknown example '" + commandKey + "'. Type 'list' to see all examples.",
                null, false);
        }

        // 1) Redirect System.out
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream captureStream = new PrintStream(baos);
        System.setOut(captureStream);

        // 2) Activate Plotly capture
        PlotlyCapture.start();

        try {
            Class<?> clazz = Class.forName(entry.className());
            Method main = clazz.getMethod("main", String[].class);
            main.invoke(null, (Object) new String[]{});

            captureStream.flush();
            String consoleText = baos.toString();
            String chartHtml   = fixPlotlyRef(PlotlyCapture.get());

            return new RunResult(
                consoleText.isBlank() ? "(no console output)" : consoleText,
                chartHtml,
                true
            );

        } catch (ClassNotFoundException e) {
            return new RunResult(
                "ERROR: Class not found: " + entry.className() +
                "\nMake sure the example is compiled and on the classpath.",
                null, false);

        } catch (NoSuchMethodException e) {
            return new RunResult(
                "ERROR: No main() method found in " + entry.className(),
                null, false);

        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            return new RunResult(
                "ERROR running " + commandKey + ": " + cause.getMessage(),
                null, false);

        } finally {
            // Always restore stdout and clear capture
            System.setOut(originalOut);
            PlotlyCapture.clear();
        }
    }

    /** Replaces local plotly.min.js reference with CDN so srcdoc iframes can load it. */
    private static String fixPlotlyRef(String html) {
        if (html == null) return null;
        return html.replace(
            "src=\"plotly.min.js\"",
            "src=\"https://cdn.plot.ly/plotly-2.35.2.min.js\""
        ).replace(
            "src='plotly.min.js'",
            "src='https://cdn.plot.ly/plotly-2.35.2.min.js'"
        );
    }
}
