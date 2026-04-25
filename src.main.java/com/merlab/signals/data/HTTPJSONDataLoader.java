package com.merlab.signals.data;

import com.merlab.signals.core.Signal;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Descarga un JSON de una URL (array de objetos) y parsea según columnas.
 */
public class HTTPJSONDataLoader implements DataLoader {
    private final String url;
    private final List<String> inputCols;
    private final List<String> targetCols;

    public HTTPJSONDataLoader(String url,
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
            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        JSONArray array = new JSONArray(response.body());
        List<Signal> inputs  = new ArrayList<>();
        List<Signal> targets = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Signal in = new Signal(), tg = new Signal();
            for (String col : inputCols)  in.add(obj.getDouble(col));
            for (String col : targetCols) tg.add(obj.getDouble(col));
            inputs .add(in);
            targets.add(tg);
        }
        return new DataSet(inputs, targets);
    }
}
