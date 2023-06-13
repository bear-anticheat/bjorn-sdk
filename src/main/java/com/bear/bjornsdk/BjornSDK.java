package com.bear.bjornsdk;

import com.bear.bjornsdk.object.Configuration;
import com.bear.bjornsdk.object.Violation;
import com.bear.bjornsdk.response.impl.ConfigResponse;
import com.bear.bjornsdk.response.impl.ServerSearchResponse;
import com.bear.bjornsdk.response.impl.ViolationSubmitResponse;
import com.bear.bjornsdk.util.JsonArrayDeserializer;
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

@Data
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

        final String jsonResponse = sendRequestWithBody("POST", "/auth/a/login", "{\n   \"apiKey\": \"" + apiKey + "\"\n}");

        if (jsonResponse == null || !jsonResponse.contains("token\":")) {
            System.out.println("[bjorn-sdk] could not obtain session token!");
            _failed = true;

            return;
        }

        final JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();

        if (json.get("status").getAsString().equals("success") && json.has("data")) {
            final JsonObject data = json.get("data").getAsJsonObject();
            final String token = data.get("token").getAsString();

            System.out.println("[bjorn-sdk] got session token. authenticated!");

            _sessionToken = token;
            _ready = true;
        }
    }

    @SneakyThrows
    public boolean destroy() {
        if (!_ready || _failed) return false;

        System.out.println("[bjorn-sdk] destroying session...");

        final String _response = sendRequest("DELETE", "/auth/revoke");

        if (_response == null) {
            System.out.println("[bjorn-sdk] null response on session revokal");
            return false;
        }

        final JsonObject response = JsonParser.parseString(_response).getAsJsonObject();

        return response.get("status").getAsString().equals("success");
    }

    @SneakyThrows
    public ServerSearchResponse checkLicense(final String licenseKey) {
        final JsonObject json = new JsonObject();

        json.addProperty("license", licenseKey);

        final String _response = sendRequestWithBody("POST", "/servers/_/search", GSON.toJson(json));

        if (_response == null) {
            System.out.println("[bjorn-sdk] null response on server search");
            return null;
        }

        final JsonObject response = JsonParser.parseString(_response).getAsJsonObject();

        if (!response.has("data")) {
            System.out.println("[bjorn-sdk] no data on server search");

            return new ServerSearchResponse(false, false);
        }

        return new ServerSearchResponse(response.get("status").getAsString().equals("success"), response.get("data")
                .getAsJsonObject().get("result").getAsBoolean());
    }

    @SneakyThrows
    public ViolationSubmitResponse submitViolation(final Violation violation) {
        final String check = violation.getCheckParent() + ":" + violation.getCheckType();
        final String server = violation.getServerLicense() + ":" + violation.getServerName();

        final JsonObject json = new JsonObject();

        json.addProperty("check", check);
        json.addProperty("server", server);

        json.addProperty("level", violation.getVl());
        json.addProperty("debug", violation.getDebug());
        json.addProperty("uuid", violation.getUuid().toString());

        final String _response = sendRequestWithBody("PUT", "/logs/_/submit", GSON.toJson(json));

        if (_response == null) {
            System.out.println("[bjorn-sdk] null response on log submission");
            return null;
        }

        final JsonObject response = JsonParser.parseString(_response).getAsJsonObject();

        return new ViolationSubmitResponse(response.get("message").getAsString(),
                response.get("status").getAsString().equals("success"));
    }

    @SneakyThrows
    public ConfigResponse fetchConfig(final String licenseKey) {
        final String _response = sendRequest("GET", "/config/license/" + licenseKey);

        if (_response == null) {
            System.out.println("[bjorn-sdk] null response on config fetch");
            return null;
        }

        final JsonObject response = JsonParser.parseString(_response).getAsJsonObject();

        if (!response.has("data")) {
            return new ConfigResponse(false, null);
        }

        final JsonObject data = response.get("data").getAsJsonObject();

        final String alertFormat = data.get("alertFormat").getAsString();
        final String banCommand = data.get("banCommand").getAsString();

        final String[] banFormat = JsonArrayDeserializer.transformString(data.get("banFormat").getAsJsonArray());

        final boolean proxyAlerts = data.get("proxyAlerts").getAsBoolean();
        final boolean proxyBans = data.get("proxyBans").getAsBoolean();

        return new ConfigResponse(response.get("status").getAsString().equals("success"),
                new Configuration(alertFormat, banCommand, banFormat, proxyAlerts, proxyBans));
    }

    @SneakyThrows
    private String sendRequest(final String method, final String path) {
        if (_failed) return null;

        final URL url = new URL(hostname + path);
        final HttpURLConnection http = (HttpURLConnection) url.openConnection();

        http.setRequestMethod(method);
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
    private String sendRequestWithBody(final String method, final String path, final String body) {
        if (_failed) return null;

        final URL url = new URL(hostname + path);
        final HttpURLConnection http = (HttpURLConnection) url.openConnection();

        http.setDoOutput(true);

        http.setRequestMethod(method);
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
