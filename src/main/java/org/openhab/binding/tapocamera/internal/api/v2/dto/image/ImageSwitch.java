package org.openhab.binding.tapocamera.internal.api.v2.dto.image;

import com.google.gson.annotations.SerializedName;

public class ImageSwitch {
    @SerializedName("switch_mode")
    public String switchMode; // "common",
    @SerializedName("schedule_start_time")
    public Integer scheduleStartTime; // "21600" - seconds GMT ?
    @SerializedName("schedule_end_time")
    public Integer scheduleEndTime; // "64800",
    @SerializedName("flip_type")
    public String flipType; // "off", "center"
    @SerializedName("rotate_type")
    public String rotateType; // "off",
    @SerializedName("ldc")
    public String lensDistortionCorrection; // "off",
    @SerializedName("night_vision_mode")
    public String nightVisionMode; // "inf_night_vision" = "on", "wtl_night_vision" = "off", "md_night_vision" = "auto"
    @SerializedName("wtl_intensity_level")
    public Integer wtlIntensityLevel; // "5"

    @Override
    public String toString() {
        return "ImageSwitch{" +
                "switchMode='" + switchMode + '\'' +
                ", scheduleStartTime=" + scheduleStartTime +
                ", scheduleEndTime=" + scheduleEndTime +
                ", flipType='" + flipType + '\'' +
                ", rotateType='" + rotateType + '\'' +
                ", lensDistortionCorrection='" + lensDistortionCorrection + '\'' +
                ", nightVisionMode='" + nightVisionMode + '\'' +
                ", wtlIntensityLevel=" + wtlIntensityLevel +
                '}';
    }
}
