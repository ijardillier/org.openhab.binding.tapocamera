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

import java.util.List;

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

/**
 * The interface Tapo camera api.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public interface TapoCameraApi {
    /**
     * Sets hostname.
     *
     * @param hostname the hostname
     */
    void setHostname(String hostname);

    /**
     * Auth boolean.
     *
     * @param username the username
     * @param password the password
     * @return the boolean
     * @throws ApiException the api exception
     */
    Boolean auth(String username, String password) throws ApiException;

    /**
     * Is authenticated boolean.
     *
     * @return the boolean
     */
    Boolean isAuthenticated();

    /**
     * Send multiple request api response.
     *
     * @param data the data
     * @return the api response
     */
    ApiResponse sendMultipleRequest(String data);

    /**
     * Send single request object.
     *
     * @param data the data
     * @return the object
     */
    Object sendSingleRequest(String data);

    /**
     * Process single response object.
     *
     * @param data the data
     * @param moduleName the module name
     * @param section the section
     * @return the object
     */
    Object processSingleResponse(Object data, String moduleName, String section);

    /**
     * Process single response list.
     *
     * @param data the data
     * @param moduleName the module name
     * @param sections the sections
     * @return the list
     */
    List<Object> processSingleResponse(Object data, String moduleName, List<String> sections);

    /**
     * Gets parameter info.
     *
     * @param type the type
     * @return the parameter info
     */
    Class<?> getParameterInfo(ApiMethodTypes type);

    /**
     * Gets device info.
     *
     * @return the device info
     */
    DeviceInfo getDeviceInfo();

    /**
     * Gets network info.
     *
     * @return the network info
     */
    NetworkInfo getNetworkInfo();

    ModuleSpec getModuleSpec();

    /**
     * Gets speaker info.
     *
     * @return the speaker info
     */
    AudioSpeakerInfo getSpeakerInfo();

    /**
     * Sets speaker volume.
     *
     * @param volume the volume
     */
    void setSpeakerVolume(int volume);

    /**
     * Gets microphone info.
     *
     * @return the microphone info
     */
    AudioMicrophoneInfo getMicrophoneInfo();

    /**
     * Sets microphone volume.
     *
     * @param volume the volume
     */
    void setMicrophoneVolume(int volume);

    /**
     * Gets clock status.
     *
     * @return the clock status
     */
    ClockStatus getClockStatus();

    /**
     * Gets image common info.
     *
     * @return the image common info
     */
    ImageCommon getImageCommonInfo();

    /**
     * Gets image switch info.
     *
     * @return the image switch info
     */
    ImageSwitch getImageSwitchInfo();

    void setImageFlip(String state);

    void setImageLdc(String state);

    void setImageNightVision(String state);

    void setImageContrast(Integer state);

    void setImageSaturation(Integer state);

    void setImageSharpness(Integer state);

    void setImageLuma(Integer state);

    /**
     * Gets lens mask info.
     *
     * @return the lens mask info
     */
    LensMaskInfo getLensMaskInfo();

    void setLensMaskEnabled(String state);

    /**
     * Gets led status.
     *
     * @return the led status
     */
    LedStatus getLedStatus();

    /**
     * Sets led status.
     *
     * @param status the status
     */
    void setLedStatus(String status);

    /**
     * Gets msg alarm info.
     *
     * @return the msg alarm info
     */
    MsgAlarmInfo getMsgAlarmInfo();

    /**
     * Sets alarm info enabled.
     *
     * @param status the status
     */
    void setAlarmInfoEnabled(String status);

    /**
     * Sets alarm info mode.
     *
     * @param modes the modes
     */
    void setAlarmInfoMode(List<String> modes);

    /**
     * Sets alarm info type.
     *
     * @param type the type
     */
    void setAlarmInfoType(String type);

    /**
     * Sets manual alarm.
     *
     * @param state the state
     */
    void setManualAlarm(String state);

    /**
     * Gets msg push info.
     *
     * @return the msg push info
     */
    MsgPushInfo getMsgPushInfo();

    /**
     * Gets last alarm info.
     *
     * @return the last alarm info
     */
    LastAlarmInfo getLastAlarmInfo();

    /**
     * Gets intrusion detection info.
     *
     * @return the intrusion detection info
     */
    IntrusionDetectionInfo getIntrusionDetectionInfo();

    /**
     * Sets intrusion detect enabled.
     *
     * @param state the state
     */
    void setIntrusionDetectEnabled(String state);

    void setTargetTrackEnabled(String state);

    /**
     * Gets line crossing detection info.
     *
     * @return the line crossing detection info
     */
    LineCrossingDetectionInfo getLineCrossingDetectionInfo();

    /**
     * Sets line crossing detect enabled.
     *
     * @param state the state
     */
    void setLineCrossingDetectEnabled(String state);

    /**
     * Gets motion detection info.
     *
     * @return the motion detection info
     */
    MotionDetection getMotionDetectionInfo();

    /**
     * Sets motion detect enabled.
     *
     * @param state the state
     */
    void setMotionDetectEnabled(String state);

    /**
     * Sets motion detect sensitivity.
     *
     * @param state the state
     */
    void setMotionDetectSensitivity(String state);

    /**
     * Sets motion detect sensitivity.
     *
     * @param state the state
     */
    void setMotionDetectSensitivity(Integer state);

    void setMotionDetectEnhanced(String state);

    /**
     * Gets person detection info.
     *
     * @return the person detection info
     */
    PersonDetectionInfo getPersonDetectionInfo();

    /**
     * Sets person detect enabled.
     *
     * @param state the state
     */
    void setPersonDetectEnabled(String state);

    /**
     * Sets person detect sensitivity.
     *
     * @param state the state
     */
    void setPersonDetectSensitivity(Integer state);

    /**
     * Gets tamper detection info.
     *
     * @return the tamper detection info
     */
    TamperDetectionInfo getTamperDetectionInfo();

    /**
     * Sets tamper detect enabled.
     *
     * @param state the state
     */
    void setTamperDetectEnabled(String state);

    /**
     * Gets preset info.
     *
     * @return the preset info
     */
    PresetInfo getPresetInfo();

    void gotoPreset(String id);

    /**
     * Gets target auto track info.
     *
     * @return the target auto track info
     */
    TargetAutoTrackInfo getTargetAutoTrackInfo();

    /**
     * Gets changeble parameters.
     *
     * @return the changeble parameters
     */
    List<ApiMethodResult> getChangebleParameters();
}
