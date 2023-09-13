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

import java.util.Arrays;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.tapocamera.internal.api.dto.PresetInfo;
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
import org.openhab.binding.tapocamera.internal.api.dto.detection.TargetAutoTrackInfo;
import org.openhab.binding.tapocamera.internal.api.dto.firmware.FirmwareUpdateInfo;
import org.openhab.binding.tapocamera.internal.api.dto.firmware.FirmwareUpdateStatus;
import org.openhab.binding.tapocamera.internal.api.dto.image.ImageCommon;
import org.openhab.binding.tapocamera.internal.api.dto.image.ImageSwitch;
import org.openhab.binding.tapocamera.internal.api.dto.image.LensMaskInfo;
import org.openhab.binding.tapocamera.internal.api.dto.system.ClockStatus;
import org.openhab.binding.tapocamera.internal.api.dto.system.DeviceInfoBasic;
import org.openhab.binding.tapocamera.internal.api.dto.system.DeviceInfoFull;
import org.openhab.binding.tapocamera.internal.api.dto.system.HardDiskInfo;
import org.openhab.binding.tapocamera.internal.api.dto.system.LedStatus;
import org.openhab.binding.tapocamera.internal.api.dto.system.ModuleSpec;
import org.openhab.binding.tapocamera.internal.api.dto.system.NetworkInfo;

/**
 * The enum Api method types.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
@NonNullByDefault
public enum ApiMethodTypes {
    /**
     * Device info basic api method types.
     */
    DEVICE_INFO_BASIC("getDeviceInfo", "device_info", "basic_info", DeviceInfoBasic.class),
    /**
     * Device info full api method types.
     */
    DEVICE_INFO_FULL("getDeviceInfo", "device_info", "info", DeviceInfoFull.class),
    /**
     * Wan info api method types.
     */
    WAN_INFO("get", "network", "wan", NetworkInfo.class),
    /**
     * Connection type api method types.
     */
    CONNECTION_TYPE("getConnectionType", "network", "get_connection_type", String/* ConnectionType */.class),
    /**
     * Motion detection api method types.
     */
    MOTION_DETECTION("getDetectionConfig", "motion_detection", "motion_det", MotionDetection.class),
    /**
     * Msg alarm manual api method types.
     */
    MSG_ALARM_MANUAL("do", "msg_alarm", "manual_msg_alarm", String.class),
    /**
     * Msg alarm info api method types.
     */
    MSG_ALARM_INFO("getAlertConfig", "msg_alarm", "chn1_msg_alarm_info", MsgAlarmInfo.class),
    /**
     * Night vision mode api method types.
     */
    IMAGE_ROTATION_STATUS("getRotationStatus", "image", "switch", ImageSwitch.class),
    /**
     * Light frequency info api method types.
     */
    LIGHT_FREQUENCY_INFO("getLightFrequencyInfo", "image", "common", ImageCommon.class),
    /**
     * Led status api method types.
     */
    LED_STATUS("getLedStatus", "led", "config", LedStatus.class),
    /**
     * Clock status api method types.
     */
    CLOCK_STATUS("getClockStatus", "system", "clock_status", ClockStatus.class),
    /**
     * Msg push info api method types.
     */
    MSG_PUSH_INFO("getMsgPushConfig", "msg_push", "chn1_msg_push_info", MsgPushInfo.class),
    /**
     * Lens mask api method types.
     */
    LENS_MASK("getLensMaskConfig", "lens_mask", "lens_mask_info", LensMaskInfo.class),
    /**
     * Target track api method types.
     */
    TARGET_TRACK("getTargetTrackConfig", "target_track", "target_track_info", TargetAutoTrackInfo.class),
    /**
     * Presets api method types.
     */
    PRESETS("getPresetConfig", "preset", "preset", PresetInfo.class),
    GOTO_PRESETS("motorMoveToPreset", "preset", "goto_preset", PresetInfo.class),
    /**
     * Firmware update status api method types.
     */
    FIRMWARE_UPDATE_STATUS("getFirmwareUpdateStatus", "cloud_config", "upgrade_status", FirmwareUpdateStatus.class),
    /**
     * Firmware update info api method types.
     */
    FIRMWARE_UPDATE_INFO("getFirmwareUpdateStatus", "cloud_status", "client_info", FirmwareUpdateInfo.class),
    /**
     * Firmware check api method types.
     */
    FIRMWARE_CHECK("checkFirmwareVersionByCloud", "cloud_config", "check_fw_version", String.class),
    /**
     * Speaker info api method types.
     */
    SPEAKER_INFO("getAudioConfig", "audio_config", "speaker", AudioSpeakerInfo.class),
    /**
     * Microphone info api method types.
     */
    MICROPHONE_INFO("getAudioConfig", "audio_config", "microphone", AudioMicrophoneInfo.class),
    /**
     * Tamper detection api method types.
     */
    TAMPER_DETECTION("getTamperDetectionConfig", "tamper_detection", "tamper_det", TamperDetectionInfo.class),
    /**
     * Intrusion detection api method types.
     */
    INTRUSION_DETECTION("getIntrusionDetectionConfig", "intrusion_detection", "detection",
            IntrusionDetectionInfo.class),
    /**
     * Linecrossing detection api method types.
     */
    LINECROSSING_DETECTION("getLinecrossingDetectionConfig", "linecrossing_detection", "detection",
            LineCrossingDetectionInfo.class),
    /**
     * Person detection api method types.
     */
    PERSON_DETECTION("getPersonDetectionConfig", "people_detection", "detection", PersonDetectionInfo.class),
    /**
     * Hard disk info api method types.
     */
    HARD_DISK_INFO("getSdCardStatus", "harddisk_manage", "hd_info", HardDiskInfo.class),
    /**
     * Last alarm info api method types.
     */
    LAST_ALARM_INFO("getLastAlarmInfo", "system", "last_alarm_info", LastAlarmInfo.class),
    /**
     * User id api method types.
     */
    USER_ID("getUserID", "system", "get_user_id", String.class),
    MODULES_SPEC("get", "function", "module_spec", ModuleSpec.class),
    /**
     * Unknown api method types.
     */
    UNKNOWN("unknown", "unknown", "unknown", String.class),;

    private String method;
    private String module;
    private String section;
    private Class<?> clazz;

    ApiMethodTypes(String method, String module, String section, Class<?> clazz) {
        this.method = method;
        this.module = module;
        this.section = section;
        this.clazz = clazz;
    }

    /**
     * Gets method.
     *
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets method.
     *
     * @param method the method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Gets module.
     *
     * @return the module
     */
    public String getModule() {
        return module;
    }

    /**
     * Sets module.
     *
     * @param module the module
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * Gets section.
     *
     * @return the section
     */
    public String getSection() {
        return section;
    }

    /**
     * Sets section.
     *
     * @param section the section
     */
    public void setSection(String section) {
        this.section = section;
    }

    /**
     * Gets clazz.
     *
     * @return the clazz
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * Sets clazz.
     *
     * @param clazz the clazz
     */
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * Gets class by module and section.
     *
     * @param module the module
     * @param section the section
     * @return the class by module and section
     */
    public static Class<?> getClassByModuleAndSection(String module, String section) {
        return Arrays.stream(ApiMethodTypes.values()).filter(v -> v.module.equals(module) && v.section.equals(section))
                .findFirst().orElse(UNKNOWN).getClazz();
    }
}
