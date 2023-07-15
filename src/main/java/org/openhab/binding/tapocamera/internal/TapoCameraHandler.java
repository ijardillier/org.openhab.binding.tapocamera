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
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_INTRUSION_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_LED_STATUS;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_LINE_CROSSING_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_MANUAL_ALARM;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_MICROPHONE_VOLUME;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_MOTION_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_MOTION_DETECTION_SENSITIVITY;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_PERSON_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_PERSON_DETECTION_SENSITIVITY;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_SPEAKER_VOLUME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
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

import org.openhab.binding.tapocamera.internal.api.ApiErrorCodes;
import org.openhab.binding.tapocamera.internal.api.ApiException;
import org.openhab.binding.tapocamera.internal.api.v1.TapoCameraApi_v1;
import org.openhab.binding.tapocamera.internal.api.v1.response.Old_ApiDeviceInfo;
import org.openhab.binding.tapocamera.internal.api.v2.TapoApi;
import org.openhab.binding.tapocamera.internal.api.v2.dto.alarm.LastAlarmInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.alarm.MsgAlarmInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.alarm.MsgPushInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.audio.AudioMicrophoneInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.audio.AudioSpeakerInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.detection.IntrusionDetectionInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.detection.LineCrossingDetectionInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.detection.MotionDetection;
import org.openhab.binding.tapocamera.internal.api.v2.dto.detection.PersonDetectionInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.detection.TamperDetectionInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.image.ImageCommon;
import org.openhab.binding.tapocamera.internal.api.v2.dto.image.ImageSwitch;
import org.openhab.binding.tapocamera.internal.api.v2.dto.image.LensMaskInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.DeviceInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.LedStatus;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.NetworkInfo;
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
    //private final TapoCameraApi_v1 apiV1;
    private final TapoApi apiV2;

    private CameraState cameraState = new CameraState();

    public TapoCameraHandler(Thing thing, TapoCameraApi_v1 apiV1, TapoApi apiV2) {
        super(thing);
        //this.apiV1 = apiV1;
        this.apiV2 = apiV2;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_MANUAL_ALARM.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                apiV2.setManualAlarm(command.equals(OnOffType.ON) ? "start" : "stop");
            }
        }
        else if (CHANNEL_LED_STATUS.getName().equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            } else if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.setLedStatus(status);
                apiV2.setLedStatus(status);
            }
        }
        else if (CHANNEL_ALARM_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getAlarmInfo().enabled = status;
                apiV2.setAlarmInfoEnabled(status);
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
                apiV2.setAlarmInfoMode(modes);
            }
        }
        else if (CHANNEL_ALARM_TYPE.getName().equals(channelUID.getId())) {
            if (command instanceof StringType) {
                String status = command.toString();
                cameraState.getAlarmInfo().alarmType = Integer.parseInt(status);
                apiV2.setAlarmInfoType(status);
            }

        }
        else if (CHANNEL_MOTION_DETECTION_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getMotionDetection().enabled = status;
                apiV2.setMotionDetectEnabled(status);
            }
        }
        else if (CHANNEL_MOTION_DETECTION_SENSITIVITY.getName().equals(channelUID.getId())) {
            if (command instanceof StringType) {
                String value = command.toString();
                cameraState.getMotionDetection().sensitivity = value;
                apiV2.setMotionDetectSensitivity(value);
            }
        }
        else if (CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                Integer value = ((PercentType) command).intValue() * 10;
                cameraState.getMotionDetection().digitalSensitivity = value;
                apiV2.setMotionDetectSensitivity(value);
            }
        }
        else if (CHANNEL_PERSON_DETECTION_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getPersonDetectionInfo().enabled = status;
                apiV2.setPersonDetectEnabled(status);
            }
        }
        else if (CHANNEL_PERSON_DETECTION_SENSITIVITY.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                Integer value = ((PercentType) command).intValue() * 10;
                cameraState.getPersonDetectionInfo().sensitivity = value;
                apiV2.setPersonDetectSensitivity(value);
            }
        }
        else if (CHANNEL_LINE_CROSSING_DETECTION_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getLineCrossingDetection().enabled = status;
                apiV2.setLineCrossingDetectEnabled(status);
            }
        }
        else if (CHANNEL_INTRUSION_DETECTION_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getIntrusionDetection().enabled = (status);
                apiV2.setIntrusionDetectEnabled(status);
            }
        }
        else if (CHANNEL_SPEAKER_VOLUME.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                int volume = ((PercentType) command).intValue();
                cameraState.setSpeakerVolume(volume);
                apiV2.setSpeakerVolume(volume);
            }
        }
        else if (CHANNEL_MICROPHONE_VOLUME.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                int volume = ((PercentType) command).intValue();
                cameraState.setMicrophoneVolume(volume);
                apiV2.setMicrophoneVolume(volume);
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
            apiV2.setHostname(hostname);
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

    private Future<?> connectCamera(int wait) {
        logger.warn("Try connect after: {} sec", wait);
        return scheduler.schedule(() -> {
            updateStatus(ThingStatus.OFFLINE);
            boolean thingReachable = deviceAuth();
            if (thingReachable) {
                updateStatus(ThingStatus.ONLINE);
                DeviceInfo deviceInfo = apiV2.getDeviceInfo();
                NetworkInfo networkInfo = apiV2.getNetworkInfo();
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
//      apiV1.old_setHostname(config.hostname);
        String pass = config.cloudPassword.isEmpty() ? config.password : config.cloudPassword;
        try {
//          result = apiV1.old_auth(config.username, pass);
            return apiV2.auth(config.username, pass);
        } catch (ApiException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            reconnect();
            throw new RuntimeException(e);
        }
    }

    private void setThingProperties(Old_ApiDeviceInfo device) {
        Map<String, String> properties = new HashMap<>();
        properties.put("Friendly Name:", device.basicInfo.friendlyName);
        properties.put("Device model:", device.basicInfo.deviceModel);
        properties.put("Software version:", device.basicInfo.swVersion);
        properties.put("Hardware version:", device.basicInfo.hwVersion);
        properties.put("Device identifier:", device.basicInfo.devId);

        updateProperties(properties);
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
        properties.put("Sensor:", device.full.sensor);
        properties.put("Lens Name:", device.full.lensName);
        properties.put("Call Support:", device.basic.isCal ? "Supported" : "Not Supported");
        updateProperties(properties);
    }

    private void updateThingProperties(NetworkInfo network) {
        updateProperty("IP Address:", network.ipaddr);
        updateProperty("Link Type:", network.link_type);
    }

    private Future<?> getCameraParameters(Integer interval) {
        return scheduler.scheduleWithFixedDelay(this::pollingCamera, interval, interval, TimeUnit.SECONDS);
    }

    private void pollingCamera() {
        logger.debug("get camera parameters");
        try {

            if (!apiV2.isAuth()) {
                reconnect();
            } else {
                updateStatus(ThingStatus.ONLINE);
            }

            apiV2.getChangebleParameters().forEach(param -> {
                processAllResults(param.result);
            });

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void setDeviceStatus(Integer errorCode) {
        String msg = ApiErrorCodes.getErrorByCode(errorCode).getMessgae();
        if (errorCode.equals(ApiErrorCodes.ERROR_40401.getCode())) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    String.format("%d - %s", errorCode, msg));
        }
    }

    public void setDeviceStatus(String message) {
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, String.format("%s", message));
    }

    private void processAllResults(Object data) {
        if (data instanceof LedStatus) {
            // get led status
            String ledStatus = ((LedStatus) data).enabled;
            cameraState.setLedStatus(ledStatus);
            updateState(CHANNEL_LED_STATUS.getName(), OnOffType.from(ledStatus.toUpperCase()));
        }
        // audio config
        else if (data instanceof AudioSpeakerInfo) {
            // speaker status
            int volume = ((AudioSpeakerInfo) data).volume;
            cameraState.setSpeakerVolume(volume);
            updateState(CHANNEL_SPEAKER_VOLUME.getName(), new PercentType(volume));
        } else if (data instanceof AudioMicrophoneInfo) {
            // microphone status
            // logger.info("\t mute: " + ((AudioMicrophoneInfo) data).mute);
            int volume = ((AudioMicrophoneInfo) data).volume;
            cameraState.setMicrophoneVolume(volume);
            updateState(CHANNEL_MICROPHONE_VOLUME.getName(), new PercentType(volume));
        }
        // detections
        else if (data instanceof MotionDetection) {
            // motion detection
            MotionDetection motionDetection = (MotionDetection) data;
            cameraState.setMotionDetection(motionDetection);
            updateState(CHANNEL_MOTION_DETECTION_ENABLED.getName(), OnOffType.from(motionDetection.enabled.toUpperCase()));
            updateState(CHANNEL_MOTION_DETECTION_SENSITIVITY.getName(), new StringType(motionDetection.sensitivity));
            int digitalSensitivity = motionDetection.digitalSensitivity / 10;
            updateState(CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY.getName(), new PercentType(digitalSensitivity));
        } else if (data instanceof PersonDetectionInfo) {
            // person detection
            PersonDetectionInfo personDetectionInfo = (PersonDetectionInfo) data;
            cameraState.setPersonDetectionInfo(personDetectionInfo);
            updateState(CHANNEL_PERSON_DETECTION_ENABLED.getName(), OnOffType.from(personDetectionInfo.enabled.toUpperCase()));
            int digitalSensitivity = personDetectionInfo.sensitivity / 10;
            updateState(CHANNEL_PERSON_DETECTION_SENSITIVITY.getName(), new PercentType(digitalSensitivity));
        } else if (data instanceof LineCrossingDetectionInfo) {
            // line crossing detection
            LineCrossingDetectionInfo lineCrossingDetection = (LineCrossingDetectionInfo) data;
            cameraState.setLineCrossingDetection(lineCrossingDetection);
            updateState(CHANNEL_LINE_CROSSING_DETECTION_ENABLED.getName(),
                    OnOffType.from(lineCrossingDetection.enabled.toUpperCase()));
        } else if (data instanceof IntrusionDetectionInfo) {
            // intrusion detection
            IntrusionDetectionInfo intrusionDetection = (IntrusionDetectionInfo) data;
            cameraState.setIntrusionDetection(intrusionDetection);
            updateState(CHANNEL_INTRUSION_DETECTION_ENABLED.getName(), OnOffType.from(intrusionDetection.enabled.toUpperCase()));
        } else if (data instanceof TamperDetectionInfo) {
            logger.info("TamperDetectionInfo:");
            logger.info("\t enabled:" + ((TamperDetectionInfo) data).enabled);
            logger.info("\t sensitivity:" + ((TamperDetectionInfo) data).sensitivity);
            logger.info("\t digitalSensitivity:" + ((TamperDetectionInfo) data).digitalSensitivity);
            // TODO: tamper detection
        }
        // image info
        else if (data instanceof ImageCommon) {
            // TODO: image commmon
            logger.info("ImageCommon:");
            logger.info("\t infSensitivity:" + ((ImageCommon) data).infSensitivity);
            logger.info("\t lightFreqMode:" + ((ImageCommon) data).lightFreqMode);
        } else if (data instanceof ImageSwitch) {
            // TODO: image switch
            logger.info("ImageSwitch:");
            logger.info("\t flipType:" + ((ImageSwitch) data).flipType);
            logger.info("\t rotateType:" + ((ImageSwitch) data).rotateType);
            logger.info("\t switchMode:" + ((ImageSwitch) data).switchMode);
            logger.info("\t nightVisionMode:" + ((ImageSwitch) data).nightVisionMode);
            logger.info("\t lensDistortionCorrection:" + ((ImageSwitch) data).lensDistortionCorrection);
            logger.info("\t wtlIntensityLevel:" + ((ImageSwitch) data).wtlIntensityLevel);
        }  else if (data instanceof LensMaskInfo) {
            logger.info("LensMaskInfo:");
            logger.info("\t enabled:" + ((LensMaskInfo) data).enabled);
        }

        // message alarm
        else if (data instanceof LastAlarmInfo) {
            logger.info("LastAlarmInfo:");
            logger.info("\t lastAlarmTime:" + ((LastAlarmInfo) data).lastAlarmTime);
            logger.info("\t lastAlarmTime:" + ((LastAlarmInfo) data).lastAlarmTime);
        } else if (data instanceof MsgAlarmInfo) {
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
            logger.info("MsgPushInfo:");
            logger.info("\t notificationEnabled:" + ((MsgPushInfo) data).notificationEnabled);
            logger.info("\t richNotificationEnabled:" + ((MsgPushInfo) data).richNotificationEnabled);
        }
        /*


         */
    }
}
