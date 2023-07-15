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

import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.CLOCK_STATUS;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.CONNECTION_TYPE;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.DEVICE_INFO_BASIC;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.DEVICE_INFO_FULL;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.INTRUSION_DETECTION;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.LAST_ALARM_INFO;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.LED_STATUS;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.LENS_MASK;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.LIGHT_FREQUENCY_INFO;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.LINECROSSING_DETECTION;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.MICROPHONE_INFO;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.MOTION_DETECTION;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.MSG_ALARM_INFO;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.MSG_ALARM_MANUAL;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.MSG_PUSH_INFO;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.NIGHT_VISION_MODE;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.PERSON_DETECTION;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.PRESETS;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.SPEAKER_INFO;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.TAMPER_DETECTION;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.TARGET_TRACK;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.USER_ID;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.WAN_INFO;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.getClassByModuleAndSection;
import static org.openhab.binding.tapocamera.internal.api.utils.ApiUtils.createMultipleCommand;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.openhab.binding.tapocamera.internal.api.dto.PresetInfo;
import org.openhab.binding.tapocamera.internal.api.dto.TargetAutoTrackInfo;
import org.openhab.binding.tapocamera.internal.api.dto.alarm.LastAlarmInfo;
import org.openhab.binding.tapocamera.internal.api.dto.alarm.MsgAlarmInfo;
import org.openhab.binding.tapocamera.internal.api.dto.alarm.MsgPushInfo;
import org.openhab.binding.tapocamera.internal.api.dto.audio.AudioMicrophoneInfo;
import org.openhab.binding.tapocamera.internal.api.dto.audio.AudioSpeakerInfo;
import org.openhab.binding.tapocamera.internal.api.dto.detection.IntrusionDetectionInfo;
import org.openhab.binding.tapocamera.internal.api.dto.detection.LineCrossingDetectionInfo;
import org.openhab.binding.tapocamera.internal.api.dto.detection.MotionDetection;
import org.openhab.binding.tapocamera.internal.api.dto.detection.PersonDetectionInfo;
import org.openhab.binding.tapocamera.internal.api.dto.detection.TamperDetectionInfo;
import org.openhab.binding.tapocamera.internal.api.dto.image.ImageCommon;
import org.openhab.binding.tapocamera.internal.api.dto.image.ImageSwitch;
import org.openhab.binding.tapocamera.internal.api.dto.image.LensMaskInfo;
import org.openhab.binding.tapocamera.internal.api.dto.system.ClockStatus;
import org.openhab.binding.tapocamera.internal.api.dto.system.DeviceInfo;
import org.openhab.binding.tapocamera.internal.api.dto.system.LedStatus;
import org.openhab.binding.tapocamera.internal.api.dto.system.NetworkInfo;
import org.openhab.binding.tapocamera.internal.api.response.ApiMethodResult;
import org.openhab.binding.tapocamera.internal.api.response.ApiResponse;
import org.openhab.binding.tapocamera.internal.api.utils.ApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Tapo camera api.
 */
public class TapoCameraApiImpl implements TapoCameraApi {
    private final Logger logger = LoggerFactory.getLogger(TapoCameraApiImpl.class);
    private static final String TAPO_USER_AGENT = "Tapo CameraClient Android";
    private String hostname = "";
    private String token = "";
    private Integer userId = -1;
    private static Gson gson = new Gson();
    private final HttpClient httpClient;

    /**
     * Instantiates a new Tapo camera api.
     *
     * @param httpClient the http client
     */
    public TapoCameraApiImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void setHostname(String hostname) {
        this.hostname = "https://" + hostname;
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

    @Override
    public Boolean isAuth() {
        return !token.isEmpty();
    }
    @Override
    public Boolean auth(String username, String password) throws ApiException {
        Boolean result = false;
        Request request = httpClient.newRequest(hostname);
        setHeaders(request);
        request.method(HttpMethod.POST);
        String passwordHash = ApiUtils.getPasswordHash(password);

        JsonObject jsonParams = new JsonObject();
        jsonParams.addProperty("hashed", Boolean.TRUE);
        jsonParams.addProperty("password", passwordHash);
        jsonParams.addProperty("username", username);

        JsonObject jsonAuth = new JsonObject();
        jsonAuth.addProperty("method", "login");
        jsonAuth.add("params", jsonParams);

        String body = jsonAuth.toString();
        request.content(new StringContentProvider(body));
        try {
            ContentResponse contentResponse = request.send();
            JsonElement json = JsonParser.parseString(contentResponse.getContentAsString());

            ApiResponse response = gson.fromJson(json, ApiResponse.class);
            if (response.errorCode == 0 && response.result.has("stok")) {
                token = response.result.get("stok").getAsString();
                if (isAuth()) {
                    userId = getUserId();
                    result = true;
                }
            } else {
                // TODO: log.error && throw new Exception
                logger.error("Error in response or Invalid token, error code: {}", response.errorCode);
            }
        } catch (TimeoutException e) {
            logger.error("TimeoutException: {}", e.getMessage());
            throw new ApiException(String.format("TimeoutException: %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.error("InterruptedException: {}", e.getMessage());
            throw new ApiException(String.format("TimeoutException: %s", e.getMessage()));
        } catch (ExecutionException e) {
            logger.error("ExecutionException: {}", e.getMessage());
            throw new ApiException(String.format("TimeoutException: %s", e.getMessage()));
        }
        return result;
    }

    private Integer getUserId() {
        Integer result = -1;
        String command = createMultipleCommand(List.of(USER_ID));
        ApiResponse response = sendMultipleRequest(token, command);
        if (response.errorCode == 0) {
            List<ApiMethodResult> multi = processMultipleResponses(response);
            if (!multi.isEmpty()) {
                result = multi.get(0).result.get("user_id").getAsInt();
            }
        } else {
            logger.error("TapoCameraApi error {}", response.errorCode);
        }
        return result;
    }

    @Override
    public ApiResponse sendMultipleRequest(String token, String data) {
        Request request = httpClient.newRequest(hostname + "/stok=" + token + "/ds");
        setHeaders(request);
        request.method(HttpMethod.POST);
        request.content(new StringContentProvider(data));
        try {
            ContentResponse contentResponse = request.send();
            ApiResponse response = gson.fromJson(contentResponse.getContentAsString(), ApiResponse.class);
            if (response.errorCode == 0) {
                return response;
            } else {
                // TODO: log.error && throw new Exception
                logger.error("Error in response, error code: {}", response.errorCode);
                return new ApiResponse();
            }
        } catch (TimeoutException e) {
            logger.error("TimeoutException: {}", e.getMessage());
            return null;
        } catch (InterruptedException e) {
            logger.error("InterruptedException: {}", e.getMessage());
            return null;
        } catch (ExecutionException e) {
            logger.error("ExecutionException: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Object sendSingleRequest(String token, String data) {
        Request request = httpClient.newRequest(hostname + "/stok=" + token + "/ds");
        setHeaders(request);
        request.method(HttpMethod.POST);
        request.content(new StringContentProvider(data));
        try {
            ContentResponse contentResponse = request.send();
            JsonObject response = JsonParser.parseString(contentResponse.getContentAsString()).getAsJsonObject();
            if (response.has("error_code") && response.get("error_code").getAsInt() == 0) {
                return response;
            } else {
                // TODO: log.error && throw new Exception
                logger.error("Error in response, error code: {}", response.get("error_code").getAsInt());
                return new Object();
            }
        } catch (TimeoutException e) {
            logger.error("TimeoutException: {}", e.getMessage());
            return null;
        } catch (InterruptedException e) {
            logger.error("InterruptedException: {}", e.getMessage());
            return null;
        } catch (ExecutionException e) {
            logger.error("ExecutionException: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Execute set method boolean.
     *
     * @param type      the type
     * @param paramName the param name
     * @param value     the value
     * @return the boolean
     */
    public boolean executeSetMethod(ApiMethodTypes type, String paramName, Object value) {
        String module = type.getModule();
        String section = type.getSection();
        String command = ApiUtils.createSingleCommand("set", module, section, paramName, value);
        JsonObject obj = (JsonObject) sendSingleRequest(token, command);
        ApiResponse response = gson.fromJson(obj, ApiResponse.class);
        return  response.errorCode == 0;
    }

    @Override
    public Object processSingleResponse(Object data, String moduleName, String section) {
        if (((JsonObject) data).has(moduleName)) {
            JsonObject jsonSection = ((JsonObject) data).get(moduleName).getAsJsonObject();
            if (jsonSection.has(section)) {
                JsonObject obj = jsonSection.get(section).getAsJsonObject();
                return gson.fromJson(obj, getClassByModuleAndSection(moduleName, section));
            }
        } else {
            logger.error("Module " + moduleName + " not found in response");
        }
        return null;
    }

    @Override
    public List<Object> processSingleResponse(Object data, String moduleName, List<String> sections) {
        List<Object> result = new ArrayList<>();
        if (((JsonObject) data).has(moduleName)) {
            JsonObject jsonSections = ((JsonObject) data).get(moduleName).getAsJsonObject();
            sections.forEach(section -> {
                if (jsonSections.has(section)) {
                    JsonObject obj = jsonSections.get(section).getAsJsonObject();
                    Object objClass = gson.fromJson(obj, getClassByModuleAndSection(moduleName, section));
                    result.add(objClass);
                }
            });
        } else {
            logger.error("Module " + moduleName + " not found in response");
        }
        return result;
    }

    private List<Object> processMethodResult(String method, JsonObject obj) {
        List<Object> result = new ArrayList<>();
        Arrays.asList(ApiMethodTypes.values()).forEach(t -> {
            if (t.getMethod().equals(method)) {
                Object data;
                if (t.getSection() != null && obj.getAsJsonObject().has(t.getModule())) {
                    data = gson.fromJson(obj.getAsJsonObject(t.getModule()).getAsJsonObject(t.getSection()), t.getClazz());
                } else {
                    data = gson.fromJson(obj.getAsJsonPrimitive(t.getModule()), t.getClazz());
                }
                // TODO: fill and updateState
                result.add(data);
                // processAllResults(data);
            }
        });
        return result;
    }

    private List<ApiMethodResult> processMultipleResponses(ApiResponse response) {
        List<ApiMethodResult> results = new ArrayList<>();
        if (response.errorCode == 0 ) {
            Type listType = new TypeToken<ArrayList<ApiMethodResult>>() {}.getType();
            List<ApiMethodResult> responses = gson.fromJson(response.result.getAsJsonArray("responses"), listType);
            if (responses != null && !responses.isEmpty()) {
                responses.forEach(resp -> {
                    if (resp.errorCode == 0) {
                        //JsonObject obj = resp.result;
                        //String method = resp.method;
                        // processMethodResult(method, obj);
                        results.add(resp);
                    }
                });
            }
        } else {
            logger.error("TapoCameraApi error {}", response.errorCode);
        }
        return results;
    }

    @Override
    public Class<?> getParameterInfo(ApiMethodTypes type) {
        String module = type.getModule();
        String section = type.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        return (Class<?>) processSingleResponse(singleResponse, module, section);
    }

    @Override
    public DeviceInfo getDeviceInfo() {
        String module = DEVICE_INFO_FULL.getModule();
        List<String> sections = List.of(DEVICE_INFO_FULL.getSection(), DEVICE_INFO_BASIC.getSection());
        String command = ApiUtils.createSingleCommand("get", module, sections);
        JsonObject response = (JsonObject) sendSingleRequest(token, command);
        if (response.get("error_code").getAsInt() == 0) {
            return gson.fromJson(response.get("device_info").getAsJsonObject(), DeviceInfo.class);
        }
        return new DeviceInfo();
    }

    @Override
    public NetworkInfo getNetworkInfo() {
        NetworkInfo result;
        String module = WAN_INFO.getModule();
        String section = WAN_INFO.getSection();
        String command = ApiUtils.createSingleCommand("get", module, List.of(section));
        JsonObject response = (JsonObject) sendSingleRequest(token, command);
        if (response.get("error_code").getAsInt() == 0) {
            result = (NetworkInfo) gson.fromJson(response.get(module).getAsJsonObject().get(section), WAN_INFO.getClazz());

            command = createMultipleCommand(List.of(CONNECTION_TYPE));
            ApiResponse response1 = sendMultipleRequest(token, command);
            if (response1.errorCode == 0) {
                List<ApiMethodResult> methodResults = processMultipleResponses(response1);
                result.link_type = methodResults.get(0).result.get("link_type").getAsString();
            }
            return result;
        }
        return new NetworkInfo();
    }

    @Override
    public AudioSpeakerInfo getSpeakerInfo() {
        String module = SPEAKER_INFO.getModule();
        String section = SPEAKER_INFO.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (AudioSpeakerInfo) result;
    }

    @Override
    public void setSpeakerVolume(int volume) {
        executeSetMethod(SPEAKER_INFO, "volume", String.valueOf(volume));
    }

    @Override
    public AudioMicrophoneInfo getMicrophoneInfo() {
        String module = MICROPHONE_INFO.getModule();
        String section = MICROPHONE_INFO.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (AudioMicrophoneInfo) result;
    }

    @Override
    public void setMicrophoneVolume(int volume) {
        executeSetMethod(MICROPHONE_INFO, "volume", String.valueOf(volume));
    }

    @Override
    public ClockStatus getClockStatus() {
        String module = CLOCK_STATUS.getModule();
        String section = CLOCK_STATUS.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (ClockStatus) result;
    }

    @Override
    public ImageCommon getImageCommonInfo() {
        String module = LIGHT_FREQUENCY_INFO.getModule();
        String section = LIGHT_FREQUENCY_INFO.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (ImageCommon) result;
    }

    @Override
    public ImageSwitch getImageSwitchInfo() {
        String module = NIGHT_VISION_MODE.getModule();
        String section = NIGHT_VISION_MODE.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (ImageSwitch) result;
    }

    @Override
    public LensMaskInfo getLensMaskInfo() {
        String module = LENS_MASK.getModule();
        String section = LENS_MASK.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (LensMaskInfo) result;
    }

    @Override
    public LedStatus getLedStatus() {
        String module = LED_STATUS.getModule();
        String section = LED_STATUS.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (LedStatus) result;
    }

    @Override
    public void setLedStatus(String status) {
        executeSetMethod(LED_STATUS, "enabled", status);
    }

    @Override
    public MsgAlarmInfo getMsgAlarmInfo() {
        String module = MSG_ALARM_INFO.getModule();
        String section = MSG_ALARM_INFO.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (MsgAlarmInfo) result;
    }
    @Override
    public void setAlarmInfoEnabled(String status) {
        executeSetMethod(MSG_ALARM_INFO, "enabled", status);
    }
    @Override
    public void setAlarmInfoMode(List<String> modes) {
        executeSetMethod(MSG_ALARM_INFO, "alarm_mode", modes);
    }
    @Override
    public void setAlarmInfoType(String type) {
        executeSetMethod(MSG_ALARM_INFO, "alarm_type", type);
    }
    @Override
    public void setManualAlarm(String state) {
        executeSetMethod(MSG_ALARM_MANUAL, "action", state);
    }
    @Override
    public MsgPushInfo getMsgPushInfo() {
        String module = MSG_PUSH_INFO.getModule();
        String section = MSG_PUSH_INFO.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (MsgPushInfo) result;
    }

    @Override
    public LastAlarmInfo getLastAlarmInfo() {
        String module = LAST_ALARM_INFO.getModule();
        String section = LAST_ALARM_INFO.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (LastAlarmInfo) result;
    }

    @Override
    public IntrusionDetectionInfo getIntrusionDetectionInfo() {
        String module = INTRUSION_DETECTION.getModule();
        String section = INTRUSION_DETECTION.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (IntrusionDetectionInfo) result;
    }
    @Override
    public void setIntrusionDetectEnabled(String state) {
        executeSetMethod(INTRUSION_DETECTION, "enable", state);
    }
    @Override
    public LineCrossingDetectionInfo getLineCrossingDetectionInfo() {
        String module = LINECROSSING_DETECTION.getModule();
        String section = LINECROSSING_DETECTION.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (LineCrossingDetectionInfo) result;
    }
    @Override
    public void setLineCrossingDetectEnabled(String state) {
        executeSetMethod(LINECROSSING_DETECTION, "enable", state);
    }
    @Override
    public MotionDetection getMotionDetectionInfo() {
        String module = MOTION_DETECTION.getModule();
        String section = MOTION_DETECTION.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (MotionDetection) result;
    }
    @Override
    public void setMotionDetectEnabled(String state) {
        executeSetMethod(MOTION_DETECTION, "enable", state);
    }
    @Override
    public void setMotionDetectSensitivity(String state) {
        executeSetMethod(MOTION_DETECTION, "sensitivity", state);
    }
    @Override
    public void setMotionDetectSensitivity(Integer state) {
        executeSetMethod(MOTION_DETECTION, "digital_sensitivity", String.valueOf(state));
    }
    @Override
    public PersonDetectionInfo getPersonDetectionInfo() {
        String module = PERSON_DETECTION.getModule();
        String section = PERSON_DETECTION.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (PersonDetectionInfo) result;
    }
    @Override
    public void setPersonDetectEnabled(String state) {
        executeSetMethod(PERSON_DETECTION, "enable", state);
    }
    @Override
    public void setPersonDetectSensitivity(Integer state) {
        executeSetMethod(PERSON_DETECTION, "sensitivity", String.valueOf(state));
    }
    @Override
    public TamperDetectionInfo getTamperDetectionInfo() {
        String module = TAMPER_DETECTION.getModule();
        String section = TAMPER_DETECTION.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (TamperDetectionInfo) result;
    }
    @Override
    public void setTamperDetectEnabled(String state) {
        executeSetMethod(TAMPER_DETECTION, "enable", state);
    }
    @Override
    public PresetInfo getPresetInfo() {
        String module = PRESETS.getModule();
        String section = PRESETS.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (PresetInfo) result;
    }
    @Override
    public TargetAutoTrackInfo getTargetAutoTrackInfo() {
        String module = TARGET_TRACK.getModule();
        String section = TARGET_TRACK.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (TargetAutoTrackInfo) result;
    }
    @Override
    public List<ApiMethodResult> getChangebleParameters() {
        List<ApiMethodResult> result = new ArrayList<>();
        List<ApiMethodTypes> listCommands = List.of(
                ApiMethodTypes.MOTION_DETECTION, ApiMethodTypes.INTRUSION_DETECTION,
                ApiMethodTypes.LINECROSSING_DETECTION, ApiMethodTypes.PERSON_DETECTION,
                ApiMethodTypes.TAMPER_DETECTION,
                ApiMethodTypes.MSG_ALARM_INFO, ApiMethodTypes.LAST_ALARM_INFO, ApiMethodTypes.MSG_PUSH_INFO,
                ApiMethodTypes.NIGHT_VISION_MODE,
                ApiMethodTypes.LENS_MASK, ApiMethodTypes.LIGHT_FREQUENCY_INFO,
                ApiMethodTypes.LED_STATUS,
                ApiMethodTypes.TARGET_TRACK, ApiMethodTypes.PRESETS,
                ApiMethodTypes.SPEAKER_INFO, ApiMethodTypes.MICROPHONE_INFO
        );

        String multipleCommand = ApiUtils.createMultipleCommand(listCommands);
        ApiResponse response = sendMultipleRequest(token, multipleCommand);
        if (response.errorCode == 0) {
            return processMultipleResponses(response);
        } else if (response.errorCode == -40401) {
            // TODO: invalid token
            logger.error("Error in response or Invalid token, error code: {}", response.errorCode);
        } else {
            logger.error("Error in response, error code: {}", response.errorCode);
        }
        return result;
    }
}