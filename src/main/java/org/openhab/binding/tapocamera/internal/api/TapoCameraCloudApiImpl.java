/*
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 *  SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.tapocamera.internal.api;

import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.TAPO_CLOUD_URL;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.openhab.binding.tapocamera.internal.api.response.ApiDeviceResponse;
import org.openhab.binding.tapocamera.internal.api.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapoCameraCloudApiImpl implements TapoCameraCloudApi {

    private final Logger logger = LoggerFactory.getLogger(TapoCameraCloudApiImpl.class);
    private String token = "";
    private static Gson gson = new Gson();
    private final HttpClient httpClient;

    public TapoCameraCloudApiImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    @Override
    public String getCloudToken(String username, String password) {
        Request request = httpClient.newRequest(TAPO_CLOUD_URL);
        setHeaders(request);
        request.method(HttpMethod.POST);

        JsonObject jsonParams = new JsonObject();
        jsonParams.addProperty("appType", "Tapo_Ios");
        jsonParams.addProperty("cloudUserName", username);
        jsonParams.addProperty("cloudPassword", password);
        jsonParams.addProperty("terminalUUID", UUID.randomUUID().toString());

        JsonObject jsonAuth = new JsonObject();
        jsonAuth.addProperty("method", "login");
        jsonAuth.add("params", jsonParams);

        request.content(new StringContentProvider(jsonAuth.toString(), StandardCharsets.UTF_8), "application/json");
        try {
            ContentResponse contentResponse = request.send();
            JsonElement json = JsonParser.parseString(contentResponse.getContentAsString());
            ApiResponse response = gson.fromJson(json, ApiResponse.class);
            if (response.errorCode == 0 && response.result.has("token")) {
                token = response.result.get("token").getAsString();
                return token;
            } else {
                // TODO: log.error && throw new Exception
                logger.error("Cloud: Error in response or Invalid token, error code: {}", response.errorCode);
            }
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            logger.error("{}}: {}", e.getClass().getSimpleName(), e.getMessage());
        }
        return "";
    }

    @Override
    public List<ApiDeviceResponse> getCloudDevices(String token) throws ApiException {
        String url = TAPO_CLOUD_URL + "?token=" + token;
        Request request = httpClient.newRequest(url);
        setHeaders(request);
        request.method(HttpMethod.POST);
        String body = "{\"method\": \"getDeviceList\"}";
        request.content(new StringContentProvider(body, StandardCharsets.UTF_8), "application/json");
        try {
            ContentResponse contentResponse = request.send();
            JsonElement json = JsonParser.parseString(contentResponse.getContentAsString());
            ApiResponse response = gson.fromJson(json, ApiResponse.class);
            if (response.errorCode == 0) {
                Type listType = new TypeToken<ArrayList<ApiDeviceResponse>>() {}.getType();
                List<ApiDeviceResponse> deviceResponseList = gson.fromJson(response.result
                        .getAsJsonArray("deviceList"), listType);
                logger.debug("Cloud devices: {}", deviceResponseList.toString());
                return deviceResponseList;
            } else {
                // TODO: log.error && throw new Exception
                logger.error("Cloud: Error in response or Invalid token, error code: {}", response.errorCode);
            }
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            logger.error("{}}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new ApiException(String.format("%s %s", e.getClass().getSimpleName(), e.getMessage()));
        }
        return new ArrayList<>();
    }

    private void setHeaders(Request request) {
        request.timeout(60, TimeUnit.SECONDS);
        request.header(HttpHeader.CONTENT_TYPE, "application/json");
        request.header(HttpHeader.ACCEPT, "application/json");
    }

}
