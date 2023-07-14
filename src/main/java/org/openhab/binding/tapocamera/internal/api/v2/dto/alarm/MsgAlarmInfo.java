package org.openhab.binding.tapocamera.internal.api.v2.dto.alarm;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MsgAlarmInfo {
    public String enabled; // "off", "on"
    @SerializedName("alarm_type")
    public Integer alarmType; // "0", "1"
    @SerializedName("alarm_mode")
    public List<String> alarmMode; // ["sound", "light"]
    @SerializedName("sound_alarm_enabled")
    public String soundAlarmEnabled; // "off", "on"
    @SerializedName("light_alarm_enabled")
    public String lightAlarmEnabled; // "off", "on"
    @SerializedName("light_type")
    public Integer lightType; // "0", "1"

    @Override
    public String toString() {
        return "MsgAlarmInfo{" +
                "enabled='" + enabled + '\'' +
                ", alarmType=" + alarmType +
                ", alarmMode=" + alarmMode +
                ", soundAlarmEnabled='" + soundAlarmEnabled + '\'' +
                ", lightAlarmEnabled='" + lightAlarmEnabled + '\'' +
                ", lightType=" + lightType +
                '}';
    }
}
