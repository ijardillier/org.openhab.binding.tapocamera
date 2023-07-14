package org.openhab.binding.tapocamera.internal.api.v2.dto.detection;

import com.google.gson.annotations.SerializedName;

public class MotionDetection extends DetectionInfo {
    @SerializedName("digital_sensitivity")
    public Integer digitalSensitivity; // "20", 0 - 100
    public String sensitivity; // "low", "medium", "high"

    @Override
    public String toString() {
        return "Old_MotionDetection{" +
                "digitalSensitivity=" + digitalSensitivity +
                ", sensitivity='" + sensitivity + '\'' +
                ", enabled='" + enabled + '\'' +
                '}';
    }
}