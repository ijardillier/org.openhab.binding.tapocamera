/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
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

import static org.openhab.binding.tapocamera.internal.api.ApiMethodTypes.*;
import static org.openhab.binding.tapocamera.internal.api.utils.ApiUtils.createMultipleCommand;
import static org.openhab.binding.tapocamera.internal.api.utils.ApiUtils.singleToMulti;

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
import org.openhab.binding.tapocamera.internal.api.dto.PresetInfo;
import org.openhab.binding.tapocamera.internal.api.dto.alarm.LastAlarmInfo;
import org.openhab.binding.tapocamera.internal.api.dto.alarm.MsgAlarmInfo;
import org.openhab.binding.tapocamera.internal.api.dto.alarm.MsgPushInfo;
import org.openhab.binding.tapocamera.internal.api.dto.audio.AudioMicrophoneInfo;
import org.openhab.binding.tapocamera.internal.api.dto.audio.AudioSpeakerInfo;
import org.openhab.binding.tapocamera.internal.api.dto.detection.*;
import org.openhab.binding.tapocamera.internal.api.dto.image.ImageCommon;
import org.openhab.binding.tapocamera.internal.api.dto.image.ImageSwitch;
import org.openhab.binding.tapocamera.internal.api.dto.image.LensMaskInfo;
import org.openhab.binding.tapocamera.internal.api.dto.system.*;
import org.openhab.binding.tapocamera.internal.api.response.ApiMethodResult;
import org.openhab.binding.tapocamera.internal.api.response.ApiResponse;
import org.openhab.binding.tapocamera.internal.api.utils.ApiUtils;
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
    private static final String TAPO_USER_AGENT = "Tapo CameraClient Android";
    private String hostname = "";
    private String token = "";
    private String lsk = "";
    private String ivb = "";
    private String passwordHash = "";
    private Long startSeq = -1L;
    private Integer userId = -1;
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

    @Override
    public void setHostname(String hostname) {
        this.hostname = "https://" + hostname;
    }

    private void setHeaders(Request request) {
        request.timeout(60, TimeUnit.SECONDS);
        request.header(HttpHeader.USER_AGENT, null);
        request.header(HttpHeader.USER_AGENT, TAPO_USER_AGENT);
        request.header(HttpHeader.CONTENT_TYPE, "application/json; charset=UTF-8");
        request.header(HttpHeader.CONNECTION, "close");
        request.header("requestByApp", "true");
        request.header(HttpHeader.ACCEPT, "application/json");
        request.header(HttpHeader.ACCEPT_ENCODING, null);
        request.header(HttpHeader.ACCEPT_ENCODING, "gzip, deflate");
    }

    @Override
    public Boolean isAuth() {
        return !token.isEmpty();
    }

    private Boolean getIsSecureConnection(String username) throws ApiException {
        Boolean result = false;
        Request request = httpClient.newRequest(hostname);
        setHeaders(request);
        request.method(HttpMethod.POST);
        JsonObject jsonParams = new JsonObject();
        jsonParams.addProperty("encrypt_type", "3");
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
            logger.info("{}: response: {}", tag, response);

            if (response.errorCode == ApiErrorCodes.ERROR_40413.getCode()) {
                JsonObject data = (JsonObject) response.result.get("data");
                if (data.has("encrypt_type")) {
                    List<String> list = new ArrayList<>();
                    logger.info("{}: data: {}", tag, data);
                    return true;
                }
            }
        } catch (TimeoutException e) {
            logger.error("{}: TimeoutException: {}", tag, e.getMessage());
            throw new ApiException(String.format("TimeoutException: %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.error("{}: InterruptedException: {}", tag, e.getMessage());
            throw new ApiException(String.format("InterruptedException: %s", e.getMessage()));
        } catch (ExecutionException e) {
            logger.error("{}: ExecutionException: {}", tag, e.getMessage());
            throw new ApiException(String.format("ExecutionException: %s", e.getMessage()));
        }
        return false;
    }

    private Boolean validateDeviceConfirm(String passwordHash, String nonce, String deviceConfirm) {
        String hashedNoncesWithSHA256 = ApiUtils.getPasswordHashSHA256(cnonce + passwordHash + nonce);
        return (deviceConfirm.equals(hashedNoncesWithSHA256 + nonce + cnonce));
    }

    @Override
    public Boolean auth(String username, String password) throws ApiException {
        Boolean result = false;

        isSecureConnection = getIsSecureConnection(username);

        Request request = httpClient.newRequest(hostname);
        logger.trace("{}: hostname: {}", tag, hostname);
        setHeaders(request);
        request.method(HttpMethod.POST);

        //

        JsonObject jsonParams = new JsonObject();

        if (isSecureConnection) {
            logger.debug("{}: Connection is secure", tag);
            // SecureRandom csprng = new SecureRandom();
            // byte[] randomBytes = new byte[8];
            // csprng.nextBytes(randomBytes);
            // cnonce = "9B5F6CE8";
            // nonce = "18D1CD86D1AE9934";

            passwordHash = ApiUtils.getPasswordHashSHA256(password);

            jsonParams.addProperty("cnonce", cnonce);
            jsonParams.addProperty("encrypt_type", "3");
            // jsonParams.addProperty("digest_passwd", digestPassword2);
            jsonParams.addProperty("username", username);
        } else {
            logger.debug("{}: Connection is insecure", tag);
            passwordHash = ApiUtils.getPasswordHash(password);
            jsonParams.addProperty("hashed", Boolean.TRUE);
            jsonParams.addProperty("password", passwordHash);
            jsonParams.addProperty("username", username);
        }

        JsonObject jsonAuth = new JsonObject();
        jsonAuth.addProperty("method", "login");
        jsonAuth.add("params", jsonParams);

        String body = jsonAuth.toString();
        logger.trace("{}: body: {}", tag, body);
        request.content(new StringContentProvider(body));
        try {
            ContentResponse contentResponse = request.send();
            JsonElement json = JsonParser.parseString(contentResponse.getContentAsString());

            ApiResponse response = gson.fromJson(json, ApiResponse.class);
            logger.trace("{}: response: {}", tag, response);

            if (isSecureConnection) {
                // get nonce and make new request
                logger.debug("{}: Processing secure response", tag);
                if (response.result.has("data")) {
                    JsonObject data = response.result.getAsJsonObject("data");
                    if (data.has("nonce") && data.has("device_confirm")) {
                        nonce = data.get("nonce").getAsString();
                        logger.trace("{}: received nonce: {}", tag, nonce);
                        String deviceConfirm = data.get("device_confirm").getAsString();
                        Boolean validated = validateDeviceConfirm(passwordHash, nonce, deviceConfirm);
                        if (validated) {
                            logger.trace("{}: sha256: {}", tag, passwordHash);
                            logger.trace("{}: cnonce: {}", tag, cnonce);
                            logger.trace("{}: nonce: {}", tag, nonce);

                            String toNewPass = passwordHash + cnonce + nonce;
                            logger.trace("{}: sha256 + cnonce + nonce: {}", tag, toNewPass);

                            String digestPassword = ApiUtils.getPasswordHashSHA256(toNewPass);
                            logger.trace("{}: sha256-2: {}", tag, digestPassword);

                            String digestPassword2 = digestPassword + cnonce + nonce;
                            logger.trace("{}: sha256-2 + cnonce + nonce: {}", tag, digestPassword2);

                            // jsonParams.addProperty("cnonce", ApiUtils.bytesToHex(randomBytes));
                            jsonParams.addProperty("cnonce", cnonce);
                            jsonParams.addProperty("encrypt_type", "3");
                            jsonParams.addProperty("digest_passwd", digestPassword2);
                            jsonParams.addProperty("username", username);

                            jsonAuth = new JsonObject();
                            jsonAuth.addProperty("method", "login");
                            jsonAuth.add("params", jsonParams);

                            body = jsonAuth.toString();
                            logger.debug("{}: body: {}", tag, body);
                            Request request2 = httpClient.newRequest(hostname);
                            setHeaders(request2);
                            request2.method(HttpMethod.POST);
                            request2.content(new StringContentProvider(body));
                            try {
                                ContentResponse contentResponse2 = request2.send();
                                JsonElement json2 = JsonParser.parseString(contentResponse2.getContentAsString());

                                ApiResponse response2 = gson.fromJson(json2, ApiResponse.class);
                                logger.debug("{}: response2: {}", tag, response2);
                                // response: ApiResponse{errorCode=0,
                                // result={"stok":"4b67dc6f7035104ff9922edef174164f","user_group":"root","start_seq":535}}
                                if (response2.errorCode == 0 && response2.result.has("stok")
                                        && response2.result.has("start_seq")) {
                                    token = response2.result.get("stok").getAsString();
                                    logger.debug("{}: token: {}", tag, token);
                                    startSeq = response2.result.get("start_seq").getAsLong();
                                    logger.debug("{}: start_seq: {}", tag, startSeq);
                                    lsk = generateEncryptionToken("lsk", passwordHash, nonce);
                                    logger.debug("{}: lsk: {}", tag, lsk);
                                    ivb = generateEncryptionToken("ivb", passwordHash, nonce);
                                    logger.debug("{}: ivb: {}", tag, ivb);
                                    result = true;
                                }

                            } catch (TimeoutException e) {
                                logger.error("{}: TimeoutException: {}", tag, e.getMessage());
                                this.token = "";
                                throw new ApiException(String.format("TimeoutException: %s", e.getMessage()));
                            } catch (InterruptedException e) {
                                logger.error("{}: InterruptedException: {}", tag, e.getMessage());
                                throw new ApiException(String.format("InterruptedException: %s", e.getMessage()));
                            } catch (ExecutionException e) {
                                logger.error("{}: ExecutionException: {}", tag, e.getMessage());
                                this.token = "";
                                throw new ApiException(String.format("ExecutionException: %s", e.getMessage()));
                            }
                        }
                    } else {
                        logger.error("{}: Empty nonce or device_confirm, error code: {}", tag, response.errorCode);
                        this.token = "";
                        throw new ApiException(String.format("error code: %d", response.errorCode));
                    }
                } else {
                    logger.error("{}: Invalid token, error code: {}", tag, response.errorCode);
                    this.token = "";
                    throw new ApiException(String.format("error code: %d", response.errorCode));
                }
            } else {
                if (response.errorCode == 0 && response.result.has("stok")) {
                    token = response.result.get("stok").getAsString();
                    if (isAuth()) {
                        userId = getUserId();
                        result = true;
                    }
                } else {
                    // TODO: log.error && throw new Exception
                    if (response.errorCode == ApiErrorCodes.ERROR_40401.getCode()) {
                        logger.error("{}: Invalid token, error code: {}", tag, response.errorCode);
                        this.token = "";
                    } else {
                        logger.error("{}: Error in response, error code: {}", tag, response.errorCode);
                        throw new ApiException(String.format("error code: %d", response.errorCode));
                    }
                }
            }

        } catch (TimeoutException e) {
            logger.error("{}: TimeoutException: {}", tag, e.getMessage());
            this.token = "";
            throw new ApiException(String.format("TimeoutException: %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.error("{}: InterruptedException: {}", tag, e.getMessage());
            throw new ApiException(String.format("InterruptedException: %s", e.getMessage()));
        } catch (ExecutionException e) {
            logger.error("{}: ExecutionException: {}", tag, e.getMessage());
            this.token = "";
            throw new ApiException(String.format("ExecutionException: %s", e.getMessage()));
        }
        return result;
    }

    private String generateEncryptionToken(String typeToken, String hashedPassword, String nonce) {
        String hashedKey = ApiUtils.getPasswordHashSHA256(cnonce + hashedPassword + nonce);
        return ApiUtils.getPasswordHashSHA256(typeToken + cnonce + nonce + hashedKey, 32);
    }

    private String generateTag(String data, String hashedPassword, String cnonce) {
        String hashedKey = ApiUtils.getPasswordHashSHA256(hashedPassword + cnonce);
        return ApiUtils.getPasswordHashSHA256(hashedKey + data + startSeq.toString());
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
            logger.error("{}: TapoCameraApi error {}", tag, response.errorCode);
        }
        return result;
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

        String tapoTag = generateTag(encryptedRequest.toString(), passwordHash, cnonce);
        logger.debug("{}: Tapo tag: {}", tag, tapoTag);

        originRequest.header("Tapo_tag", tapoTag);

        startSeq += 1;
    }

    @Override
    public ApiResponse sendMultipleRequest(String token, String data) {
        Request request = httpClient.newRequest(hostname + "/stok=" + token + "/ds");
        setHeaders(request);
        request.method(HttpMethod.POST);
        if (isSecureConnection) {
            logger.debug("{}: sendMultipleRequest data: {}", tag, data);
            makeEncryptedRequest(request, data, startSeq, passwordHash, lsk, ivb);
        } else {
            request.content(new StringContentProvider(data));
        }
        try {
            ContentResponse contentResponse = request.send();
            ApiResponse response = gson.fromJson(contentResponse.getContentAsString(), ApiResponse.class);
            if (response.errorCode == 0) {
                if (isSecureConnection) {
                    String result = response.result.get("response").getAsString();
                    result = ApiUtils.decryptResponse(result, lsk, ivb);
                    logger.debug("{}: decrypted response: {}", tag, result);
                    JsonObject json = JsonParser.parseString(result).getAsJsonObject();
                    response.result = json.getAsJsonObject("result");
                }
                return response;
            } else {
                // TODO: log.error && throw new Exception
                logger.error("{}: Error in response, error code: {}", tag, response.errorCode);
                this.token = "";
                return new ApiResponse();
            }
        } catch (TimeoutException e) {
            this.token = "";
            logger.error("{}: TimeoutException: {}", tag, e.getMessage());
            return null;
        } catch (InterruptedException e) {
            logger.error("{}: InterruptedException: {}", tag, e.getMessage());
            return null;
        } catch (ExecutionException e) {
            this.token = "";
            logger.error("{}: ExecutionException: {}", tag, e.getMessage());
            return null;
        }
    }

    @Override
    public Object sendSingleRequest(String token, String data) {
        Request request = httpClient.newRequest(hostname + "/stok=" + token + "/ds");
        setHeaders(request);
        request.method(HttpMethod.POST);
        if (isSecureConnection) {
            logger.debug("{}: sendSingleRequest data: {}", tag, data);
            makeEncryptedRequest(request, data, startSeq, passwordHash, lsk, ivb);
        } else {
            request.content(new StringContentProvider(data));
        }

        try {
            ContentResponse contentResponse = request.send();
            JsonObject response = JsonParser.parseString(contentResponse.getContentAsString()).getAsJsonObject();
            if (response.has("error_code") && response.get("error_code").getAsInt() == 0) {
                if (isSecureConnection) {
                    String result = response.get("result").getAsJsonObject().get("response").getAsString();
                    result = ApiUtils.decryptResponse(result, lsk, ivb);
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
                this.token = "";
                return new Object();
            }
        } catch (TimeoutException e) {
            logger.error("{}: TimeoutException: {}", tag, e.getMessage());
            this.token = "";
            return null;
        } catch (InterruptedException e) {
            logger.error("{}: InterruptedException: {}", tag, e.getMessage());
            return null;
        } catch (ExecutionException e) {
            logger.error("{}: ExecutionException: {}", tag, e.getMessage());
            this.token = "";
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
            JsonObject obj = (JsonObject) sendSingleRequest(token, command);
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
            result = (NetworkInfo) gson.fromJson(response.get(module).getAsJsonObject().get(section),
                    WAN_INFO.getClazz());

            command = createMultipleCommand(List.of(CONNECTION_TYPE));
            ApiResponse response1 = sendMultipleRequest(token, command);
            if (response1.errorCode == 0) {
                List<ApiMethodResult> methodResults = processMultipleResponses(response1);
                result.connectionType = gson.fromJson(methodResults.get(0).result, ConnectionType.class);
                // result.link_type = methodResults.get(0).result.get("link_type").getAsString();
            }
            return result;
        }
        return new NetworkInfo();
    }

    @Override
    public ModuleSpec getModuleSpec() {
        String module = MODULES_SPEC.getModule();
        String section = MODULES_SPEC.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
        Object result = processSingleResponse(singleResponse, module, section);
        return (ModuleSpec) result;
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
        String module = IMAGE_ROTATION_STATUS.getModule();
        String section = IMAGE_ROTATION_STATUS.getSection();
        String singleCommand = ApiUtils.createSingleCommand("get", module, List.of(section));
        Object singleResponse = sendSingleRequest(token, singleCommand);
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
        Object singleResponse = sendSingleRequest(token, singleCommand);
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
        executeDoMethod(MSG_ALARM_MANUAL, "action", state);
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
        Object singleResponse = sendSingleRequest(token, singleCommand);
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
        Object singleResponse = sendSingleRequest(token, singleCommand);
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
        Object singleResponse = sendSingleRequest(token, singleCommand);
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
        Object singleResponse = sendSingleRequest(token, singleCommand);
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
        Object singleResponse = sendSingleRequest(token, singleCommand);
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
        sendSingleRequest(token, command);
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
        List<ApiMethodTypes> listCommands = List.of(ApiMethodTypes.MOTION_DETECTION, ApiMethodTypes.INTRUSION_DETECTION,
                ApiMethodTypes.LINECROSSING_DETECTION, ApiMethodTypes.PERSON_DETECTION, ApiMethodTypes.TAMPER_DETECTION,
                ApiMethodTypes.MSG_ALARM_INFO, ApiMethodTypes.LAST_ALARM_INFO, ApiMethodTypes.MSG_PUSH_INFO,
                ApiMethodTypes.IMAGE_ROTATION_STATUS, ApiMethodTypes.LENS_MASK, ApiMethodTypes.LIGHT_FREQUENCY_INFO,
                ApiMethodTypes.LED_STATUS, ApiMethodTypes.TARGET_TRACK, ApiMethodTypes.PRESETS,
                ApiMethodTypes.SPEAKER_INFO, ApiMethodTypes.MICROPHONE_INFO);

        String multipleCommand = ApiUtils.createMultipleCommand(listCommands);
        ApiResponse response = sendMultipleRequest(token, multipleCommand);
        if (response.errorCode == 0 && response.result != null) {
            return processMultipleResponses(response);
        } else if (response.errorCode == ApiErrorCodes.ERROR_40401.getCode()) {
            // TODO: invalid token
            logger.error("{}: Invalid token, error code: {}", tag, response.errorCode);
            this.token = "";
        } else {
            logger.error("{}: Error in response, error code: {}", tag, response.errorCode);
            this.token = "";
        }
        return result;
    }
}
