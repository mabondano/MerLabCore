package com.merlab.signals.miniserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * MerLabCore MiniServer — lightweight HTTP server on port 8086.
 *
 * Endpoints:
 *   POST /command   → executes CLI commands, returns JSON { output, chart }
 *   GET  /health    → simple liveness check
 *
 * Supported commands:
 *   help            → shows available commands
 *   version         → server version
 *   list            → lists all registered examples
 *   run <key>       → runs an example by key (e.g. "run KMeans")
 *
 * Start:  java com.merlab.signals.miniserver.MerLabCoreMiniServer
 * Then open CLI_MerLabCore.html in your browser.
 */
public class MerLabCoreMiniServer {

    private static final int    PORT    = 8086;
    private static final String VERSION = "MerLabCore MiniServer v1.0";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/command", MerLabCoreMiniServer::handleCommand);
        server.createContext("/health",  MerLabCoreMiniServer::handleHealth);
        server.createContext("/examples", MerLabCoreMiniServer::handleExamples);
        server.start();

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  " + VERSION + "                  ║");
        System.out.println("║  Listening on http://localhost:" + PORT + "          ║");
        System.out.println("║  Open CLI_MerLabCore.html in your browser    ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println("Registered examples: " + ExampleRegistry.all().size());
    }

    // ── /command ────────────────────────────────────────────────────────────

    private static void handleCommand(HttpExchange exchange) throws IOException {
        addCors(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String body    = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String command = extractCommand(body);

        System.out.println("[MiniServer] command: " + command);

        String output;
        String chart = null;

        if (command == null || command.isBlank()) {
            output = "ERROR: Empty command. Type 'help' for available commands.";

        } else if (command.equalsIgnoreCase("help")) {
            output = """
Available commands:
  help              Show this help
  version           Show server version
  list              List all runnable examples
  run <key>         Run an example  (e.g.  run KMeans)
  run <key>         Examples: run MLPReg1, run KMeans, run LogRegRadial

Tip: click any example in the INDEX panel to run it automatically.
""";

        } else if (command.equalsIgnoreCase("version")) {
            output = VERSION;

        } else if (command.equalsIgnoreCase("list")) {
            output = ExampleRegistry.listFormatted();

        } else if (command.toLowerCase().startsWith("run ")) {
            String key = command.substring(4).trim();
            output = "[MiniServer] Running: " + key + " ...\n";

            long t0 = System.currentTimeMillis();
            ExampleRunner.RunResult result = ExampleRunner.run(key);
            long elapsed = System.currentTimeMillis() - t0;

            if (result.success()) {
                output += result.consoleOutput()
                       + "\n[OK] Finished in " + elapsed + " ms";
            } else {
                output += result.consoleOutput();
            }
            chart = result.chartHtml();

        } else {
            output = "Unknown command: '" + command + "'. Type 'help'.";
        }

        sendJson(exchange, output, chart);
    }

    // ── /health ─────────────────────────────────────────────────────────────

    private static void handleHealth(HttpExchange exchange) throws IOException {
        addCors(exchange);
        byte[] body = "{\"status\":\"ok\"}".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }

    private static void handleExamples(HttpExchange exchange) throws IOException {
        addCors(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        StringBuilder json = new StringBuilder();
        json.append("{ \"examples\": [");

        boolean first = true;
        for (var entry : ExampleRegistry.all().entrySet()) {
            if (!first) {
                json.append(", ");
            }
            first = false;

            ExampleRegistry.ExampleEntry example = entry.getValue();
            json.append("{ \"key\": ")
                .append(toJsonString(entry.getKey()))
                .append(", \"category\": ")
                .append(toJsonString(example.category()))
                .append(", \"description\": ")
                .append(toJsonString(example.description()))
                .append(" }");
        }

        json.append("] }");
        sendJsonResponse(exchange, json.toString());
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private static void sendJson(HttpExchange exchange, String output, String chart) throws IOException {
        String chartJson = (chart != null)
            ? toJsonString(chart)
            : "null";

        String response = "{ \"output\": " + toJsonString(output)
                        + ", \"chart\": " + chartJson + " }";

        sendJsonResponse(exchange, response);
    }

    private static void sendJsonResponse(HttpExchange exchange, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /** Extracts the "command" field from a simple JSON body. */
    private static String extractCommand(String body) {
        // Simple extraction — no external JSON library needed
        if (body == null) return null;
        int idx = body.indexOf("\"command\"");
        if (idx < 0) return null;
        int colon = body.indexOf(":", idx);
        if (colon < 0) return null;
        int q1 = body.indexOf("\"", colon + 1);
        if (q1 < 0) return null;
        int q2 = body.indexOf("\"", q1 + 1);
        if (q2 < 0) return null;
        return body.substring(q1 + 1, q2);
    }

    /** Escapes a Java string to a JSON string literal. */
    private static String toJsonString(String text) {
        if (text == null) return "null";
        return "\"" + text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            + "\"";
    }

    private static void addCors(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin",  "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }
}
