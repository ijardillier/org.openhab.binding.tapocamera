package org.openhab.binding.tapocamera.internal.api.v2.dto.detection;

import com.google.gson.annotations.SerializedName;

public class TamperDetectionInfo extends DetectionInfo {
    public String sensitivity;
    @SerializedName("digital_sensitivity")
    public Integer digitalSensitivity;
}
