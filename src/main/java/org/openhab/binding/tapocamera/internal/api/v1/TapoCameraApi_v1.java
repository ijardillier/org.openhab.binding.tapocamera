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

package org.openhab.binding.tapocamera.internal.api.v1;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import org.openhab.binding.tapocamera.internal.TapoCameraHandler;
import org.openhab.binding.tapocamera.internal.api.ApiException;
import org.openhab.binding.tapocamera.internal.api.v1.response.Old_ApiDeviceInfo;
import org.openhab.binding.tapocamera.internal.api.v1.response.ApiResponse;
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_AlarmInfo;
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_IntrusionDetection;
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_LineCrossingDetection;
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_MotionDetection;
import org.openhab.binding.tapocamera.internal.api.v1.dto.Old_PeopleDetection;

/**
 * The {@link TapoCameraApi_v1} is describing api interface.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
@NonNullByDefault
public interface TapoCameraApi_v1 {

    void old_setDevice(TapoCameraHandler device);

    ApiResponse old_sendPostRequest(String path, String body) throws ApiException;

    Boolean old_auth(String username, String password) throws ApiException;

    void old_setHostname(String hostname);

    String old_getToken();

    Old_ApiDeviceInfo old_getDeviceInfo();

    String old_getLedStatus();

    @Nullable
    Old_AlarmInfo old_getAlarmInfo();

    @Nullable
    Old_MotionDetection old_getMotionDetection();

    @Nullable
    Old_PeopleDetection old_getPeopleDetection();

    @Nullable
    Old_LineCrossingDetection old_getLineCrossingDetection();

    @Nullable
    Old_IntrusionDetection old_getIntrusionDetection();

    void old_setLedStatus(String status);

    void old_setAlarmInfoEnabled(String status);

    void old_setAlarmInfoMode(List<String> modes);

    void old_setAlarmInfoType(String type);

    void old_setMotionDetectionEnabled(String state);

    void old_setMotionDetectionSensitivity(String state);

    void old_setPeopleDetectionEnabled(String state);

    void old_setPeopleDetectionSensitivity(String state);

    void old_setManualAlarm(String state);

    void old_setLineCrossingDetectionEnabled(String state);

    void old_setIntrusionDetectionEnabled(String state);
}
