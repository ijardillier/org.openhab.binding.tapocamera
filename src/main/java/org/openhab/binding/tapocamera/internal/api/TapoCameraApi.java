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

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import org.openhab.binding.tapocamera.internal.TapoCameraHandler;
import org.openhab.binding.tapocamera.internal.api.response.ApiDeviceInfo;
import org.openhab.binding.tapocamera.internal.api.response.ApiResponse;
import org.openhab.binding.tapocamera.internal.dto.AlarmInfo;
import org.openhab.binding.tapocamera.internal.dto.IntrusionDetection;
import org.openhab.binding.tapocamera.internal.dto.LineCrossingDetection;
import org.openhab.binding.tapocamera.internal.dto.MotionDetection;
import org.openhab.binding.tapocamera.internal.dto.PeopleDetection;

/**
 * The {@link TapoCameraApi} is describing api interface.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
@NonNullByDefault
public interface TapoCameraApi {

    void setDevice(TapoCameraHandler device);

    ApiResponse sendPostRequest(String path, String body) throws ApiException;

    Boolean auth(String username, String password) throws ApiException;

    void setHostname(String hostname);

    String getToken();

    ApiDeviceInfo getDeviceInfo();

    String getLedStatus();

    AlarmInfo getAlarmInfo();

    MotionDetection getMotionDetection();

    PeopleDetection getPeopleDetection();

    LineCrossingDetection getLineCrossingDetection();

    IntrusionDetection getIntrusionDetection();

    void setLedStatus(String status);

    void setAlarmInfoEnabled(String status);

    void setAlarmInfoMode(List<String> modes);

    void setAlarmInfoType(String type);

    void setMotionDetectionEnabled(String state);

    void setMotionDetectionSensitivity(String state);
    void setMotionDetectionDigitalSensitivity(String state);

    void setPeopleDetectionEnabled(String state);

    void setPeopleDetectionSensitivity(String state);

    void setManualAlarm(String state);

    void setLineCrossingDetectionEnabled(String state);

    void setIntrusionDetectionEnabled(String state);

    int getSpeakerVolume();
    void setSpeakerVolume(int volume);

    int getMicrophoneVolume();

    void setMicrophoneVolume(int volume);
}
