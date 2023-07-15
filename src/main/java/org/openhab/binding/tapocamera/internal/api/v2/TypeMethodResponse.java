package org.openhab.binding.tapocamera.internal.api.v2;

import java.util.Arrays;

import org.openhab.binding.tapocamera.internal.api.v2.dto.PresetInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.TargetAutoTrackInfo;
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
import org.openhab.binding.tapocamera.internal.api.v2.dto.firmware.FirmwareUpdateInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.firmware.FirmwareUpdateStatus;
import org.openhab.binding.tapocamera.internal.api.v2.dto.image.ImageCommon;
import org.openhab.binding.tapocamera.internal.api.v2.dto.image.ImageSwitch;
import org.openhab.binding.tapocamera.internal.api.v2.dto.image.LensMaskInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.ClockStatus;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.DeviceInfoBasic;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.DeviceInfoFull;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.HardDiskInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.LedStatus;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.NetworkInfo;

public enum TypeMethodResponse {
    DEVICE_INFO_BASIC("getDeviceInfo", "device_info", "basic_info", DeviceInfoBasic.class),
    DEVICE_INFO_FULL("getDeviceInfo", "device_info", "info", DeviceInfoFull.class),
    WAN_INFO("get", "network", "wan", NetworkInfo.class),
    CONNECTION_TYPE("getConnectionType", "network", "get_connection_type", String.class),
    MOTION_DETECTION("getDetectionConfig", "motion_detection", "motion_det", MotionDetection.class),
    MSG_ALARM_MANUAL("do", "msg_alarm", "manual_msg_alarm", String.class),
    MSG_ALARM_INFO("getAlertConfig", "msg_alarm", "chn1_msg_alarm_info", MsgAlarmInfo.class),
    NIGHT_VISION_MODE("getNightVisionModeConfig", "image", "switch", ImageSwitch.class),
    LIGHT_FREQUENCY_INFO("getLightFrequencyInfo", "image", "common", ImageCommon.class),
    LED_STATUS("getLedStatus", "led", "config", LedStatus.class),
    CLOCK_STATUS("getClockStatus", "system", "clock_status", ClockStatus.class),
    MSG_PUSH_INFO("getMsgPushConfig", "msg_push", "chn1_msg_push_info", MsgPushInfo.class),
    LENS_MASK("getLensMaskConfig", "lens_mask", "lens_mask_info", LensMaskInfo.class),
    TARGET_TRACK("getTargetTrackConfig", "target_track", "target_track_info", TargetAutoTrackInfo.class),
    PRESETS("getPresetConfig", "preset", "preset", PresetInfo.class),
    FIRMWARE_UPDATE_STATUS("getFirmwareUpdateStatus", "cloud_config", "upgrade_status", FirmwareUpdateStatus.class),
    FIRMWARE_UPDATE_INFO("getFirmwareUpdateStatus", "cloud_status", "client_info", FirmwareUpdateInfo.class),
    FIRMWARE_CHECK("checkFirmwareVersionByCloud", "cloud_config", "check_fw_version", String.class),
    SPEAKER_INFO("getAudioConfig", "audio_config", "speaker", AudioSpeakerInfo.class),
    MICROPHONE_INFO("getAudioConfig", "audio_config", "microphone", AudioMicrophoneInfo.class),
    TAMPER_DETECTION("getTamperDetectionConfig", "tamper_detection", "tamper_det", TamperDetectionInfo.class),
    INTRUSION_DETECTION("getIntrusionDetectionConfig", "intrusion_detection", "detection", IntrusionDetectionInfo.class),
    LINECROSSING_DETECTION("getLinecrossingDetectionConfig", "linecrossing_detection", "detection", LineCrossingDetectionInfo.class),
    PERSON_DETECTION("getPersonDetectionConfig", "people_detection", "detection", PersonDetectionInfo.class),
    HARD_DISK_INFO("getSdCardStatus", "harddisk_manage", "hd_info", HardDiskInfo.class),
    LAST_ALARM_INFO("getLastAlarmInfo", "system", "last_alarm_info", LastAlarmInfo.class),
    USER_ID("getUserID", "system", "get_user_id", String.class),
    UNKNOWN("unknown", "unknown", "unknown", String.class),
    ;

    private String method;
    private String module;
    private String section;
    private Class<?> clazz;
    TypeMethodResponse(String method, String module, String section, Class<?> clazz) {
        this.method = method;
        this.module = module;
        this.section = section;
        this.clazz = clazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static Class<?> getClassByModuleAndSection(String module, String section) {
        return Arrays.stream(TypeMethodResponse.values())
                .filter(v -> v.module.equals(module) && v.section.equals(section))
                .findFirst().orElse(UNKNOWN).getClazz();
    }
}
