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
        if (CHANNEL_MANUAL_ALARM.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                try {
                    api.setManualAlarm(command.equals(OnOffType.ON) ? "start" : " stop");
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (CHANNEL_LED_STATUS.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            } else if (command instanceof OnOffType) {
                cameraState.setLedStatus(command.toString().toLowerCase());
                try {
                    api.setLedStatus(cameraState.getLedStatus());
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (CHANNEL_ALARM_ENABLED.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                cameraState.getAlarmInfo().setEnabled(command.toString().toLowerCase());
                try {
                    api.setAlarmInfoEnabled(cameraState.getAlarmInfo().getEnabled());
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
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
                try {
                    api.setAlarmInfoMode(modes);
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (CHANNEL_ALARM_TYPE.equals(channelUID.getId())) {
            if (command instanceof StringType) {
                cameraState.getAlarmInfo().setAlarmType(command.toString());
                try {
                    api.setAlarmInfoType(command.toString());
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }

        } else if (CHANNEL_MOTION_DETECTION_ENABLED.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                cameraState.getMotionDetection().setEnabled(command.toString().toLowerCase());
                try {
                    api.setMotionDetectionEnabled(cameraState.getMotionDetection().getEnabled());
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY.equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                String value = String.valueOf(((PercentType) command).intValue() * 10);
                cameraState.getMotionDetection().setDigitalSensitivity(value);
                try {
                    api.setMotionDetectionSensitivity(value);
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (CHANNEL_PEOPLE_DETECTION_ENABLED.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                cameraState.getPeopleDetection().setEnabled(command.toString().toLowerCase());
                try {
                    api.setPeopleDetectionEnabled(cameraState.getPeopleDetection().getEnabled());
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (CHANNEL_PEOPLE_DETECTION_SENSITIVITY.equals(channelUID.getId())) {
            if (command instanceof PercentType) {
                String value = String.valueOf(((PercentType) command).intValue() * 10);
                cameraState.getPeopleDetection().setSensitivity(value);
                try {
                    api.setPeopleDetectionSensitivity(value);
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (CHANNEL_LINE_CROSSING_DETECTION_ENABLED.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                cameraState.getLineCrossingDetection().setEnabled(command.toString().toLowerCase());
                try {
                    api.setLineCrossingDetectionEnabled(command.toString().toUpperCase());
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (CHANNEL_INTRUSION_DETECTION_ENABLED.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                cameraState.getIntrusionDetection().setEnabled(command.toString().toLowerCase());
                try {
                    api.setIntrusionDetectionEnabled(command.toString().toUpperCase());
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
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

    private Future<?> connect(int wait) {
        logger.warn("Try connect after: {} sec", wait);
        return scheduler.schedule(() -> {
            if (config == null) {
                updateStatus(ThingStatus.UNINITIALIZED, ThingStatusDetail.CONFIGURATION_ERROR);
            } else {
                updateStatus(ThingStatus.OFFLINE);
                api.setHostname(config.hostname);
                String pass = config.cloudPassword.isEmpty() ? config.password : config.cloudPassword;
                boolean thingReachable = false;
                try {
                    thingReachable = api.auth(config.username, pass);
                } catch (ApiException e) {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
                    reconnect();
                    throw new RuntimeException(e);
                }
                if (thingReachable) {
                    updateStatus(ThingStatus.ONLINE);
                    try {
                        ApiDeviceInfo devInfo = api.getDeviceInfo();
                        setThingProperties(devInfo);
                        pollingJob = getCameraParameters(config.pollingInterval);
                    } catch (ApiException e) {
                        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
                        throw new RuntimeException(e);
                    }
                } else {
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
        logger.debug("get camera parameters");
        return scheduler.scheduleWithFixedDelay(() -> {

            try {
                // get led status
                String ledStatus = api.getLedStatus();
                cameraState.setLedStatus(ledStatus);
                updateState(CHANNEL_LED_STATUS, OnOffType.from(ledStatus.toUpperCase()));

                // alarm mode
                AlarmInfo alarmInfo = api.getAlarmInfo();
                cameraState.setAlarmInfo(alarmInfo);
                updateState(CHANNEL_ALARM_ENABLED, OnOffType.from(alarmInfo.getEnabled().toUpperCase()));
                updateState(CHANNEL_ALARM_TYPE, new StringType(alarmInfo.getAlarmType()));
                updateState(CHANNEL_ALARM_LIGHT_TYPE, new StringType(alarmInfo.getLightType()));

                if (alarmInfo.getAlarmMode().isEmpty() || alarmInfo.getAlarmMode().size() == 0) {
                    updateState(CHANNEL_ALARM_MODE, new StringType("off"));
                } else if (alarmInfo.getAlarmMode().size() == 2) {
                    updateState(CHANNEL_ALARM_MODE, new StringType("both"));
                } else if (alarmInfo.getAlarmMode().size() == 1) {
                    updateState(CHANNEL_ALARM_MODE, new StringType(alarmInfo.getAlarmMode().get(0)));
                }

                // motion detection
                MotionDetection motionDetection = api.getMotionDetection();
                cameraState.setMotionDetection(motionDetection);
                updateState(CHANNEL_MOTION_DETECTION_ENABLED,
                        OnOffType.from(motionDetection.getEnabled().toUpperCase()));
                updateState(CHANNEL_MOTION_DETECTION_SENSITIVITY, new StringType(motionDetection.getSensitivity()));
                int digitalSensitivity = Integer.parseInt(motionDetection.getDigitalSensitivity()) / 10;
                updateState(CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY, new PercentType(digitalSensitivity));

                // people detection
                PeopleDetection peopleDetection = api.getPeopleDetection();
                cameraState.setPeopleDetection(peopleDetection);
                updateState(CHANNEL_PEOPLE_DETECTION_ENABLED,
                        OnOffType.from(peopleDetection.getEnabled().toUpperCase()));
                digitalSensitivity = Integer.parseInt(peopleDetection.getSensitivity()) / 10;
                updateState(CHANNEL_PEOPLE_DETECTION_SENSITIVITY, new PercentType(digitalSensitivity));

                LineCrossingDetection lineCrossingDetection = api.getLineCrossingDetection();
                cameraState.setLineCrossingDetection(lineCrossingDetection);
                updateState(CHANNEL_LINE_CROSSING_DETECTION_ENABLED, OnOffType.from(lineCrossingDetection.getEnabled().toUpperCase()));

                IntrusionDetection intrusionDetection = api.getIntrusionDetection();
                cameraState.setIntrusionDetection(intrusionDetection);
                updateState(CHANNEL_INTRUSION_DETECTION_ENABLED, OnOffType.from(intrusionDetection.getEnabled().toUpperCase()));


                // get image common
                // get image switch
                // get events

            } catch (ApiException e) {
                throw new RuntimeException(e);
            }

        }, interval, interval, TimeUnit.SECONDS);
    }
}
