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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
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
import org.openhab.binding.tapocamera.internal.TapoCameraHandler;
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

    @Nullable
    protected TapoCameraHandler device;
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

    @Nullable
    public TapoCameraHandler getDevice() {
        return device;
    }

    @Override
    public void setDevice(TapoCameraHandler device) {
        this.device = device;
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
    public ApiResponse sendPostRequest(String path, String body) throws ApiException {
        return sendPostRequest(path, body, "result");
    }

    private ApiResponse sendPostRequest(String path, String body, String root) throws ApiException {
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
            resetSession();
            errorReason = "TimeoutException: TapoCamera Api was not reachable on your network";
        } catch (JsonSyntaxException e) {
            throw new ApiException("JsonSyntaxException:{}", e);
        }
        device.setDeviceStatus(errorReason);
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
        } catch (JsonSyntaxException e) {
            throw new ApiException("JsonSyntaxException:{}", e);
        }
    }

    private ApiException processErrorResponse(String tag, ApiResponse response) {
        if (response.httpCode >= 400 && response.httpCode < 500 || response.errorCode < 0) {
            if (response.httpCode == 401 || response.errorCode == -40401) {
                resetSession();
                device.setDeviceStatus(response.errorCode);
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
    }

    @Override
    public ApiDeviceInfo getDeviceInfo() {
        ApiDeviceInfo apiDeviceInfo = new ApiDeviceInfo();
        String command = "{\"method\":\"get\",\"device_info\":{\"name\": [\"basic_info\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "device_info");
            if (response.errorCode == 0) {
                if (response.result.has("basic_info")) {
                    apiDeviceInfo = Objects.requireNonNull(gson.fromJson(response.result, ApiDeviceInfo.class));
                }
            } else {
                throw processErrorResponse("devInfo", response);
            }
        } catch (JsonSyntaxException e) {
            logger.error(e.getMessage());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return apiDeviceInfo;
    }

    private Integer getUserId() {
        Integer id = -1;
        String command = "{\"method\":\"multipleRequest\",\"params\":{\"requests\": [{\"method\":\"getUserID\",\"params\":{\"system\":{\"get_user_id\":\"null\"}}}]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "result");
            if (response.errorCode == 0) {
                if (response.result.has("responses")) {
                    id = response.result.get("responses").getAsJsonArray().get(0).getAsJsonObject()
                            .getAsJsonObject("result").get("user_id").getAsInt();
                }
            } else {
                throw processErrorResponse("devInfo", response);
            }
        } catch (JsonSyntaxException e) {
            logger.error(e.getMessage());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public String getLedStatus() {
        String status = "off";
        String command = "{\"method\":\"get\",\"led\":{\"name\": [\"config\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "led");
            if (response.errorCode == 0) {
                if (response.result.has("config")) {
                    status = response.result.get("config").getAsJsonObject().get("enabled").getAsString();
                    logger.debug("led status is {}", status);
                }
            } else {
                throw processErrorResponse("led", response);
            }
        } catch (JsonSyntaxException e) {
            logger.error(e.getMessage());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    @Override
    public @Nullable AlarmInfo getAlarmInfo() {
        AlarmInfo alarmInfo = new AlarmInfo();
        String command = "{\"method\":\"get\",\"msg_alarm\":{\"name\": [\"chn1_msg_alarm_info\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "msg_alarm");
            if (response.errorCode == 0) {
                if (response.result.has("chn1_msg_alarm_info")) {
                    alarmInfo = gson.fromJson(response.result.get("chn1_msg_alarm_info"), AlarmInfo.class);
                    logger.debug("alarm info is {}", alarmInfo.toString());
                }
            } else {
                throw processErrorResponse("alarm", response);
            }
        } catch (JsonSyntaxException e) {
            logger.error(e.getMessage());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return alarmInfo;
    }

    @Override
    public @Nullable MotionDetection getMotionDetection() {
        MotionDetection motionDetection = new MotionDetection();
        String command = "{\"method\":\"get\",\"motion_detection\":{\"name\": [\"motion_det\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "motion_detection");
            if (response.httpCode == 200 && response.errorCode == 0) {
                if (response.result.has("motion_det")) {
                    motionDetection = gson.fromJson(response.result.get("motion_det"), MotionDetection.class);
                    logger.debug("motion detection is {}", motionDetection.toString());
                }
                return new MotionDetection();
            } else {
                throw processErrorResponse("motion", response);
            }
        } catch (JsonSyntaxException e) {
            logger.error(e.getMessage());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return motionDetection;
    }

    @Override
    public @Nullable PeopleDetection getPeopleDetection() {
        PeopleDetection peopleDetection = new PeopleDetection();
        String command = "{\"method\":\"get\",\"people_detection\":{\"name\": [\"detection\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "people_detection");
            if (response.errorCode == 0) {
                if (response.result.has("detection")) {
                    peopleDetection = gson.fromJson(response.result.get("detection"), PeopleDetection.class);
                    logger.debug("people detection is {}", peopleDetection.toString());
                }
            } else {
                throw processErrorResponse("people", response);
            }
        } catch (JsonSyntaxException e) {
            logger.error(e.getMessage());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return peopleDetection;
    }

    @Override
    public @Nullable LineCrossingDetection getLineCrossingDetection() {
        LineCrossingDetection detection = new LineCrossingDetection();
        String command = "{\"method\":\"get\",\"linecrossing_detection\":{\"name\": [\"detection\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "linecrossing_detection");
            if (response.errorCode == 0) {
                if (response.result.has("detection")) {
                    detection = gson.fromJson(response.result.get("detection"), LineCrossingDetection.class);
                    logger.debug("line crossing detection is {}", detection.toString());
                } else {
                    return new LineCrossingDetection();
                }
            } else {
                throw processErrorResponse("line", response);
            }
        } catch (JsonSyntaxException e) {
            logger.error(e.getMessage());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return detection;
    }

    @Override
    public @Nullable IntrusionDetection getIntrusionDetection() {
        IntrusionDetection detection = new IntrusionDetection();
        String command = "{\"method\":\"get\",\"intrusion_detection\":{\"name\": [\"detection\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "intrusion_detection");
            if (response.result.has("detection")) {
                detection = gson.fromJson(response.result.get("detection"), IntrusionDetection.class);
                logger.debug("Intrusion detection is {}", detection.toString());
            }
        } catch (JsonSyntaxException e) {
            logger.error(e.getMessage());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return detection;
    }

    private void sendCommand(String command, String tag) {
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "result");
            if (response.httpCode != 200 || response.errorCode != 0) {
                throw processErrorResponse(tag, response);
            }
        } catch (ApiException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void setLedStatus(String status) {
        String command = String.format("{\"method\": \"set\",\"led\":{\"config\":{\"enabled\":\"%s\"}}}", status);
        sendCommand(command, "led");
    }

    @Override
    public void setAlarmInfoEnabled(String status) {
        String command = String
                .format("{\"method\": \"set\",\"msg_alarm\":{\"chn1_msg_alarm_info\":{\"enabled\":\"%s\"}}}", status);
        sendCommand(command, "alarm");
    }

    @Override
    public void setAlarmInfoMode(List<String> modes) {
        String command = String.format(
                "{\"method\": \"set\",\"msg_alarm\":{\"chn1_msg_alarm_info\":{\"alarm_mode\":[\"%s\"]}}}",
                modes.toString());
        sendCommand(command, "alarm");
    }

    @Override
    public void setAlarmInfoType(String type) {
        String command = String
                .format("{\"method\": \"set\",\"msg_alarm\":{\"chn1_msg_alarm_info\":{\"alarm_type\":\"%s\"}}}", type);
        sendCommand(command, "alarm");
    }

    @Override
    public void setMotionDetectionEnabled(String state) {
        String command = String
                .format("{\"method\": \"set\",\"motion_detection\":{\"motion_det\":{\"enable\":\"%s\"}}}", state);
        sendCommand(command, "motion");
    }

    @Override
    public void setMotionDetectionSensitivity(String state) {
        String command = String.format(
                "{\"method\": \"set\",\"motion_detection\":{\"motion_det\":{\"digital_sensitivity\":\"%s\"}}}", state);
        sendCommand(command, "motion");
    }

    @Override
    public void setPeopleDetectionEnabled(String state) {
        String command = String.format("{\"method\": \"set\",\"people_detection\":{\"detection\":{\"enable\":\"%s\"}}}",
                state);
        sendCommand(command, "people");
    }

    @Override
    public void setPeopleDetectionSensitivity(String state) {
        String command = String
                .format("{\"method\": \"set\",\"people_detection\":{\"detection\":{\"sensitivity\":\"%s\"}}}", state);
        sendCommand(command, "people");
    }

    @Override
    public void setManualAlarm(String state) {
        String command = String.format("{\"method\": \"do\",\"msg_alarm\":{\"manual_msg_alarm\":{\"action\":\"%s\"}}}",
                state);
        sendCommand(command, "manual");
    }

    @Override
    public void setLineCrossingDetectionEnabled(String state) {
        String command = String
                .format("{\"method\": \"set\",\"linecrossing_detection\":{\"detection\":{\"enable\":\"%s\"}}}", state);
        sendCommand(command, "lineCrossing");
    }

    @Override
    public void setIntrusionDetectionEnabled(String state) {
        String command = String
                .format("{\"method\": \"set\",\"intrusion_detection\":{\"detection\":{\"enable\":\"%s\"}}}", state);
        sendCommand(command, "intrusion");
    }

    @Override
    public int getSpeakerVolume() {
        int volume = 0;
        String command = "{\"method\":\"get\",\"audio_config\":{\"name\": [\"speaker\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "audio_config");
            if (response.errorCode == 0) {
                if (response.result.has("speaker")) {
                    volume = response.result.get("speaker").getAsJsonObject().get("volume").getAsInt();
                    logger.debug("speaker volume is {}", volume);
                }
            } else {
                throw processErrorResponse("speaker", response);
            }
        } catch (JsonSyntaxException e) {
            logger.error(e.getMessage());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return volume;
    }

    @Override
    public void setSpeakerVolume(int volume) {
        String command = String
                .format("{\"method\": \"set\",\"audio_config\":{\"speaker\":{\"volume\":\"%d\"}}}", volume);
        sendCommand(command, "audio_config");
    }

    @Override
    public int getMicrophoneVolume() {
        int volume = 0;
        String command = "{\"method\":\"get\",\"audio_config\":{\"name\": [\"microphone\"]}}";
        try {
            ApiResponse response = sendPostRequest("/stok=" + token + "/ds", command, "audio_config");
            if (response.errorCode == 0) {
                if (response.result.has("microphone")) {
                    volume = response.result.get("microphone").getAsJsonObject().get("volume").getAsInt();
                    logger.debug("microphone volume is {}", volume);
                }
            } else {
                throw processErrorResponse("microphone", response);
            }
        } catch (JsonSyntaxException e) {
            logger.error(e.getMessage());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return volume;
    }

    @Override
    public void setMicrophoneVolume(int volume) {
        String command = String
                .format("{\"method\": \"set\",\"audio_config\":{\"microphone\":{\"volume\":\"%d\"}}}", volume);
        sendCommand(command, "audio_config");
    }
}
