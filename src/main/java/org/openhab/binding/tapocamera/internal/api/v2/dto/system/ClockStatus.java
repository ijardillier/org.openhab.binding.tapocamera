package org.openhab.binding.tapocamera.internal.api.v2.dto.system;

import com.google.gson.annotations.SerializedName;

public class ClockStatus {
    @SerializedName("local_time")
    public String localTime;
    @SerializedName("seconds_from_1970")
    public Long secondsFrom1970;
}
