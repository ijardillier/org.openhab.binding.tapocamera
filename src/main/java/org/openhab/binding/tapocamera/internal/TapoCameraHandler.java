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
package org.openhab.binding.tapocamera.internal;

import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_ALARM_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_ALARM_LIGHT_TYPE;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_ALARM_MODE;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_ALARM_TYPE;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_IMAGE_CONTRAST;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_IMAGE_FLIP;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_IMAGE_LENS_CORRECTION;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_IMAGE_LUMA;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_IMAGE_NIGHT_VISION;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_IMAGE_SATURATION;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_IMAGE_SHARPNESS;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_INTRUSION_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_LAST_ALARM_TIME;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_LAST_ALARM_TYPE;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_LED_STATUS;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_LINE_CROSSING_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_MANUAL_ALARM;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_MICROPHONE_VOLUME;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_MOTION_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_MOTION_DETECTION_SENSITIVITY;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_PERSON_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_PERSON_DETECTION_SENSITIVITY;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_PRIVACY_MODE;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_SPEAKER_VOLUME;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_TARGET_TRACK_ENABLED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.openhab.binding.tapocamera.internal.api.ApiErrorCodes;
import org.openhab.binding.tapocamera.internal.api.ApiException;
import org.openhab.binding.tapocamera.internal.api.ApiMethodTypes;
import org.openhab.binding.tapocamera.internal.api.TapoCameraApi;
import org.openhab.binding.tapocamera.internal.api.dto.detection.TargetAutoTrackInfo;
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
import org.openhab.binding.tapocamera.internal.api.dto.system.DeviceInfo;
import org.openhab.binding.tapocamera.internal.api.dto.system.LedStatus;
import org.openhab.binding.tapocamera.internal.api.dto.system.ModuleSpec;
import org.openhab.binding.tapocamera.internal.api.dto.system.NetworkInfo;
import org.openhab.binding.tapocamera.internal.api.response.ApiMethodResult;
import org.openhab.binding.tapocamera.internal.dto.CameraState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TapoCameraHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
@NonNullByDefault
public class TapoCameraHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(TapoCameraHandler.class);
    private @Nullable TapoCameraConfiguration config;
    private @Nullable Future<?> initJob;
    private @Nullable Future<?> pollingJob;
    private final TapoCameraApi api;
    private final Gson gson = new Gson();
    private CameraState cameraState = new CameraState();

    /**
     * Instantiates a new Tapo camera handler.
     *
     * @param thing the thing
     * @param api   the api
     */
    public TapoCameraHandler(Thing thing, TapoCameraApi api) {
        super(thing);
        this.api = api;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_MANUAL_ALARM.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                api.setManualAlarm(command.equals(OnOffType.ON) ? "start" : "stop");
            }
        }
        else if (CHANNEL_LED_STATUS.getName().equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            } else if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.setLedStatus(status);
                api.setLedStatus(status);
            }
        }
        else if (CHANNEL_PRIVACY_MODE.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                api.setLensMaskEnabled(status);
            }
        }
        else if (CHANNEL_ALARM_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getAlarmInfo().enabled = status;
                api.setAlarmInfoEnabled(status);
            }
        }
        else if (CHANNEL_ALARM_MODE.getName().equals(channelUID.getId())) {
            if (command instanceof StringType) {
                List<String> modes = new ArrayList<>();
                if (command.toString().equals("off")) {

                } else if (command.toString().equals("both")) {
                    modes.add("sound");
                    modes.add("light");
                } else if (command.toString().equals("sound")) {
                    modes.add("sound");
                } else if (command.toString().equals("light")) {
                    modes.add("light");
                }
                cameraState.getAlarmInfo().alarmMode.addAll(modes);
                api.setAlarmInfoMode(modes);
            }
        }
        else if (CHANNEL_ALARM_TYPE.getName().equals(channelUID.getId())) {
            if (command instanceof StringType) {
                String status = command.toString();
                cameraState.getAlarmInfo().alarmType = Integer.parseInt(status);
                api.setAlarmInfoType(status);
            }

        }
        else if (CHANNEL_MOTION_DETECTION_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getMotionDetection().enabled = status;
                api.setMotionDetectEnabled(status);
            }
        }
        else if (CHANNEL_MOTION_DETECTION_SENSITIVITY.getName().equals(channelUID.getId())) {
            if (command instanceof StringType) {
                String value = command.toString();
                cameraState.getMotionDetection().sensitivity = value;
                api.setMotionDetectSensitivity(value);
            }
        }
        else if (CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                Integer value = ((PercentType) command).intValue();
                cameraState.getMotionDetection().digitalSensitivity = value;
                api.setMotionDetectSensitivity(value);
            }
        }
        else if (CHANNEL_PERSON_DETECTION_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getPersonDetectionInfo().enabled = status;
                if (cameraState.getMotionDetection().enhanced == null || cameraState.getMotionDetection().enhanced.isEmpty()) {
                    api.setPersonDetectEnabled(status);
                } else {
                    api.setMotionDetectEnhanced(status);
                }
            }
        }
        else if (CHANNEL_PERSON_DETECTION_SENSITIVITY.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                Integer value = ((PercentType) command).intValue();
                cameraState.getPersonDetectionInfo().sensitivity = value;
                api.setPersonDetectSensitivity(value);
            }
        }
        else if (CHANNEL_LINE_CROSSING_DETECTION_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getLineCrossingDetection().enabled = status;
                api.setLineCrossingDetectEnabled(status);
            }
        }
        else if (CHANNEL_INTRUSION_DETECTION_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getIntrusionDetection().enabled = (status);
                api.setIntrusionDetectEnabled(status);
            }
        }
        else if (CHANNEL_TARGET_TRACK_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                api.setTargetTrackEnabled(status);
            }
        }
        else if (CHANNEL_SPEAKER_VOLUME.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                int volume = ((PercentType) command).intValue();
                cameraState.setSpeakerVolume(volume);
                api.setSpeakerVolume(volume);
            }
        }
        else if (CHANNEL_MICROPHONE_VOLUME.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                int volume = ((PercentType) command).intValue();
                cameraState.setMicrophoneVolume(volume);
                api.setMicrophoneVolume(volume);
            }
        }
        else if (CHANNEL_IMAGE_FLIP.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String state = command.equals(OnOffType.ON) ? "center" : "off";
                api.setImageFlip(state);
            }
        }
        else if (CHANNEL_IMAGE_LENS_CORRECTION.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String state = command.toString().toLowerCase();
                api.setImageLdc(state);
            }
        }
        else if (CHANNEL_IMAGE_NIGHT_VISION.getName().equals(channelUID.getId())) {
            if (command instanceof StringType) {
                String state = command.toString();
                api.setImageNightVision(state);
            }
        }
        else if (CHANNEL_IMAGE_CONTRAST.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                int state = ((PercentType) command).intValue();
                api.setImageContrast(state);
            }
        }
        else if (CHANNEL_IMAGE_SATURATION.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                int state = ((PercentType) command).intValue();
                api.setImageSaturation(state);
            }
        }
        else if (CHANNEL_IMAGE_SHARPNESS.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                int state = ((PercentType) command).intValue();
                api.setImageSharpness(state);
            }
        }
        else if (CHANNEL_IMAGE_LUMA.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                int state = ((PercentType) command).intValue();
                api.setImageLuma(state);
            }
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(TapoCameraConfiguration.class);
        if (config == null) {
            updateStatus(ThingStatus.UNINITIALIZED, ThingStatusDetail.CONFIGURATION_ERROR);
        } else {
            String hostname = config.hostname;
            api.setHostname(hostname);
            updateStatus(ThingStatus.UNKNOWN);

            initJob = connectCamera(0);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        Future<?> job = initJob;
        if (job != null) {
            job.cancel(true);
            initJob = null;
        }
        job = pollingJob;
        if (job != null) {
            job.cancel(true);
            pollingJob = null;
        }
    }

    private void getSupportedFeatures(ModuleSpec moduleSpec) {
        cameraState.setHasMotor(moduleSpec.motor != null && moduleSpec.motor == 1);
        cameraState.setHasTargetTrack(moduleSpec.target_track != null && moduleSpec.target_track == 1);
        // cameraState.setHasBabyCryDetection();
        cameraState.setHasSmartDetection(moduleSpec.smart_detection != null && moduleSpec.smart_detection == 1);
        cameraState.setHasLineCrossingDetection(moduleSpec.linecrossing_detection != null && moduleSpec.linecrossing_detection == 1);
        cameraState.setHasIntrusionDetection(moduleSpec.intrusion_detection != null && moduleSpec.intrusion_detection == 1);
        cameraState.setHasAudioExceptionDetection(moduleSpec.audioexception_detection != null && moduleSpec.audioexception_detection == 1);

    }

    private Future<?> connectCamera(int wait) {
        logger.warn("Try connect after: {} sec", wait);
        return scheduler.schedule(() -> {
            updateStatus(ThingStatus.OFFLINE);
            boolean thingReachable = deviceAuth();
            if (thingReachable) {
                updateStatus(ThingStatus.ONLINE);
                DeviceInfo deviceInfo = api.getDeviceInfo();
                cameraState.setFriendlyName(deviceInfo.basic.deviceAlias);
                logger.debug("{}: received: {}", cameraState.getFriendlyName(), deviceInfo);
                NetworkInfo networkInfo = api.getNetworkInfo();
                logger.debug("{}: received: {}", cameraState.getFriendlyName(), networkInfo.toString());
                ModuleSpec moduleSpec = api.getModuleSpec();
                logger.debug("{}: received: {}", cameraState.getFriendlyName(), moduleSpec.toString());
                getSupportedFeatures(moduleSpec);

                setThingProperties(deviceInfo);
                updateThingProperties(networkInfo);
                pollingJob = getCameraParameters(config.pollingInterval);
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
                reconnect();
            }
        }, wait, TimeUnit.SECONDS);
    }

    private void reconnect() {
        logger.debug("Try to reconnect");
        Future<?> job = initJob;
        if (job != null) {
            job.cancel(true);
            initJob = null;
        }
        initJob = connectCamera(config.reconnectInterval);
    }

    private Boolean deviceAuth() {
        String pass = config.cloudPassword.isEmpty() ? config.password : config.cloudPassword;
        try {
            return api.auth(config.username, pass);
        } catch (ApiException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            reconnect();
            throw new RuntimeException(e);
        }
    }

    private void setThingProperties(DeviceInfo device) {
        Map<String, String> properties = new HashMap<>();
        properties.put("Friendly Name:", device.basic.deviceAlias);
        properties.put("Device Model:", device.basic.deviceModel);
        properties.put("Manufacturer:", device.full.manufacturerName);
        properties.put("Software Version:", device.basic.swVersion);
        properties.put("Hardware Version:", device.basic.hwVersion);
        properties.put("Device Identifier:", device.basic.devId);
        properties.put("MAC Address:", device.full.mac);
        if (device.full.sensor != null) properties.put("Sensor:", device.full.sensor);
        if (device.full.lensName != null) properties.put("Lens Name:", device.full.lensName);
        // properties.put("Call Support:", Boolean.TRUE.equals(device.basic.isCal) ? "Supported" : "Not Supported");
        updateProperties(properties);
    }

    private void updateThingProperties(NetworkInfo network) {
        updateProperty("IP Address:", network.ipaddr);
        updateProperty("Link Type:", network.connectionType.linkType);
        if (network.connectionType.ssid != null) updateProperty("SSID:", network.connectionType.ssid);
        if (network.connectionType.rssi != null) updateProperty("RSSI:", String.valueOf(network.connectionType.rssi));
    }

    private Future<?> getCameraParameters(Integer interval) {
        return scheduler.scheduleWithFixedDelay(this::pollingCamera, interval, interval, TimeUnit.SECONDS);
    }

    private void pollingCamera() {
        logger.debug("get camera parameters");
        try {
            if (!api.isAuth()) {
                reconnect();
            } else {
                updateStatus(ThingStatus.ONLINE);
            }

            List<ApiMethodResult> apiMethodsResultList = api.getChangebleParameters();
            apiMethodsResultList.forEach(param -> {
                processAllResults(param);
            });

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Sets device status.
     *
     * @param errorCode the error code
     */
    public void setDeviceStatus(Integer errorCode) {
        String msg = ApiErrorCodes.getErrorByCode(errorCode).getMessage();
        if (errorCode.equals(ApiErrorCodes.ERROR_40401.getCode())) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    String.format("%d - %s", errorCode, msg));
        }
    }

    /**
     * Sets device status.
     *
     * @param message the message
     */
    public void setDeviceStatus(String message) {
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, String.format("%s", message));
    }

    private void processAllResults(ApiMethodResult object) {
         // ApiMethodTypes.getClassByModuleAndSection()
        Optional<ApiMethodTypes> apipMethod = Arrays.asList(ApiMethodTypes.values()).stream()
                .filter(m -> {
                    return object.method.equals(m.getMethod()) &&
                            object.result.has(m.getModule()) &&
                            object.result.getAsJsonObject(m.getModule()).has(m.getSection());
                })
                //.map(ApiMethodTypes::getClazz)
                .findFirst();
      if ( !apipMethod.isPresent() ) {
          return;
      }
      Class<?> clazz = apipMethod.get().getClazz();
      String module = apipMethod.get().getModule();
      String section = apipMethod.get().getSection();
      JsonElement newObj = object.result.getAsJsonObject(module).get(section);
      Object data = gson.fromJson(newObj, clazz);

        if (data instanceof LedStatus) {
            // get led status
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            String ledStatus = ((LedStatus) data).enabled;
            cameraState.setLedStatus(ledStatus);
            updateState(CHANNEL_LED_STATUS.getName(), OnOffType.from(ledStatus.toUpperCase()));
        }
        // audio config
        else if (data instanceof AudioSpeakerInfo) {
            // speaker status
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            int volume = ((AudioSpeakerInfo) data).volume;
            cameraState.setSpeakerVolume(volume);
            updateState(CHANNEL_SPEAKER_VOLUME.getName(), new PercentType(volume));
        } else if (data instanceof AudioMicrophoneInfo) {
            // microphone status
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            int volume = ((AudioMicrophoneInfo) data).volume;
            cameraState.setMicrophoneVolume(volume);
            updateState(CHANNEL_MICROPHONE_VOLUME.getName(), new PercentType(volume));
        }
        // detections
        else if (data instanceof MotionDetection) {
            // motion detection
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            MotionDetection motionDetection = (MotionDetection) data;
            cameraState.setMotionDetection(motionDetection);
            updateState(CHANNEL_MOTION_DETECTION_ENABLED.getName(), OnOffType.from(motionDetection.enabled.toUpperCase()));
            updateState(CHANNEL_MOTION_DETECTION_SENSITIVITY.getName(), new StringType(motionDetection.sensitivity));
            int digitalSensitivity = motionDetection.digitalSensitivity;
            updateState(CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY.getName(), new PercentType(digitalSensitivity));
            if (motionDetection.enhanced != null) {
                cameraState.setHasSmartDetection(true);
            }
        } else if (data instanceof PersonDetectionInfo) {
            // person detection
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            PersonDetectionInfo personDetectionInfo = (PersonDetectionInfo) data;
            cameraState.setPersonDetectionInfo(personDetectionInfo);
            updateState(CHANNEL_PERSON_DETECTION_ENABLED.getName(), OnOffType.from(personDetectionInfo.enabled.toUpperCase()));
            int digitalSensitivity = personDetectionInfo.sensitivity;
            updateState(CHANNEL_PERSON_DETECTION_SENSITIVITY.getName(), new PercentType(digitalSensitivity));
        } else if (data instanceof LineCrossingDetectionInfo) {
            // line crossing detection
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            LineCrossingDetectionInfo lineCrossingDetection = (LineCrossingDetectionInfo) data;
            cameraState.setLineCrossingDetection(lineCrossingDetection);
            updateState(CHANNEL_LINE_CROSSING_DETECTION_ENABLED.getName(),
                    OnOffType.from(lineCrossingDetection.enabled.toUpperCase()));
        } else if (data instanceof IntrusionDetectionInfo) {
            // intrusion detection
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            IntrusionDetectionInfo intrusionDetection = (IntrusionDetectionInfo) data;
            cameraState.setIntrusionDetection(intrusionDetection);
            updateState(CHANNEL_INTRUSION_DETECTION_ENABLED.getName(), OnOffType.from(intrusionDetection.enabled.toUpperCase()));
        } else if (data instanceof TamperDetectionInfo) {
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            TamperDetectionInfo tamperDetection = (TamperDetectionInfo) data;
            // TODO: update tamper channels
        } else if (data instanceof TargetAutoTrackInfo) {
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            TargetAutoTrackInfo targetAutoTrackInfo = (TargetAutoTrackInfo) data;
            updateState(CHANNEL_TARGET_TRACK_ENABLED.getName(), OnOffType.from(targetAutoTrackInfo.enabled.toUpperCase()));
        }
        // image info
        else if (data instanceof ImageCommon) {
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            updateState(CHANNEL_IMAGE_NIGHT_VISION.getName(), new StringType(((ImageCommon) data).nightMode));
            updateState(CHANNEL_IMAGE_CONTRAST.getName(), new PercentType(((ImageCommon) data).contrast));
            updateState(CHANNEL_IMAGE_SHARPNESS.getName(), new PercentType(((ImageCommon) data).sharpness));
            updateState(CHANNEL_IMAGE_SATURATION.getName(), new PercentType(((ImageCommon) data).saturation));
            updateState(CHANNEL_IMAGE_LUMA.getName(), new PercentType(((ImageCommon) data).luma));
        } else if (data instanceof ImageSwitch) {
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            updateState(CHANNEL_IMAGE_FLIP.getName(), OnOffType.from(((ImageSwitch) data).flipType.equals("center")));
            updateState(CHANNEL_IMAGE_LENS_CORRECTION.getName(), OnOffType.from(((ImageSwitch) data).lensDistortionCorrection.toUpperCase()));
        }  else if (data instanceof LensMaskInfo) {
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            updateState(CHANNEL_PRIVACY_MODE.getName(), OnOffType.from(((LensMaskInfo) data).enabled.toUpperCase()));
        }
        // message alarm
        else if (data instanceof LastAlarmInfo) {
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            updateState(CHANNEL_LAST_ALARM_TYPE.getName(), new StringType(((LastAlarmInfo) data).lastAlarmType));
            Long timestamp = ((LastAlarmInfo) data).lastAlarmTime.longValue();
            updateState(CHANNEL_LAST_ALARM_TIME.getName(), new DecimalType(timestamp));
        } else if (data instanceof MsgAlarmInfo) {
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data);
            // alarm mode
            MsgAlarmInfo alarmInfo = (MsgAlarmInfo) data;
            cameraState.setAlarmInfo(alarmInfo);
            updateState(CHANNEL_ALARM_ENABLED.getName(), OnOffType.from(alarmInfo.enabled.toUpperCase()));
            updateState(CHANNEL_ALARM_TYPE.getName(), new StringType(alarmInfo.alarmType.toString()));
            updateState(CHANNEL_ALARM_LIGHT_TYPE.getName(), new StringType(alarmInfo.lightType.toString()));
            if (alarmInfo.alarmMode.isEmpty()) {
                updateState(CHANNEL_ALARM_MODE.getName(), new StringType("off"));
            } else if (alarmInfo.alarmMode.size() == 2) {
                updateState(CHANNEL_ALARM_MODE.getName(), new StringType("both"));
            } else if (alarmInfo.alarmMode.size() == 1) {
                updateState(CHANNEL_ALARM_MODE.getName(), new StringType(alarmInfo.alarmMode.get(0)));
            }
        } else if (data instanceof MsgPushInfo) {
            logger.debug("{}: received: {}", cameraState.getFriendlyName(), data.toString());
        }
    }
}
