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

import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.CLOCK_STATUS;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.GOTO_PRESETS;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.IMAGE_ROTATION_STATUS;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.INTRUSION_DETECTION;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.LAST_ALARM_INFO;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.LED_STATUS;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.LENS_MASK;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.LIGHT_FREQUENCY_INFO;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.LINECROSSING_DETECTION;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.MICROPHONE_INFO;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.MODULES_SPEC;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.MOTION_DETECTION;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.MSG_ALARM_INFO;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.MSG_ALARM_MANUAL;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.MSG_PUSH_INFO;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.PERSON_DETECTION;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.PRESETS;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.SPEAKER_INFO;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.TAMPER_DETECTION;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.TARGET_TRACK;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.USER_ID;
import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.getClassByModuleAndSection;
import static org.openhab.binding.tapocamera.internal.api.utils.ApiUtils.createMultipleCommand;
import static org.openhab.binding.tapocamera.internal.api.utils.ApiUtils.singleToMulti;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.binding.tapocamera.internal.api.dto.PresetInfo;
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
import org.openhab.binding.tapocamera.internal.api.dto.detection.TargetAutoTrackInfo;
import org.openhab.binding.tapocamera.internal.api.dto.image.ImageCommon;
import org.openhab.binding.tapocamera.internal.api.dto.image.ImageSwitch;
import org.openhab.binding.tapocamera.internal.api.dto.image.LensMaskInfo;
import org.openhab.binding.tapocamera.internal.api.dto.system.ClockStatus;
import org.openhab.binding.tapocamera.internal.api.dto.system.DeviceInfo;
import org.openhab.binding.tapocamera.internal.api.dto.system.LedStatus;
import org.openhab.binding.tapocamera.internal.api.dto.system.ModuleSpec;
import org.openhab.binding.tapocamera.internal.api.dto.system.NetworkInfo;
import org.openhab.binding.tapocamera.internal.api.response.ApiMethodResult;
import org.openhab.binding.tapocamera.internal.api.response.ApiResponse;
import org.openhab.binding.tapocamera.internal.api.utils.ApiUtils;
import org.openhab.binding.tapocamera.internal.api.utils.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * The type Tapo camera api.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public class TapoCameraApiImpl implements TapoCameraApi {

    private final Logger logger = LoggerFactory.getLogger(TapoCameraApiImpl.class);

    private String hostname = "";
    private String url = "";
    private ApiAuthData apiAuthData = null;
    private String passwordHash = "";
    private Long startSeq = -1L;
    private Boolean isSecureConnection = false;
    private String nonce = "";
    private String cnonce = "C167F0A5";
    private static Gson gson = new Gson();
    private final HttpClient httpClient;
    private String tag = "";

    /**
     * Instantiates a new Tapo camera api.
     *
     * @param httpClient the http client
     */
    public TapoCameraApiImpl(HttpClient httpClient, String tag) {
        this.httpClient = httpClient;
        this.tag = tag;
    }

    private void setHeaders(Request request) {
        request.header(HttpHeader.HOST, this.hostname);
        request.header(HttpHeader.REFERER, this.url);
        request.header(HttpHeader.ACCEPT, "application/json");
        request.header(HttpHeader.ACCEPT_ENCODING, "gzip, deflate");
        request.header(HttpHeader.USER_AGENT, "Tapo CameraClient Android");
        request.header(HttpHeader.CONNECTION, "close");
        request.header(HttpHeader.CONTENT_TYPE, "application/json; charset=UTF-8");
        request.header("requestByApp", "true");
    }

    private Boolean getIsSecureConnection(String username) throws ApiException {
        Request request = httpClient.newRequest(this.url).method(HttpMethod.POST);
        setHeaders(request);

        JsonObject params = new JsonObject();
        params.addProperty("encrypt_type", "3");
        params.addProperty("username", username);

        JsonObject method = new JsonObject();
        method.addProperty("method", "login");
        method.add("params", params);

        logger.debug("[{}] body: {}", this.tag, method.toString());
        request.content(new StringContentProvider(method.toString()));

        try {
            JsonElement json = JsonParser.parseString(request.send().getContentAsString());
            ApiResponse response = gson.fromJson(json, ApiResponse.class);
            logger.debug("{}: response: {}", this.tag, response);

            if (response.errorCode == ApiErrorCodes.ERROR_40413.getCode()) {
                JsonObject data = (JsonObject) response.result.get("data");
                if (data.has("encrypt_type") && data.get("encrypt_type").isJsonArray()
                        && !data.get("encrypt_type").getAsJsonArray().isEmpty()
                        && data.get("encrypt_type").getAsJsonArray().get(0).getAsInt() == 3) {
                    return true;
                }
            }

            return false;

        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            throw new ApiException(String.format("[{}] {} when checking if connection is secured or not: {}", this.tag,
                    e.getClass().getName(), e.getMessage()));
        }
    }

    private JsonObject getFirstStepLoginMethod(String username, String password) {

        JsonObject params = new JsonObject();
        if (this.isSecureConnection) {
            logger.debug("[{}] Connection is secure", this.tag);
            this.cnonce = HexUtils.randomHexString(8);
            this.passwordHash = ApiUtils.getPasswordHashSHA256(password);

            params.addProperty("cnonce", this.cnonce);
            params.addProperty("encrypt_type", "3");
            params.addProperty("username", username);
        } else {
            logger.debug("[{}] Connection is insecure", this.tag);
            this.passwordHash = ApiUtils.getPasswordHash(password);

            params.addProperty("hashed", Boolean.TRUE);
            params.addProperty("password", this.passwordHash);
            params.addProperty("username", username);
        }

        JsonObject method = new JsonObject();
        method.addProperty("method", "login");
        method.add("params", params);

        return method;
    }

    private void updateToSecondStepLoginMethod(JsonObject method, String username) {

        String toNewPass = this.passwordHash + this.cnonce + this.nonce;
        logger.trace("[{}] sha256 + cnonce + nonce: {}", this.tag, toNewPass);

        String digestPassword = ApiUtils.getPasswordHashSHA256(toNewPass);
        logger.trace("[{}] sha256-2: {}", this.tag, digestPassword);

        String digestPassword2 = digestPassword + this.cnonce + this.nonce;
        logger.trace("[{}] sha256-2 + cnonce + nonce: {}", this.tag, digestPassword2);

        JsonObject params = method.get("params").getAsJsonObject();

        params.addProperty("cnonce", this.cnonce);
        params.addProperty("encrypt_type", "3");
        params.addProperty("digest_passwd", digestPassword2);
        params.addProperty("username", username);
    }

    private Boolean validateDeviceConfirm(String deviceConfirm) {
        String hashedNoncesWithSHA256 = ApiUtils.getPasswordHashSHA256(this.cnonce + this.passwordHash + this.nonce);
        return (deviceConfirm.equals(hashedNoncesWithSHA256 + this.nonce + this.cnonce));
    }

    private Integer getUserId() {
        String command = createMultipleCommand(List.of(USER_ID));
        ApiResponse response = sendMultipleRequest(command);

        if (response.errorCode == 0) {
            List<ApiMethodResult> multi = processMultipleResponses(response);
            if (!multi.isEmpty()) {
                return multi.get(0).result.get("user_id").getAsInt();
            }
            return -1;
        } else {
            logger.error("[{}] Unable to get user_id, error code: {}", this.tag, response.errorCode);
            return -1;
        }
    }

    private ApiAuthData processAuthResponse(ApiResponse response) throws ApiException {
        logger.debug("[{}] Processing auth unsecure response", this.tag);

        if (response.errorCode == 0 && response.result.has("stok")) {
            ApiAuthData authData = new ApiAuthData();
            authData.stok = response.result.get("stok").getAsString();
            if (!authData.stok.isEmpty()) {
                authData.userId = getUserId();
            }
            return authData;
        } else {
            if (response.errorCode == ApiErrorCodes.ERROR_40401.getCode()) {
                throw new ApiException(
                        String.format("[{}] Invalid token, error code: {}", this.tag, response.errorCode));
            } else {
                throw new ApiException(
                        String.format("[{}] Error in auth response, error code: {}", this.tag, response.errorCode));
            }
        }
    }

    private String generateEncryptionToken(String tokenType) {
        String hashedKey = ApiUtils.getPasswordHashSHA256(this.cnonce + this.passwordHash + this.nonce);
        return ApiUtils.getPasswordHashSHA256(tokenType + this.cnonce + this.nonce + hashedKey, 32);
    }

    private ApiAuthData processAuthSecureResponse(ApiResponse response, String username, JsonObject method)
            throws ApiException {
        logger.debug("[{}] Processing auth secure response", this.tag);

        if (response.result.has("data")) {
            JsonObject data = response.result.getAsJsonObject("data");

            if (data.has("nonce") && data.has("device_confirm")) {
                this.nonce = data.get("nonce").getAsString();
                logger.trace("[{}] Received nonce: {}", this.tag, this.nonce);

                String deviceConfirm = data.get("device_confirm").getAsString();
                Boolean validated = validateDeviceConfirm(deviceConfirm);

                if (validated) {
                    logger.trace("[{}] sha256: {}", this.tag, this.passwordHash);
                    logger.trace("[{}] cnonce: {}", this.tag, this.cnonce);
                    logger.trace("[{}] nonce: {}", this.tag, this.nonce);

                    updateToSecondStepLoginMethod(method, username);

                    Request request = httpClient.newRequest(this.url).method(HttpMethod.POST);
                    setHeaders(request);

                    logger.trace("[{}] Body: {}", this.tag, method.toString());
                    request.content(new StringContentProvider(method.toString()));

                    try {
                        JsonElement json = JsonParser.parseString(request.send().getContentAsString());
                        response = gson.fromJson(json, ApiResponse.class);
                        logger.info("[{}] Response: {}", this.tag, response);

                        if (response.errorCode == 0 && response.result.has("stok")
                                && response.result.has("start_seq")) {
                            ApiAuthData authData = new ApiAuthData();
                            authData.stok = response.result.get("stok").getAsString();
                            authData.startSeq = response.result.get("start_seq").getAsLong();
                            authData.lsk = generateEncryptionToken("lsk");
                            authData.ivb = generateEncryptionToken("ivb");
                            return authData;
                        } else {
                            throw new ApiException(String.format("[{}] Error in auth response, error code: {}",
                                    this.tag, response.errorCode));
                        }

                    } catch (TimeoutException | InterruptedException | ExecutionException e) {
                        throw new ApiException(String.format("[{}] {} when processing auth secure response: {}", tag,
                                e.getClass().getName(), e.getMessage()));
                    }
                } else {
                    throw new ApiException(String.format(
                            "[{}] Unable to validate device confirm in auth secure response, error code: {}", this.tag,
                            response.errorCode));
                }

            } else {
                throw new ApiException(String.format("[{}] Empty nonce or device_confirm, error code: {}", this.tag,
                        response.errorCode));
            }
        } else {
            throw new ApiException(String.format("[{}] Invalid token, error code: {}", this.tag, response.errorCode));
        }
    }

    private String generateTag(String data) {
        String hashedKey = ApiUtils.getPasswordHashSHA256(this.passwordHash + this.cnonce);
        return ApiUtils.getPasswordHashSHA256(hashedKey + data + this.startSeq.toString());
    }

    @Override
    public void setHostname(String hostname) {
        this.hostname = hostname;
        this.url = "https://" + hostname;
    }

    @Override
    public Boolean isAuthenticated() {
        return !apiAuthData.stok.isEmpty();
    }

    @Override
    public Boolean auth(String username, String password) throws ApiException {
        this.isSecureConnection = getIsSecureConnection(username);

        Request request = httpClient.newRequest(this.url).method(HttpMethod.POST);
        setHeaders(request);

        JsonObject method = getFirstStepLoginMethod(username, password);

        logger.trace("[{}] Body: {}", this.tag, method.toString());
        request.content(new StringContentProvider(method.toString()));

        try {
            JsonElement json = JsonParser.parseString(request.send().getContentAsString());
            ApiResponse response = gson.fromJson(json, ApiResponse.class);
            logger.info("[{}] Response: {}", this.tag, response);

            if (isSecureConnection) {
                this.apiAuthData = processAuthSecureResponse(response, username, method);
            } else {
                this.apiAuthData = processAuthResponse(response);
            }

        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            throw new ApiException(String.format("[{}] {} when checking if connection is secured or not: {}", tag,
                    e.getClass().getName(), e.getMessage()));
        }

        return this.apiAuthData != null;
    }

    private ApiResponse executeRequest(String method, String params) throws ApiException {
        Request request = httpClient.newRequest(this.url + "/stok=" + this.apiAuthData.stok + "/ds")
                .method(HttpMethod.POST);

        setHeaders(request);

        String command = "{'method': '" + method + "', 'params': " + params + "}";
        String data = "{'method': 'multipleRequest', 'params': {'requests': [" + command + "]}}";

        logger.debug("[{}] Body: {}", this.tag, data);
        request.content(new StringContentProvider(data));

        try {
            JsonElement json = JsonParser.parseString(request.send().getContentAsString());
            ApiResponse response = gson.fromJson(json, ApiResponse.class);
            logger.debug("[{}] response: {}", this.tag, response);

            if (!isSecureConnection) {
                return response;
            }

            if (response.errorCode == 0) {
                String result = response.result.getAsJsonObject().get("response").getAsString();
                result = ApiUtils.decryptResponse(result, apiAuthData.lsk, apiAuthData.ivb);
                logger.debug("[{}] Decrypted response: {}", tag, result);
                json = JsonParser.parseString(result);
                response = gson.fromJson(json, ApiResponse.class);
                if (response.errorCode == 0) {
                    JsonObject responseJson = response.result.get("responses").getAsJsonArray().get(0)
                            .getAsJsonObject();
                    String responseMethod = responseJson.get("method").getAsString();
                    if (method.equals(responseMethod)) {
                        response.result = responseJson.get("result").getAsJsonObject();
                        response.errorCode = responseJson.get("error_code").getAsInt();
                    }
                }

                return response;
            } else {
                throw new ApiException(String.format("[{}] Error in executeRequest response, error code: {}", this.tag,
                        response.errorCode));
            }
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            throw new ApiException(String.format("[{}] {} when checking if connection is secured or not: {}", this.tag,
                    e.getClass().getName(), e.getMessage()));
        }
    }

    private void makeEncryptedRequest(Request originRequest, String data, Long seq, String passwordHash, String lsk,
            String ivb) {
        String encrypted = ApiUtils.encryptRequest(data, lsk, ivb);
        logger.trace("{}: encrypted data: {}", tag, encrypted);
        JsonObject jsonParams = new JsonObject();
        jsonParams.addProperty("request", encrypted);
        JsonObject encryptedRequest = new JsonObject();
        encryptedRequest.addProperty("method", "securePassthrough");
        encryptedRequest.add("params", jsonParams);

        logger.trace("{}: encrypted request: {}", tag, encryptedRequest);
        originRequest.content(new StringContentProvider(encryptedRequest.toString()));
        originRequest.header("Seq", seq.toString());

        String tapoTag = generateTag(encryptedRequest.toString());
        logger.debug("{}: Tapo tag: {}", tag, tapoTag);

        originRequest.header("Tapo_tag", tapoTag);

        startSeq += 1;
    }

    @Override
    public ApiResponse sendMultipleRequest(String data) {
        Request request = httpClient.newRequest(this.url + "/stok=" + this.apiAuthData.stok + "/ds")
                .method(HttpMethod.POST);

        setHeaders(request);

        if (isSecureConnection) {
            logger.debug("{}: sendMultipleRequest data: {}", tag, data);
            makeEncryptedRequest(request, data, startSeq, passwordHash, apiAuthData.lsk, apiAuthData.ivb);
        } else {
            request.content(new StringContentProvider(data));
        }
        try {
            ContentResponse contentResponse = request.send();
            ApiResponse response = gson.fromJson(contentResponse.getContentAsString(), ApiResponse.class);
            if (response.errorCode == 0) {
                if (isSecureConnection) {
                    String result = response.result.get("response").getAsString();
                    result = ApiUtils.decryptResponse(result, apiAuthData.lsk, apiAuthData.ivb);
                    logger.debug("{}: decrypted response: {}", tag, result);
                    JsonObject json = JsonParser.parseString(result).getAsJsonObject();
                    response.result = json.getAsJsonObject("result");
                }
                return response;
            } else {
                // TODO: log.error && throw new Exception
                logger.error("{}: Error in response, error code: {}", tag, response.errorCode);
                this.apiAuthData.stok = "";
                return new ApiResponse();
            }
        } catch (TimeoutException e) {
            this.apiAuthData.stok = "";
            logger.error("{}: TimeoutException: {}", tag, e.getMessage());
            return null;
        } catch (InterruptedException e) {
            logger.error("{}: InterruptedException: {}", tag, e.getMessage());
            return null;
        } catch (ExecutionException e) {
            this.apiAuthData.stok = "";
            logger.error("{}: ExecutionException: {}", tag, e.getMessage());
            return null;
        }
    }

    @Override
    public Object sendSingleRequest(String data) {
        Request request = httpClient.newRequest(this.url + "/stok=" + this.apiAuthData.stok + "/ds")
                .method(HttpMethod.POST);

        setHeaders(request);

        if (isSecureConnection) {
            logger.debug("{}: sendSingleRequest data: {}", tag, data);
            // makeEncryptedRequest(request, data, startSeq, passwordHash, this.apiAuthData.lsk, this.apiAuthData.ivb);
            data = "{\"method\": \"multipleRequest\", \"params\": {\"requests\": [" + data + "]}}";
            request.content(new StringContentProvider(data));
        } else {
            request.content(new StringContentProvider(data));
        }

        try {
            ContentResponse contentResponse = request.send();

            JsonObject response = JsonParser.parseString(contentResponse.getContentAsString()).getAsJsonObject();
            if (response.has("error_code") && response.get("error_code").getAsInt() == 0) {
                if (isSecureConnection) {
                    String result = response.get("result").getAsJsonObject().get("response").getAsString();
                    result = ApiUtils.decryptResponse(result, apiAuthData.lsk, apiAuthData.ivb);
                    logger.debug("{}: decrypted response: {}", tag, result);
                    JsonObject json = JsonParser.parseString(result).getAsJsonObject();
                    if (json.has("error_code") && json.get("error_code").getAsInt() == 0) {
                        response = json;
                    } else {
                        logger.error("{}: Error in response, error code: {}, {}", tag,
                                json.get("error_code").getAsInt(),
                                ApiErrorCodes.getErrorByCode(json.get("error_code").getAsInt()).getMessage());
                        response.addProperty("error_code", json.get("error_code").getAsInt());
                    }
                }
                return response;
            } else {
                // TODO: log.error && throw new Exception
                logger.error("{}: Error in response, error code: {}", tag, response.get("error_code").getAsInt());
                this.apiAuthData.stok = "";
                return new Object();
            }
        } catch (TimeoutException e) {
            logger.error("{}: TimeoutException: {}", tag, e.getMessage());
            this.apiAuthData.stok = "";
            return null;
        } catch (InterruptedException e) {
            logger.error("{}: InterruptedException: {}", tag, e.getMessage());
            return null;
        } catch (ExecutionException e) {
            logger.error("{}: ExecutionException: {}", tag, e.getMessage());
            this.apiAuthData.stok = "";
            return null;
        }
    }

    /**
     * Execute set method boolean.
     *
     * @param type the type
     * @param paramName the param name
     * @param value the value
     * @return the boolean
     */
    public boolean executeSetMethod(ApiMethodTypes type, String paramName, Object value) {
        boolean res = false;
        try {
            res = executeCommandMethod("set", type, paramName, value);
        } catch (Exception e) {
            logger.error("{}: failed to executeSetMethod: {}", tag, paramName);
        }
        return res;
    }

    public boolean executeDoMethod(ApiMethodTypes type, String paramName, Object value) {
        boolean res = false;
        try {
            res = executeCommandMethod("do", type, paramName, value);
        } catch (Exception e) {
            logger.error("{}: failed to executeDoMethod: {}", tag, paramName);
        }
        return res;
    }

    private boolean executeCommandMethod(String method, ApiMethodTypes type, String paramName, Object value)
            throws ApiException {
        String module = type.getModule();
        String section = type.getSection();
        String command = ApiUtils.createSingleCommand(method, module, section, paramName, value);
        try {
            JsonObject obj = (JsonObject) sendSingleRequest(command);
            ApiResponse response = gson.fromJson(obj, ApiResponse.class);
            return response.errorCode == 0;
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
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
            logger.error("{}: Module {} not found in response", tag, moduleName);
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
            logger.error("{}: Module {} not found in response", tag, moduleName);
        }
        return result;
    }

    private List<Object> processMethodResult(String method, JsonObject obj) {
        List<Object> result = new ArrayList<>();
        Arrays.asList(ApiMethodTypes.values()).forEach(t -> {
            if (t.getMethod().equals(method)) {
                Object data;
                if (t.getSection() != null && obj.getAsJsonObject().has(t.getModule())) {
                    data = gson.fromJson(obj.getAsJsonObject(t.getModule()).getAsJsonObject(t.getSection()),
                            t.getClazz());
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
        if (response.errorCode == 0) {
            Type listType = new TypeToken<ArrayList<ApiMethodResult>>() {
            }.getType();
            List<ApiMethodResult> responses = gson.fromJson(response.result.getAsJsonArray("responses"), listType);
            if (responses != null && !responses.isEmpty()) {
                responses.forEach(resp -> {
                    if (resp.errorCode == 0) {
                        // JsonObject obj = resp.result;
                        // String method = resp.method;
                        // processMethodResult(method, obj);
                        results.add(resp);
                    }
                });
            }
        } else {
            logger.error("{}: TapoCameraApi error {}", tag, response.errorCode);
        }
        return results;
    }

    @Override
    public Class<?> getParameterInfo(ApiMethodTypes type) {
        String module = type.getModule();
        String section = type.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
        return (Class<?>) processSingleResponse(singleResponse, module, section);
    }

    @Override
    public DeviceInfo getDeviceInfo() {
        try {
            ApiResponse response = executeRequest(ApiMethodTypes.DEVICE_INFO.getMethod(),
                    ApiMethodTypes.DEVICE_INFO.getParams());
            return gson.fromJson(response.result.get("device_info").getAsJsonObject(), DeviceInfo.class);
        } catch (ApiException e) {
            logger.error(e.getMessage());
        }

        return new DeviceInfo();
    }

    @Override
    public NetworkInfo getNetworkInfo() {
        try {
            ApiResponse response = executeRequest(ApiMethodTypes.WAN_INFO.getMethod(),
                    ApiMethodTypes.WAN_INFO.getParams());
            return gson.fromJson(response.result.get("device_info").getAsJsonObject(), NetworkInfo.class);
        } catch (ApiException e) {
            logger.error(e.getMessage());
        }

        return new NetworkInfo();

        // NetworkInfo result;
        // String module = WAN_INFO.getModule();
        // String section = WAN_INFO.getSection();
        // String command = ApiUtils.createSingleCommand("get", module, List.of(section));
        // JsonObject response = (JsonObject) sendSingleRequest(command);
        // if (response.get("error_code").getAsInt() == 0) {
        // result = (NetworkInfo) gson.fromJson(response.get(module).getAsJsonObject().get(section),
        // WAN_INFO.getClazz());

        // command = createMultipleCommand(List.of(CONNECTION_TYPE));
        // ApiResponse response1 = sendMultipleRequest(command);
        // if (response1.errorCode == 0) {
        // List<ApiMethodResult> methodResults = processMultipleResponses(response1);
        // result.connectionType = gson.fromJson(methodResults.get(0).result, ConnectionType.class);
        // // result.link_type = methodResults.get(0).result.get("link_type").getAsString();
        // }
        // return result;
        // }
        // return new NetworkInfo();
    }

    @Override
    public ModuleSpec getModuleSpec() {
        String module = MODULES_SPEC.getModule();
        String section = MODULES_SPEC.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (ModuleSpec) result;
    }

    @Override
    public AudioSpeakerInfo getSpeakerInfo() {
        String module = SPEAKER_INFO.getModule();
        String section = SPEAKER_INFO.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
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
        Object singleResponse = sendSingleRequest(singleCommand);
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
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (ClockStatus) result;
    }

    @Override
    public ImageCommon getImageCommonInfo() {
        String module = LIGHT_FREQUENCY_INFO.getModule();
        String section = LIGHT_FREQUENCY_INFO.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (ImageCommon) result;
    }

    @Override
    public ImageSwitch getImageSwitchInfo() {
        String module = IMAGE_ROTATION_STATUS.getModule();
        String section = IMAGE_ROTATION_STATUS.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (ImageSwitch) result;
    }

    @Override
    public void setImageFlip(String state) {
        executeSetMethod(IMAGE_ROTATION_STATUS, "flip_type", state);
    }

    @Override
    public void setImageLdc(String state) {
        executeSetMethod(IMAGE_ROTATION_STATUS, "ldc", state);
    }

    @Override
    public void setImageNightVision(String state) {
        executeSetMethod(LIGHT_FREQUENCY_INFO, "inf_type", state);
    }

    @Override
    public void setImageContrast(Integer state) {
        executeSetMethod(LIGHT_FREQUENCY_INFO, "contrast", String.valueOf(state));
    }

    @Override
    public void setImageSaturation(Integer state) {
        executeSetMethod(LIGHT_FREQUENCY_INFO, "saturation", String.valueOf(state));
    }

    @Override
    public void setImageSharpness(Integer state) {
        executeSetMethod(LIGHT_FREQUENCY_INFO, "sharpness", String.valueOf(state));
    }

    @Override
    public void setImageLuma(Integer state) {
        executeSetMethod(LIGHT_FREQUENCY_INFO, "luma", String.valueOf(state));
    }

    @Override
    public LensMaskInfo getLensMaskInfo() {
        String module = LENS_MASK.getModule();
        String section = LENS_MASK.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (LensMaskInfo) result;
    }

    @Override
    public void setLensMaskEnabled(String state) {
        executeSetMethod(LENS_MASK, "enabled", state);
    }

    @Override
    public LedStatus getLedStatus() {
        String module = LED_STATUS.getModule();
        String section = LED_STATUS.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
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
        Object singleResponse = sendSingleRequest(singleCommand);
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
        executeDoMethod(MSG_ALARM_MANUAL, "action", state);
    }

    @Override
    public MsgPushInfo getMsgPushInfo() {
        String module = MSG_PUSH_INFO.getModule();
        String section = MSG_PUSH_INFO.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (MsgPushInfo) result;
    }

    @Override
    public LastAlarmInfo getLastAlarmInfo() {
        String module = LAST_ALARM_INFO.getModule();
        String section = LAST_ALARM_INFO.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (LastAlarmInfo) result;
    }

    @Override
    public IntrusionDetectionInfo getIntrusionDetectionInfo() {
        String module = INTRUSION_DETECTION.getModule();
        String section = INTRUSION_DETECTION.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (IntrusionDetectionInfo) result;
    }

    @Override
    public void setIntrusionDetectEnabled(String state) {
        executeSetMethod(INTRUSION_DETECTION, "enabled", state);
    }

    @Override
    public void setTargetTrackEnabled(String state) {
        executeSetMethod(TARGET_TRACK, "enabled", state);
    }

    @Override
    public LineCrossingDetectionInfo getLineCrossingDetectionInfo() {
        String module = LINECROSSING_DETECTION.getModule();
        String section = LINECROSSING_DETECTION.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (LineCrossingDetectionInfo) result;
    }

    @Override
    public void setLineCrossingDetectEnabled(String state) {
        executeSetMethod(LINECROSSING_DETECTION, "enabled", state);
    }

    @Override
    public MotionDetection getMotionDetectionInfo() {
        String module = MOTION_DETECTION.getModule();
        String section = MOTION_DETECTION.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (MotionDetection) result;
    }

    @Override
    public void setMotionDetectEnabled(String state) {
        executeSetMethod(MOTION_DETECTION, "enabled", state);
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
    public void setMotionDetectEnhanced(String state) {
        executeSetMethod(MOTION_DETECTION, "enhanced", state);
    }

    @Override
    public PersonDetectionInfo getPersonDetectionInfo() {
        String module = PERSON_DETECTION.getModule();
        String section = PERSON_DETECTION.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (PersonDetectionInfo) result;
    }

    @Override
    public void setPersonDetectEnabled(String state) {
        executeSetMethod(PERSON_DETECTION, "enabled", state);
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
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (TamperDetectionInfo) result;
    }

    @Override
    public void setTamperDetectEnabled(String state) {
        executeSetMethod(TAMPER_DETECTION, "enabled", state);
    }

    @Override
    public PresetInfo getPresetInfo() {
        String module = PRESETS.getModule();
        String section = PRESETS.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (PresetInfo) result;
    }

    @Override
    public void gotoPreset(String id) {
        String module = GOTO_PRESETS.getModule();
        String section = GOTO_PRESETS.getSection();
        String method = GOTO_PRESETS.getMethod();
        String command = ApiUtils.createSingleCommandAsParam(method, module, section, "id", id);
        command = singleToMulti(command);
        sendSingleRequest(command);
    }

    @Override
    public TargetAutoTrackInfo getTargetAutoTrackInfo() {
        String module = TARGET_TRACK.getModule();
        String section = TARGET_TRACK.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (TargetAutoTrackInfo) result;
    }

    @Override
    public List<ApiMethodResult> getChangebleParameters() {
        List<ApiMethodResult> result = new ArrayList<>();
        List<ApiMethodTypes> listCommands = List.of(ApiMethodTypes.MOTION_DETECTION, ApiMethodTypes.INTRUSION_DETECTION,
                ApiMethodTypes.LINECROSSING_DETECTION, ApiMethodTypes.PERSON_DETECTION, ApiMethodTypes.TAMPER_DETECTION,
                ApiMethodTypes.MSG_ALARM_INFO, ApiMethodTypes.LAST_ALARM_INFO, ApiMethodTypes.MSG_PUSH_INFO,
                ApiMethodTypes.IMAGE_ROTATION_STATUS, ApiMethodTypes.LENS_MASK, ApiMethodTypes.LIGHT_FREQUENCY_INFO,
                ApiMethodTypes.LED_STATUS, ApiMethodTypes.TARGET_TRACK, ApiMethodTypes.PRESETS,
                ApiMethodTypes.SPEAKER_INFO, ApiMethodTypes.MICROPHONE_INFO);

        String multipleCommand = ApiUtils.createMultipleCommand(listCommands);
        ApiResponse response = sendMultipleRequest(multipleCommand);
        if (response.errorCode == 0 && response.result != null) {
            return processMultipleResponses(response);
        } else if (response.errorCode == ApiErrorCodes.ERROR_40401.getCode()) {
            // TODO: invalid token
            logger.error("{}: Invalid token, error code: {}", tag, response.errorCode);
            this.apiAuthData.stok = "";
        } else {
            logger.error("{}: Error in response, error code: {}", tag, response.errorCode);
            this.apiAuthData.stok = "";
        }
        return result;
    }
}
