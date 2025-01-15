/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.tapocamera.internal.api;

import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.TAPO_CLOUD_URL;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.binding.tapocamera.internal.api.response.ApiDeviceResponse;
import org.openhab.binding.tapocamera.internal.api.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * The Tapo camera cloud api implementation.
 *
 * @author "Ingrid JARDILLIER (ijardillier)"
 */
public class TapoCameraCloudApiImpl implements TapoCameraCloudApi {

    private final Logger logger = LoggerFactory.getLogger(TapoCameraCloudApiImpl.class);

    private static Gson gson = new Gson();
    private final HttpClient httpClient;

    /**
     * Initializes a new Tapo camera cloud api.
     *
     * @param httpClient the http client
     */
    public TapoCameraCloudApiImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String getCloudToken(String username, String password) {
        Request request = httpClient.newRequest(TAPO_CLOUD_URL).method(HttpMethod.POST).accept("application/json");

        JsonObject body = createMethod("login", createLoginParams(username, password));
        request.content(new StringContentProvider("application/json", body.toString(), StandardCharsets.UTF_8));

        try {
            JsonElement responseContent = JsonParser.parseString(request.send().getContentAsString());
            ApiResponse response = gson.fromJson(responseContent, ApiResponse.class);
            if (response.errorCode == 0 && response.result.has("token")) {
                return response.result.get("token").getAsString();
            } else {
                logger.error("Cloud: Error in response or Invalid token, error code: {}", response.errorCode);
            }
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            logger.error("{}}: {}", e.getClass().getSimpleName(), e.getMessage());
        }

        return null;
    }

    @Override
    public List<ApiDeviceResponse> getCloudDevices(String token) throws ApiException {
        Request request = httpClient.newRequest(TAPO_CLOUD_URL).method(HttpMethod.POST).accept("application/json");

        JsonObject body = createMethod("getDeviceList", createTokenParams(token));
        request.content(new StringContentProvider("application/json", body.toString(), StandardCharsets.UTF_8));

        try {
            JsonElement responseContent = JsonParser.parseString(request.send().getContentAsString());
            ApiResponse response = gson.fromJson(responseContent, ApiResponse.class);
            if (response.errorCode == 0) {
                Type listType = new TypeToken<ArrayList<ApiDeviceResponse>>() {
                }.getType();
                return gson.fromJson(response.result.getAsJsonArray("deviceList"), listType);
            } else {
                logger.error("Cloud: Error in response or Invalid token, error code: {}", response.errorCode);
            }
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            logger.error("{}}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new ApiException(String.format("%s %s", e.getClass().getSimpleName(), e.getMessage()));
        }

        return new ArrayList<>();
    }

    private JsonObject createLoginParams(String username, String password) {
        JsonObject json = new JsonObject();
        json.addProperty("appType", "Tapo_Android");
        json.addProperty("cloudUserName", username);
        json.addProperty("cloudPassword", password);
        json.addProperty("terminalUUID", UUID.randomUUID().toString());
        return json;
    }

    private JsonObject createTokenParams(String token) {
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        return json;
    }

    private JsonObject createMethod(String method, JsonObject params) {
        JsonObject json = new JsonObject();
        json.addProperty("method", method);

        if (params != null) {
            json.add("params", params);
        }

        return json;
    }
}
