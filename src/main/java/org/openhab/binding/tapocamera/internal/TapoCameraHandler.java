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
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_PEOPLE_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_PEOPLE_DETECTION_SENSITIVITY;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_ALARM_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_ALARM_MODE;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_ALARM_TYPE;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_INTRUSION_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_LED_STATUS;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_LINE_CROSSING_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_MANUAL_ALARM;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_MOTION_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_PEOPLE_DETECTION_ENABLED;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_PEOPLE_DETECTION_SENSITIVITY;
import static org.openhab.binding.tapocamera.internal.TapoCameraChannels.CHANNEL_SPEAKER_VOLUME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import org.openhab.binding.tapocamera.internal.api.ApiErrorCodes;
import org.openhab.binding.tapocamera.internal.api.ApiException;
import org.openhab.binding.tapocamera.internal.api.TapoCameraApi;
import org.openhab.binding.tapocamera.internal.api.response.ApiDeviceInfo;
import org.openhab.binding.tapocamera.internal.dto.AlarmInfo;
import org.openhab.binding.tapocamera.internal.dto.CameraState;
import org.openhab.binding.tapocamera.internal.dto.IntrusionDetection;
import org.openhab.binding.tapocamera.internal.dto.LineCrossingDetection;
import org.openhab.binding.tapocamera.internal.dto.MotionDetection;
import org.openhab.binding.tapocamera.internal.dto.PeopleDetection;
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

    private CameraState cameraState = new CameraState();

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
        } else if (CHANNEL_LED_STATUS.getName().equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            } else if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.setLedStatus(status);
                api.setLedStatus(status);
            }
        } else if (CHANNEL_ALARM_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getAlarmInfo().setEnabled(status);
                api.setAlarmInfoEnabled(status);
            }
        } else if (CHANNEL_ALARM_MODE.getName().equals(channelUID.getId())) {
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
                api.setAlarmInfoMode(modes);
            }
        } else if (CHANNEL_ALARM_TYPE.getName().equals(channelUID.getId())) {
            if (command instanceof StringType) {
                String status = command.toString();
                cameraState.getAlarmInfo().setAlarmType(status);
                api.setAlarmInfoType(status);
            }

        } else if (CHANNEL_MOTION_DETECTION_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getMotionDetection().setEnabled(status);
                api.setMotionDetectionEnabled(status);
            }
        } else if (CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                String value = String.valueOf(((PercentType) command).intValue() * 10);
                cameraState.getMotionDetection().setDigitalSensitivity(value);
                api.setMotionDetectionSensitivity(value);
            }
        } else if (CHANNEL_PEOPLE_DETECTION_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getPeopleDetection().setEnabled(status);
                api.setPeopleDetectionEnabled(status);
            }
        } else if (CHANNEL_PEOPLE_DETECTION_SENSITIVITY.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                String value = String.valueOf(((PercentType) command).intValue() * 10);
                cameraState.getPeopleDetection().setSensitivity(value);
                api.setPeopleDetectionSensitivity(value);
            }
        } else if (CHANNEL_LINE_CROSSING_DETECTION_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getLineCrossingDetection().setEnabled(status);
                api.setLineCrossingDetectionEnabled(status);
            }
        } else if (CHANNEL_INTRUSION_DETECTION_ENABLED.getName().equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                String status = command.toString().toLowerCase();
                cameraState.getIntrusionDetection().setEnabled(status);
                api.setIntrusionDetectionEnabled(status);
            }
        } else if (CHANNEL_SPEAKER_VOLUME.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                int volume = ((PercentType) command).intValue();
                cameraState.setSpeakerVolume(volume);
                api.setSpeakerVolume(volume);
            }
        } else if (CHANNEL_MICROPHONE_VOLUME.getName().equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                int volume = ((PercentType) command).intValue();
                cameraState.setMicrophoneVolume(volume);
                api.setMicrophoneVolume(volume);
            }
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(TapoCameraConfiguration.class);
        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        initJob = connect(0);
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

    private Boolean deviceAuth() {
        Boolean result = false;
        api.setHostname(config.hostname);
        String pass = config.cloudPassword.isEmpty() ? config.password : config.cloudPassword;
        try {
            result = api.auth(config.username, pass);
            return result;
        } catch (ApiException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            reconnect();
            throw new RuntimeException(e);
        }
    }

    private Future<?> connect(int wait) {
        logger.warn("Try connect after: {} sec", wait);
        return scheduler.schedule(() -> {
            if (config == null) {
                updateStatus(ThingStatus.UNINITIALIZED, ThingStatusDetail.CONFIGURATION_ERROR);
            } else {
                updateStatus(ThingStatus.OFFLINE);
                boolean thingReachable = deviceAuth();
                if (thingReachable) {
                    updateStatus(ThingStatus.ONLINE);
                    ApiDeviceInfo devInfo = api.getDeviceInfo();
                    setThingProperties(devInfo);
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
        initJob = connect(config.reconnectInterval);
    }

    private void setThingProperties(ApiDeviceInfo device) {
        Map<String, String> properties = new HashMap<>();
        properties.put("Friendly Name:", device.basicInfo.friendlyName);
        properties.put("Device model:", device.basicInfo.deviceModel);
        properties.put("Software version:", device.basicInfo.swVersion);
        properties.put("Hardware version:", device.basicInfo.hwVersion);
        properties.put("Device identifier:", device.basicInfo.devId);

        updateProperties(properties);
    }

    private Future<?> getCameraParameters(Integer interval) {
        return scheduler.scheduleWithFixedDelay(this::pollingCamera, interval, interval, TimeUnit.SECONDS);
    }

    private void pollingCamera() {
        logger.debug("get camera parameters");
        try {
            if (api.getToken().isEmpty()) {
                if (!deviceAuth()) {
                    return;
                }
                updateStatus(ThingStatus.ONLINE);
            }
            // get led status
            String ledStatus = api.getLedStatus();
            cameraState.setLedStatus(ledStatus);
            updateState(CHANNEL_LED_STATUS.getName(), OnOffType.from(ledStatus.toUpperCase()));

            // alarm mode
            AlarmInfo alarmInfo = api.getAlarmInfo();
            cameraState.setAlarmInfo(alarmInfo);
            updateState(CHANNEL_ALARM_ENABLED.getName(), OnOffType.from(alarmInfo.getEnabled().toUpperCase()));
            updateState(CHANNEL_ALARM_TYPE.getName(), new StringType(alarmInfo.getAlarmType()));
            updateState(CHANNEL_ALARM_LIGHT_TYPE.getName(), new StringType(alarmInfo.getLightType()));

            if (alarmInfo.getAlarmMode().isEmpty() || alarmInfo.getAlarmMode().size() == 0) {
                updateState(CHANNEL_ALARM_MODE.getName(), new StringType("off"));
            } else if (alarmInfo.getAlarmMode().size() == 2) {
                updateState(CHANNEL_ALARM_MODE.getName(), new StringType("both"));
            } else if (alarmInfo.getAlarmMode().size() == 1) {
                updateState(CHANNEL_ALARM_MODE.getName(), new StringType(alarmInfo.getAlarmMode().get(0)));
            }

            // motion detection
            MotionDetection motionDetection = api.getMotionDetection();
            cameraState.setMotionDetection(motionDetection);
            updateState(CHANNEL_MOTION_DETECTION_ENABLED.getName(), OnOffType.from(motionDetection.getEnabled().toUpperCase()));
            updateState(CHANNEL_MOTION_DETECTION_SENSITIVITY.getName(), new StringType(motionDetection.getSensitivity()));
            int digitalSensitivity = Integer.parseInt(motionDetection.getDigitalSensitivity()) / 10;
            updateState(CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY.getName(), new PercentType(digitalSensitivity));

            // people detection
            PeopleDetection peopleDetection = api.getPeopleDetection();
            cameraState.setPeopleDetection(peopleDetection);
            updateState(CHANNEL_PEOPLE_DETECTION_ENABLED.getName(), OnOffType.from(peopleDetection.getEnabled().toUpperCase()));
            digitalSensitivity = Integer.parseInt(peopleDetection.getSensitivity()) / 10;
            updateState(CHANNEL_PEOPLE_DETECTION_SENSITIVITY.getName(), new PercentType(digitalSensitivity));

            LineCrossingDetection lineCrossingDetection = api.getLineCrossingDetection();
            cameraState.setLineCrossingDetection(lineCrossingDetection);
            updateState(CHANNEL_LINE_CROSSING_DETECTION_ENABLED.getName(),
                    OnOffType.from(lineCrossingDetection.getEnabled().toUpperCase()));

            IntrusionDetection intrusionDetection = api.getIntrusionDetection();
            cameraState.setIntrusionDetection(intrusionDetection);
            updateState(CHANNEL_INTRUSION_DETECTION_ENABLED.getName(),
                    OnOffType.from(intrusionDetection.getEnabled().toUpperCase()));

            int volume = api.getMicrophoneVolume();
            cameraState.setMicrophoneVolume(volume);
            updateState(CHANNEL_MICROPHONE_VOLUME.getName(), new PercentType(volume));

            volume = api.getSpeakerVolume();
            cameraState.setSpeakerVolume(volume);
            updateState(CHANNEL_SPEAKER_VOLUME.getName(), new PercentType(volume));

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
}
