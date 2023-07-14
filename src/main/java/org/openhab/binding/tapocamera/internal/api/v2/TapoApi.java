package org.openhab.binding.tapocamera.internal.api.v2;

import java.util.List;

import org.openhab.binding.tapocamera.internal.api.ApiException;
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
import org.openhab.binding.tapocamera.internal.api.v2.dto.image.ImageCommon;
import org.openhab.binding.tapocamera.internal.api.v2.dto.image.ImageSwitch;
import org.openhab.binding.tapocamera.internal.api.v2.dto.image.LensMaskInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.ClockStatus;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.DeviceInfo;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.LedStatus;
import org.openhab.binding.tapocamera.internal.api.v2.dto.system.NetworkInfo;

public interface TapoApi {
    void setHostname(String hostname);
    Boolean auth(String username, String password) throws ApiException;

    Boolean isAuth();

    ApiResponse sendMultipleRequest(String token, String data);

    Object sendSingleRequest(String token, String data);

    Object processSingleResponse(Object data, String moduleName, String section);
    List<Object> processSingleResponse(Object data, String moduleName, List<String> sections);

    Class<?> getParameterInfo(TypeMethodResponse type);

    DeviceInfo getDeviceInfo();
    NetworkInfo getNetworkInfo();

    AudioSpeakerInfo getSpeakerInfo();
    void setSpeakerVolume(int volume);

    AudioMicrophoneInfo getMicrophoneInfo();
    void setMicrophoneVolume(int volume);

    ClockStatus getClockStatus();

    ImageCommon getImageCommonInfo();
    ImageSwitch getImageSwitchInfo();
    LensMaskInfo getLensMaskInfo();

    LedStatus getLedStatus();
    void setLedStatus(String status);

    MsgAlarmInfo getMsgAlarmInfo();
    MsgPushInfo getMsgPushInfo();

    LastAlarmInfo getLastAlarmInfo();

    IntrusionDetectionInfo getIntrusionDetectionInfo();
    LineCrossingDetectionInfo getLineCrossingDetectionInfo();
    MotionDetection getMotionDetectionInfo();
    PersonDetectionInfo getPersonDetectionInfo();
    TamperDetectionInfo getTamperDetectionInfo();

    PresetInfo getPresetInfo();
    TargetAutoTrackInfo getTargetAutoTrackInfo();

    List<ApiMethodResult> getChangebleParameters();
}
