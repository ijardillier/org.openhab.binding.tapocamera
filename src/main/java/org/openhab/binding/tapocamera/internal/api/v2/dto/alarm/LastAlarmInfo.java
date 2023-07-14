package org.openhab.binding.tapocamera.internal.api.v2.dto.alarm;

import com.google.gson.annotations.SerializedName;

public class LastAlarmInfo {
    @SerializedName("last_alarm_type")
    public String lastAlarmType; // "motion",
    @SerializedName("last_alarm_time")
    public Integer lastAlarmTime; // "1689317707"
}
