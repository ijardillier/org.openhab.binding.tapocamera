package org.openhab.binding.tapocamera.internal;

import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_GROUP_ALARM_CONTROL;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_GROUP_DETECTIONS;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_GROUP_SYSTEM;

public enum TapoCameraChannels {
    // system group
    CHANNEL_LED_STATUS("led-status", CHANNEL_GROUP_SYSTEM),
    CHANNEL_MANUAL_ALARM("manual-alarm", CHANNEL_GROUP_SYSTEM),
    CHANNEL_SPEAKER_VOLUME("speaker-volume", CHANNEL_GROUP_SYSTEM),
    CHANNEL_MICROPHONE_VOLUME("microphone-volume", CHANNEL_GROUP_SYSTEM),

    // alarm control group
    CHANNEL_ALARM_ENABLED("alarm-enabled", CHANNEL_GROUP_ALARM_CONTROL),
    CHANNEL_ALARM_TYPE("alarm-type", CHANNEL_GROUP_ALARM_CONTROL),
    CHANNEL_ALARM_MODE("alarm-mode", CHANNEL_GROUP_ALARM_CONTROL),
    CHANNEL_ALARM_LIGHT_TYPE("alarm-light-type", CHANNEL_GROUP_ALARM_CONTROL),

    // detections group
    CHANNEL_MOTION_DETECTION_ENABLED("motion-detection-enabled", CHANNEL_GROUP_DETECTIONS),
    CHANNEL_MOTION_DETECTION_SENSITIVITY("motion-detection-sensitivity", CHANNEL_GROUP_DETECTIONS),
    CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY("motion-detection-digital-sensitivity", CHANNEL_GROUP_DETECTIONS),
    CHANNEL_PEOPLE_DETECTION_ENABLED("person-detection-enabled", CHANNEL_GROUP_DETECTIONS),
    CHANNEL_PEOPLE_DETECTION_SENSITIVITY("person-detection-sensitivity", CHANNEL_GROUP_DETECTIONS),
    CHANNEL_LINE_CROSSING_DETECTION_ENABLED("line-crossing-detection-enabled", CHANNEL_GROUP_DETECTIONS),
    CHANNEL_INTRUSION_DETECTION_ENABLED("intrusion-detection-enabled", CHANNEL_GROUP_DETECTIONS),

    ;

    private String channelId;
    private String groupId;

    TapoCameraChannels(String channelId, String groupId) {
        this.channelId = channelId;
        this.groupId = groupId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return groupId + "#" + channelId;
    }
}
