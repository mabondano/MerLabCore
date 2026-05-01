package com.merlab.signals.miniserver;

/**
 * Thread-local capture for Plotly HTML output.
 *
 * When capturing mode is active, PlotlyBrowserViewer.showInBrowser()
 * calls PlotlyCapture.capture(html) instead of opening the browser.
 *
 * Usage in MiniServer:
 *   PlotlyCapture.start();
 *   // run example main()
 *   String html = PlotlyCapture.get();
 *   PlotlyCapture.clear();
 */
public class PlotlyCapture {

    private static final ThreadLocal<Boolean>  capturing = new ThreadLocal<>();
    private static final ThreadLocal<String>   capturedHtml = new ThreadLocal<>();

    /** Activate capture mode for this thread. */
    public static void start() {
        capturing.set(Boolean.TRUE);
        capturedHtml.remove();
    }

    /** Returns true if capture mode is active on this thread. */
    public static boolean isCapturing() {
        return Boolean.TRUE.equals(capturing.get());
    }

    /**
     * Called by PlotlyBrowserViewer instead of opening the browser.
     * Only the first call per run is stored (subsequent plots are appended).
     */
    public static void capture(String html) {
        String existing = capturedHtml.get();
        if (existing == null) {
            capturedHtml.set(html);
        } else {
            // If example produces multiple charts, append them side by side
            capturedHtml.set(existing + "\n<hr style='border-color:#ffd500'>\n" + html);
        }
    }

    /** Returns the captured HTML, or null if nothing was captured. */
    public static String get() {
        return capturedHtml.get();
    }

    /** Deactivate capture mode and clear stored HTML. */
    public static void clear() {
        capturing.remove();
        capturedHtml.remove();
    }
}
