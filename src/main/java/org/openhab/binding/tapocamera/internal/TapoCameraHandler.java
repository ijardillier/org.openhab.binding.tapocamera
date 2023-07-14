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

import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_ALARM_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_ALARM_LIGHT_TYPE;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_ALARM_MODE;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_ALARM_TYPE;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_INTRUSION_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_LED_STATUS;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_LINE_CROSSING_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_MANUAL_ALARM;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_MOTION_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_MOTION_DETECTION_SENSITIVITY;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_PEOPLE_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_PEOPLE_DETECTION_SENSITIVITY;

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
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_AlarmInfo;
import org.openhab.binding.tapocamera.internal.api.v2.ApiMethodResult;
import org.openhab.binding.tapocamera.internal.api.v2.TapoApi;
import org.openhab.binding.tapocamera.internal.api.v2.dto.audio.AudioMicrophoneInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.audio.AudioSpeakerInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.DeviceInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.LedStatus;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.NetworkInfo;
import org.openhab.binding.tapocamera.internal.dto.CameraState;
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_IntrusionDetection;
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_LineCrossingDetection;
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_MotionDetection;
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_PeopleDetection;
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
    private final TapoCameraApi_v1 apiV1;
    private final TapoApi apiV2;

    private CameraState cameraState = new CameraState();

    public TapoCameraHandler(Thing thing, TapoCameraApi_v1 apiV1, TapoApi apiV2) {
        super(thing);
        this.apiV1 = apiV1;
        this.apiV2 = apiV2;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_MANUAL_ALARM.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                apiV1.old_setManualAlarm(command.equals(OnOffType.ON) ? "start" : "stop");
            }
        } else if (CHANNEL_LED_STATUS.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            } else if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.setLedStatus(status);
                apiV1.old_setLedStatus(status);
            }
        } else if (CHANNEL_ALARM_ENABLED.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getAlarmInfo().setEnabled(status);
                apiV1.old_setAlarmInfoEnabled(status);
            }
        } else if (CHANNEL_ALARM_MODE.equals(channelUID.getId())) {
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
                cameraState.getAlarmInfo().setAlarmMode(modes);
                apiV1.old_setAlarmInfoMode(modes);
            }
        } else if (CHANNEL_ALARM_TYPE.equals(channelUID.getId())) {
            if (command instanceof StringType) {
                String status = command.toString();
                cameraState.getAlarmInfo().setAlarmType(status);
                apiV1.old_setAlarmInfoType(status);
            }

        } else if (CHANNEL_MOTION_DETECTION_ENABLED.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getMotionDetection().setEnabled(status);
                apiV1.old_setMotionDetectionEnabled(status);
            }
        } else if (CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY.equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                String value = String.valueOf(((PercentType) command).intValue() * 10);
                cameraState.getMotionDetection().setDigitalSensitivity(value);
                apiV1.old_setMotionDetectionSensitivity(value);
            }
        } else if (CHANNEL_PEOPLE_DETECTION_ENABLED.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getPeopleDetection().setEnabled(status);
                apiV1.old_setPeopleDetectionEnabled(status);
            }
        } else if (CHANNEL_PEOPLE_DETECTION_SENSITIVITY.equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                String value = String.valueOf(((PercentType) command).intValue() * 10);
                cameraState.getPeopleDetection().setSensitivity(value);
                apiV1.old_setPeopleDetectionSensitivity(value);
            }
        } else if (CHANNEL_LINE_CROSSING_DETECTION_ENABLED.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getLineCrossingDetection().setEnabled(status);
                apiV1.old_setLineCrossingDetectionEnabled(status);
            }
        } else if (CHANNEL_INTRUSION_DETECTION_ENABLED.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getIntrusionDetection().setEnabled(status);
                apiV1.old_setIntrusionDetectionEnabled(status);
            }
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(TapoCameraConfiguration.class);
        apiV2.setHostname(config.hostname);
        updateStatus(ThingStatus.UNKNOWN);

        initJob = connectCamera(0);
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
            if (config == null) {
                updateStatus(ThingStatus.UNINITIALIZED, ThingStatusDetail.CONFIGURATION_ERROR);
            } else {
                updateStatus(ThingStatus.OFFLINE);
                boolean thingReachable = deviceAuth();
                if (thingReachable) {
                    updateStatus(ThingStatus.ONLINE);
                    // Old_ApiDeviceInfo devInfo = apiV1.old_getDeviceInfo();
                    DeviceInfo deviceInfo = apiV2.getDeviceInfo();
                    NetworkInfo networkInfo = apiV2.getNetworkInfo();

                    // setThingProperties(devInfo);
                    setThingProperties(deviceInfo);
                    updateThingProperties(networkInfo);

                    pollingJob = getCameraParameters(config.pollingInterval);
                } else {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
                    reconnect();
                }
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
//            if (apiV1.old_getToken().isEmpty()) {
//                if (!deviceAuth()) {
//                    return;
//                }
//                updateStatus(ThingStatus.ONLINE);
//            }
            if (!apiV2.isAuth()) {
                reconnect();
            }

            apiV2.getChangebleParameters().forEach(param -> {
                processAllResults(param.result);
            });

            // get led status
//            String ledStatus = apiV1.old_getLedStatus();
//            cameraState.setLedStatus(ledStatus);
//            updateState(CHANNEL_LED_STATUS, OnOffType.from(ledStatus.toUpperCase()));

            // alarm mode
            Old_AlarmInfo oldAlarmInfo = apiV1.old_getAlarmInfo();
            cameraState.setAlarmInfo(oldAlarmInfo);
            updateState(CHANNEL_ALARM_ENABLED, OnOffType.from(oldAlarmInfo.getEnabled().toUpperCase()));
            updateState(CHANNEL_ALARM_TYPE, new StringType(oldAlarmInfo.getAlarmType()));
            updateState(CHANNEL_ALARM_LIGHT_TYPE, new StringType(oldAlarmInfo.getLightType()));

            if (oldAlarmInfo.getAlarmMode().isEmpty() || oldAlarmInfo.getAlarmMode().size() == 0) {
                updateState(CHANNEL_ALARM_MODE, new StringType("off"));
            } else if (oldAlarmInfo.getAlarmMode().size() == 2) {
                updateState(CHANNEL_ALARM_MODE, new StringType("both"));
            } else if (oldAlarmInfo.getAlarmMode().size() == 1) {
                updateState(CHANNEL_ALARM_MODE, new StringType(oldAlarmInfo.getAlarmMode().get(0)));
            }

            // motion detection
            Old_MotionDetection oldMotionDetection = apiV1.old_getMotionDetection();
            cameraState.setMotionDetection(oldMotionDetection);
            updateState(CHANNEL_MOTION_DETECTION_ENABLED, OnOffType.from(oldMotionDetection.getEnabled().toUpperCase()));
            updateState(CHANNEL_MOTION_DETECTION_SENSITIVITY, new StringType(oldMotionDetection.getSensitivity()));
            int digitalSensitivity = Integer.parseInt(oldMotionDetection.getDigitalSensitivity()) / 10;
            updateState(CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY, new PercentType(digitalSensitivity));

            // people detection
            Old_PeopleDetection oldPeopleDetection = apiV1.old_getPeopleDetection();
            cameraState.setPeopleDetection(oldPeopleDetection);
            updateState(CHANNEL_PEOPLE_DETECTION_ENABLED, OnOffType.from(oldPeopleDetection.getEnabled().toUpperCase()));
            digitalSensitivity = Integer.parseInt(oldPeopleDetection.getSensitivity()) / 10;
            updateState(CHANNEL_PEOPLE_DETECTION_SENSITIVITY, new PercentType(digitalSensitivity));

            Old_LineCrossingDetection oldLineCrossingDetection = apiV1.old_getLineCrossingDetection();
            cameraState.setLineCrossingDetection(oldLineCrossingDetection);
            updateState(CHANNEL_LINE_CROSSING_DETECTION_ENABLED,
                    OnOffType.from(oldLineCrossingDetection.getEnabled().toUpperCase()));

            Old_IntrusionDetection oldIntrusionDetection = apiV1.old_getIntrusionDetection();
            cameraState.setIntrusionDetection(oldIntrusionDetection);
            updateState(CHANNEL_INTRUSION_DETECTION_ENABLED,
                    OnOffType.from(oldIntrusionDetection.getEnabled().toUpperCase()));

            // get image common
            // get image switch
            // get events

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
            updateState(CHANNEL_LED_STATUS, OnOffType.from(ledStatus.toUpperCase()));
        } else if (data instanceof AudioSpeakerInfo) {
            // speaker status
            logger.info("AudioSpeakerInfo:");
            logger.info("\t volume: " + ((AudioSpeakerInfo) data).volume);
        } else if (data instanceof AudioMicrophoneInfo) {
            // microphone status
            logger.info("AudioMicrophoneInfo:");
            logger.info("\t volume: " + ((AudioMicrophoneInfo) data).volume);
            logger.info("\t mute: " + ((AudioMicrophoneInfo) data).mute);
        }
    }
}
