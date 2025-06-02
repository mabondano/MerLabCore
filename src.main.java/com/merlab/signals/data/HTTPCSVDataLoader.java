package com.merlab.signals.data;

import com.merlab.signals.core.Signal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Descarga un CSV de una URL y parsea inputCols/targetCols.
 */
public class HTTPCSVDataLoader implements DataLoader {
    private final String url;
    private final List<String> inputCols;
    private final List<String> targetCols;

    public HTTPCSVDataLoader(String url,
                             List<String> inputCols,
                             List<String> targetCols) {
        this.url        = url;
        this.inputCols  = inputCols;
        this.targetCols = targetCols;
    }

    @Override
    public DataSet load() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
        HttpResponse<String> response =
            client.send(request, HttpResponse.BodyHandlers.ofString());

        List<String> lines = List.of(response.body().split("\n"));
        // suponemos header en lines.get(0)
        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] tokens = line.split(",");
            Signal in = new Signal(), tg = new Signal();
            for (int j = 0; j < inputCols.size(); j++)
                in.add(Double.parseDouble(tokens[j]));
            for (int j = 0; j < targetCols.size(); j++)
                tg.add(Double.parseDouble(tokens[inputCols.size() + j]));
            inputs .add(in);
            targets.add(tg);
        }
        return new DataSet(inputs, targets);
    }
}

