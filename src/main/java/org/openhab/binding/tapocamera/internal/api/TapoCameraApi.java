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

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

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

    ApiResponse sendPostRequest(String path, String body)
            throws ApiException, KeyManagementException, NoSuchAlgorithmException;

    Boolean auth(String username, String password) throws ApiException;

    void setHostname(String hostname);

    String getToken();

    ApiDeviceInfo getDeviceInfo() throws ApiException;

    String getLedStatus() throws ApiException;

    AlarmInfo getAlarmInfo() throws ApiException;

    MotionDetection getMotionDetection() throws ApiException;

    PeopleDetection getPeopleDetection() throws ApiException;

    LineCrossingDetection getLineCrossingDetection() throws ApiException;

    IntrusionDetection getIntrusionDetection() throws ApiException;

    void setLedStatus(String status) throws ApiException;

    void setAlarmInfoEnabled(String status) throws ApiException;

    void setAlarmInfoMode(List<String> modes) throws ApiException;

    void setAlarmInfoType(String type) throws ApiException;

    void setMotionDetectionEnabled(String state) throws ApiException;

    void setMotionDetectionSensitivity(String state) throws ApiException;

    void setPeopleDetectionEnabled(String state) throws ApiException;

    void setPeopleDetectionSensitivity(String state) throws ApiException;

    void setManualAlarm(String state) throws ApiException;

    void setLineCrossingDetectionEnabled(String state) throws ApiException;
    void setIntrusionDetectionEnabled(String state) throws ApiException;
}
