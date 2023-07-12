/*
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 *  See the NOTICE file(s) distributed with this work for additional
 *  information.
 *
 * This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.tapocamera.internal.api;

import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.openhab.binding.tapocamera.internal.api.response.ApiDeviceInfo;
import org.openhab.binding.tapocamera.internal.api.response.ApiResponse;
import org.openhab.binding.tapocamera.internal.dto.AlarmInfo;
import org.openhab.binding.tapocamera.internal.dto.IntrusionDetection;
import org.openhab.binding.tapocamera.internal.dto.LineCrossingDetection;
import org.openhab.binding.tapocamera.internal.dto.MotionDetection;
import org.openhab.binding.tapocamera.internal.dto.PeopleDetection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TapoCameraApiImpl} is describing implementation of api interface.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
@NonNullByDefault
public class TapoCameraApiImpl implements TapoCameraApi {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final HttpClient httpClient;
    protected final Gson gson = new Gson();
    public static final String TAPO_USER_AGENT = "Tapo CameraClient Android";

    private String token = "";
    private String hostname = "";

    private Integer userId = 0;

    public TapoCameraApiImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
        // this.httpClient = new HttpClient(new SslContextFactory.Client());
        // try {
        // this.httpClient.start();
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public ApiResponse sendPostRequest(String path, String body)
            throws ApiException, KeyManagementException, NoSuchAlgorithmException {
        return sendPostRequest(path, body, "result");
    }

    private ApiResponse sendPostRequest(String path, String body, String root)
            throws ApiException, KeyManagementException, NoSuchAlgorithmException {
        String errorReason = "";
        ApiResponse result = new ApiResponse();
        String url = "https://" + hostname;
        if (!path.isEmpty() && !path.equals("/")) {
            url += path;
        }

        Request request = httpClient.newRequest(url);
        setHeaders(request);
        httpClient.setConnectTimeout(60 * 1000);
        httpClient.setAddressResolutionTimeout(60 * 1000);

        request.method(HttpMethod.POST);
        request.content(new StringContentProvider(body));
        try {
            ContentResponse contentResponse = request.send();
            result.httpCode = contentResponse.getStatus();
            if (result.httpCode == 200 || result.httpCode >= 400 && result.httpCode < 500) {
                JsonObject json = JsonParser.parseString(contentResponse.getContentAsString()).getAsJsonObject();
                result.errorCode = json.get("error_code").getAsInt();
                if (json.has(root)) {
                    result.result = json.get(root).getAsJsonObject();
                } else {
                    result.result = json;
                }
                return result;
            } else {
                errorReason = String.format("TapoCamera request failed with %d: %s", contentResponse.getStatus(),
                        contentResponse.getReason());
            }
        } catch (InterruptedException e) {
            errorReason = String.format("InterruptedException: %s", e.getMessage());
        } catch (ExecutionException e) {
            errorReason = String.format("ExecutionException: %s", e.getMessage());
        } catch (TimeoutException e) {
            errorReason = "TimeoutException: TapoCamera Api was not reachable on your network";
        }
        throw new ApiException(errorReason);
    }

    private void setHeaders(Request request) {
        request.timeout(60, TimeUnit.SECONDS);
        request.header(HttpHeader.USER_AGENT, null);
        request.header(HttpHeader.USER_AGENT, TAPO_USER_AGENT);
        request.header(HttpHeader.CONTENT_TYPE, "application/json");
        request.header(HttpHeader.CONNECTION, "close");
        request.header(HttpHeader.ACCEPT, "application/json");
        request.header(HttpHeader.ACCEPT_ENCODING, null);
        request.header(HttpHeader.ACCEPT_ENCODING, "gzip, deflate");
        request.header("rejectUnauthorized", "false");
    }

    private String getPasswordHash(String password) {
        String hash = "";
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(password.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            hash = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hash.length() < 32) {
                hash = "0" + hash;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return hash.toUpperCase();
    }

    @Override
    public Boolean auth(String username, String password) throws ApiException {
        String passwordHash = getPasswordHash(password);
        Boolean result = Boolean.FALSE;
        JsonObject jsonParams = new JsonObject();
        jsonParams.addProperty("hashed", Boolean.TRUE);
        jsonParams.addProperty("password", passwordHash);
        jsonParams.addProperty("username", username);

        JsonObject jsonAuth = new JsonObject();
        jsonAuth.addProperty("method", "login");
        jsonAuth.add("params", jsonParams);

        try {
            ApiResponse response = sendPostRequest("/", jsonAuth.toString());
            if (response.httpCode == 200 && response.errorCode == 0) {
                if (response.result.has("stok")) {
                    token = response.result.get("stok").getAsString();
                    result = !token.isEmpty();
                    userId = getUserId();
                }
                return result;
            } else {
                throw processErrorResponse("auth", response);
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException("NoSuchAlgorithmException:{}", e);
        } catch (JsonSyntaxException e) {
            throw new ApiException("JsonSyntaxException:{}", e);
        } catch (KeyManagementException e) {
            throw new ApiException("KeyManagementException:{}", e);
        }
    }

    private ApiException processErrorResponse(String tag, ApiResponse response) {
        if (response.httpCode >= 400 && response.httpCode < 500 || response.errorCode < 0) {
            if (response.httpCode == 401 || response.errorCode == -40401) {
                resetSession();
            }
            logger.error(
                    String.format("TapoCamera API (%s) error: %d - %d", tag, response.httpCode, response.errorCode));
            return new ApiException(
                    String.format("TapoCamera API (%s) error: %d - %d", tag, response.httpCode, response.errorCode));
        } else {
            logger.error(String.format("TapoCamera API (%s) error: %d - %s", tag, response.httpCode, response.result));
            return new ApiException(
                    String.format("TapoCamera API (%s) error: %d - %s", tag, response.httpCode, response.result));
        }
    }

    private void resetSession() {
        token = "";
        // thing offline
    }

    @Override
    public ApiDeviceInfo getDeviceInfo() throws ApiException {
        String command = "{\"method\":\"get\",\"device_info\":{\"name\": [\"basic_info\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "device_info");
            if (response.httpCode == 200 && response.errorCode == 0) {
                if (response.result.has("basic_info")) {
                    return Objects.requireNonNull(gson.fromJson(response.result, ApiDeviceInfo.class));
                }
                return new ApiDeviceInfo();
            } else {
                throw processErrorResponse("devInfo", response);
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException("NoSuchAlgorithmException:{}", e);
        } catch (JsonSyntaxException e) {
            throw new ApiException("JsonSyntaxException:{}", e);
        } catch (KeyManagementException e) {
            throw new ApiException("KeyManagementException:{}", e);
        }
    }


    private Integer getUserId() throws ApiException {
        String command = "{\"method\":\"multipleRequest\",\"params\":{\"requests\": [{\"method\":\"getUserID\",\"params\":{\"system\":{\"get_user_id\":\"null\"}}}]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "result");
            if (response.httpCode == 200 && response.errorCode == 0) {
                if (response.result.has("responses")) {
                    userId = response.result.get("responses").getAsJsonArray().get(0).getAsJsonObject()
                            .getAsJsonObject("result").get("user_id").getAsInt();
                    return userId;
                }
                return -1;
            } else {
                throw processErrorResponse("devInfo", response);
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException("NoSuchAlgorithmException:{}", e);
        } catch (JsonSyntaxException e) {
            throw new ApiException("JsonSyntaxException:{}", e);
        } catch (KeyManagementException e) {
            throw new ApiException("KeyManagementException:{}", e);
        }
    }

    @Override
    public String getLedStatus() throws ApiException {
        String command = "{\"method\":\"get\",\"led\":{\"name\": [\"config\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "led");
            if (response.httpCode == 200 && response.errorCode == 0) {
                if (response.result.has("config")) {
                    String status = response.result.get("config").getAsJsonObject().get("enabled").getAsString();
                    logger.debug("led status is {}", status);
                    return status;
                }
                return "off";
            } else {
                throw processErrorResponse("led", response);
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (JsonSyntaxException e) {
            throw new ApiException("JsonSyntaxException:{}", e);
        } catch (KeyManagementException e) {
            throw new ApiException("KeyManagementException:{}", e);
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException("NoSuchAlgorithmException:{}", e);
        }
    }

    @Override
    public AlarmInfo getAlarmInfo() throws ApiException {
        String command = "{\"method\":\"get\",\"msg_alarm\":{\"name\": [\"chn1_msg_alarm_info\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "msg_alarm");
            if (response.httpCode == 200 && response.errorCode == 0) {
                if (response.result.has("chn1_msg_alarm_info")) {
                    AlarmInfo alarmInfo = gson.fromJson(response.result.get("chn1_msg_alarm_info"), AlarmInfo.class);
                    logger.debug("alarm info is {}", alarmInfo.toString());
                    return alarmInfo;
                }
                return new AlarmInfo();
            } else {
                throw processErrorResponse("alarm", response);
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (JsonSyntaxException e) {
            throw new ApiException("JsonSyntaxException:{}", e);
        } catch (KeyManagementException e) {
            throw new ApiException("KeyManagementException:{}", e);
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException("NoSuchAlgorithmException:{}", e);
        }
    }

    @Override
    public MotionDetection getMotionDetection() throws ApiException {
        String command = "{\"method\":\"get\",\"motion_detection\":{\"name\": [\"motion_det\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "motion_detection");
            if (response.httpCode == 200 && response.errorCode == 0) {
                if (response.result.has("motion_det")) {
                    MotionDetection motionDetection = gson.fromJson(response.result.get("motion_det"),
                            MotionDetection.class);
                    logger.debug("motion detection is {}", motionDetection.toString());
                    return motionDetection;
                }
                return new MotionDetection();
            } else {
                throw processErrorResponse("motion", response);
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (JsonSyntaxException e) {
            throw new ApiException("JsonSyntaxException:{}", e);
        } catch (KeyManagementException e) {
            throw new ApiException("KeyManagementException:{}", e);
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException("NoSuchAlgorithmException:{}", e);
        }
    }

    @Override
    public PeopleDetection getPeopleDetection() throws ApiException {
        String command = "{\"method\":\"get\",\"people_detection\":{\"name\": [\"detection\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "people_detection");
            if (response.httpCode == 200 && response.errorCode == 0) {
                if (response.result.has("detection")) {
                    PeopleDetection peopleDetection = gson.fromJson(response.result.get("detection"),
                            PeopleDetection.class);
                    logger.debug("people detection is {}", peopleDetection.toString());
                    return peopleDetection;
                }
                return new PeopleDetection();
            } else {
                throw processErrorResponse("people", response);
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (JsonSyntaxException e) {
            throw new ApiException("JsonSyntaxException:{}", e);
        } catch (KeyManagementException e) {
            throw new ApiException("KeyManagementException:{}", e);
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException("NoSuchAlgorithmException:{}", e);
        }
    }

    @Override
    public LineCrossingDetection getLineCrossingDetection() throws ApiException {
        String command = "{\"method\":\"get\",\"linecrossing_detection\":{\"name\": [\"detection\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "linecrossing_detection");
            if (response.httpCode == 200 && response.errorCode == 0) {
                if (response.result.has("detection")) {
                    LineCrossingDetection detection = gson.fromJson(response.result.get("detection"),
                            LineCrossingDetection.class);
                    logger.debug("line crossing detection is {}", detection.toString());
                    return detection;
                } else {
                    return new LineCrossingDetection();
                }
            } else {
                throw processErrorResponse("line", response);
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (JsonSyntaxException e) {
            throw new ApiException("JsonSyntaxException:{}", e);
        } catch (KeyManagementException e) {
            throw new ApiException("KeyManagementException:{}", e);
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException("NoSuchAlgorithmException:{}", e);
        }
    }

    @Override
    public IntrusionDetection getIntrusionDetection() throws ApiException {
        String command = "{\"method\":\"get\",\"intrusion_detection\":{\"name\": [\"detection\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "intrusion_detection");
            if (response.httpCode == 200 && response.errorCode == 0) {
                if (response.result.has("detection")) {
                    IntrusionDetection detection = gson.fromJson(response.result.get("detection"),
                            IntrusionDetection.class);
                    logger.debug("Intrusion detection is {}", detection.toString());
                    return detection;
                } else {
                    return new IntrusionDetection();
                }
            } else {
                throw processErrorResponse("line", response);
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (JsonSyntaxException e) {
            throw new ApiException("JsonSyntaxException:{}", e);
        } catch (KeyManagementException e) {
            throw new ApiException("KeyManagementException:{}", e);
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException("NoSuchAlgorithmException:{}", e);
        }
    }

    private void sendCommand(String command, String tag) throws ApiException {
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "result");
            if (response.httpCode != 200 || response.errorCode != 0) {
                throw processErrorResponse(tag, response);
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (JsonSyntaxException e) {
            throw new ApiException("JsonSyntaxException:{}", e);
        } catch (KeyManagementException e) {
            throw new ApiException("KeyManagementException:{}", e);
        } catch (NoSuchAlgorithmException e) {
            throw new ApiException("NoSuchAlgorithmException:{}", e);
        }
    }

    @Override
    public void setLedStatus(String status) throws ApiException {
        String command = String.format("{\"method\": \"set\",\"led\":{\"config\":{\"enabled\":\"%s\"}}}", status);
        sendCommand(command, "led");
    }

    @Override
    public void setAlarmInfoEnabled(String status) throws ApiException {
        String command = String
                .format("{\"method\": \"set\",\"msg_alarm\":{\"chn1_msg_alarm_info\":{\"enabled\":\"%s\"}}}", status);
        sendCommand(command, "alarm");
    }

    @Override
    public void setAlarmInfoMode(List<String> modes) throws ApiException {
        String command = String.format(
                "{\"method\": \"set\",\"msg_alarm\":{\"chn1_msg_alarm_info\":{\"alarm_mode\":[\"%s\"]}}}",
                modes.toString());
        sendCommand(command, "alarm");
    }

    @Override
    public void setAlarmInfoType(String type) throws ApiException {
        String command = String
                .format("{\"method\": \"set\",\"msg_alarm\":{\"chn1_msg_alarm_info\":{\"alarm_type\":\"%s\"}}}", type);
        sendCommand(command, "alarm");
    }

    @Override
    public void setMotionDetectionEnabled(String state) throws ApiException {
        String command = String
                .format("{\"method\": \"set\",\"motion_detection\":{\"motion_det\":{\"enable\":\"%s\"}}}", state);
        sendCommand(command, "motion");
    }

    @Override
    public void setMotionDetectionSensitivity(String state) throws ApiException {
        String command = String.format(
                "{\"method\": \"set\",\"motion_detection\":{\"motion_det\":{\"digital_sensitivity\":\"%s\"}}}", state);
        sendCommand(command, "motion");
    }

    @Override
    public void setPeopleDetectionEnabled(String state) throws ApiException {
        String command = String.format("{\"method\": \"set\",\"people_detection\":{\"detection\":{\"enable\":\"%s\"}}}",
                state);
        sendCommand(command, "people");
    }

    @Override
    public void setPeopleDetectionSensitivity(String state) throws ApiException {
        String command = String
                .format("{\"method\": \"set\",\"people_detection\":{\"detection\":{\"sensitivity\":\"%s\"}}}", state);
        sendCommand(command, "people");
    }

    @Override
    public void setManualAlarm(String state) throws ApiException {
        String command = String.format("{\"method\": \"do\",\"msg_alarm\":{\"manual_msg_alarm\":{\"action\":\"%s\"}}}",
                state);
        sendCommand(command, "manual");
    }

    @Override
    public void setLineCrossingDetectionEnabled(String state) throws ApiException {
        String command = String.format("{\"method\": \"set\",\"linecrossing_detection\":{\"detection\":{\"enable\":\"%s\"}}}",
                state);
        sendCommand(command, "lineCrossing");
    }

    @Override
    public void setIntrusionDetectionEnabled(String state) throws ApiException {
        String command = String.format("{\"method\": \"set\",\"intrusion_detection\":{\"detection\":{\"enable\":\"%s\"}}}",
                state);
        sendCommand(command, "intrusion");
    }
}
