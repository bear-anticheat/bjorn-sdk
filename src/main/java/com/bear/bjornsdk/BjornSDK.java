package com.bear.bjornsdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class BjornSDK {

    private final String hostname;
    private final String apiKey;

    private static final Gson GSON = new GsonBuilder().create();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean _ready = false;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean _failed = false;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String _sessionToken = "";

    /**
     * Authenticates the SDK with the Bjorn server and
     * creates a session.
     */
    @SneakyThrows
    public void init() {
        System.out.println("[bjorn-sdk] attempting to obtain session token...");

        final String jsonResponse = sendRequestWithBody("/auth/a/login", "{\n   \"apiKey\": \"" + apiKey + "\"\n}");

        if (jsonResponse == null || !jsonResponse.contains("token\":")) {
            System.out.println("[bjorn-sdk] could not obtain session token!");
            _failed = true;

            return;
        }

        final JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();

        if (json.get("success").getAsBoolean() && json.has("data")) {
            final JsonObject data = json.get("data").getAsJsonObject();
            final String token = data.get("token").getAsString();

            System.out.println("[bjorn-sdk] got session token. authenticated!");

            _sessionToken = token;
            _ready = true;
        }
    }

    @SneakyThrows
    private String sendRequest(final String path) {
        if (_failed) return null;

        final URL url = new URL(hostname + path);
        final HttpURLConnection http = (HttpURLConnection) url.openConnection();

        http.setRequestMethod("POST");
        http.setRequestProperty("User-Agent", "Bjorn Java SDK");

        if (_ready) {
            http.setRequestProperty("Authorization", "Bearer " + _sessionToken);
        }

        final StringBuilder response = new StringBuilder();

        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()))) {
            while (bufferedReader.ready()) response.append(bufferedReader.readLine());
        }

        return response.toString();
    }

    @SneakyThrows
    private String sendRequestWithBody(final String path, final String body) {
        if (_failed) return null;

        final URL url = new URL(hostname + path);
        final HttpURLConnection http = (HttpURLConnection) url.openConnection();

        http.setDoOutput(true);

        http.setRequestMethod("POST");
        http.setRequestProperty("User-Agent", "Bjorn Java SDK");
        http.setRequestProperty("Content-Type", "application/json");

        if (_ready) {
            http.setRequestProperty("Authorization", "Bearer " + _sessionToken);
        }

        http.setRequestProperty("Content-Length", Integer.toString(body.length()));
        http.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));

        final StringBuilder response = new StringBuilder();

        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()))) {
            while (bufferedReader.ready()) response.append(bufferedReader.readLine());
        }

        return response.toString();
    }
}
