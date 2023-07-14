package org.openhab.binding.tapocamera.internal.api.v2.dto.image;

import com.google.gson.annotations.SerializedName;

public class ImageCommon {
    public Integer luma; // "50",
    public Integer contrast; // "50",
    public Integer chroma; // "50",
    public Integer saturation; // "50",
    public Integer sharpness; // "50",
    @SerializedName("exp_type")
    public String expType; // "auto",
    public String shutter; // "1/25",
    @SerializedName("focus_type")
    public String focusType; // "semi_auto",
    @SerializedName("focus_limited")
    public Integer focusLimited; // "600",
    @SerializedName("exp_gain")
    public Integer expGain; // "0",
    @SerializedName("inf_start_time")
    public Integer infStartTime; // "64800",
    @SerializedName("inf_end_time")
    public Integer infEndTime; // "21600",
    @SerializedName("inf_sensitivity")
    public Integer infSensitivity; // "4",
    @SerializedName("inf_delay")
    public Integer infDelay; // "5",
    @SerializedName("wide_dynamic")
    public String wideDynamic; // "off",
    @SerializedName("light_freq_mode")
    public String lightFreqMode; // "auto",
    public Integer wd_gain; // "50",
    public String wb_type; // "auto",
    public Integer wb_R_gain; // "50",
    public Integer wb_G_gain; // "50",
    public Integer wb_B_gain; // "50",
    public Integer lock_red_gain; // "0",
    public Integer lock_gr_gain; // "0",
    public Integer lock_gb_gain; // "0",
    public Integer lock_blue_gain; // "0",
    public Integer lock_red_colton; // "0",
    public Integer lock_green_colton; // "0",
    public Integer lock_blue_colton; // "0",
    public String lock_source; // "local",
    public String area_compensation; // "default",
    public String smartir; // "off",
    public Integer smartir_level; // "100",
    public String high_light_compensation; // "off",
    public String dehaze; // "off",
    public String inf_type; // "off"

    @Override
    public String toString() {
        return "ImageCommon{" +
                "luma=" + luma +
                ", contrast=" + contrast +
                ", chroma=" + chroma +
                ", saturation=" + saturation +
                ", sharpness=" + sharpness +
                ", expType='" + expType + '\'' +
                ", shutter='" + shutter + '\'' +
                ", focusType='" + focusType + '\'' +
                ", focusLimited=" + focusLimited +
                ", expGain=" + expGain +
                ", infStartTime=" + infStartTime +
                ", infEndTime=" + infEndTime +
                ", infSensitivity=" + infSensitivity +
                ", infDelay=" + infDelay +
                ", wideDynamic='" + wideDynamic + '\'' +
                ", lightFreqMode='" + lightFreqMode + '\'' +
                ", wd_gain=" + wd_gain +
                ", wb_type='" + wb_type + '\'' +
                ", wb_R_gain=" + wb_R_gain +
                ", wb_G_gain=" + wb_G_gain +
                ", wb_B_gain=" + wb_B_gain +
                ", lock_red_gain=" + lock_red_gain +
                ", lock_gr_gain=" + lock_gr_gain +
                ", lock_gb_gain=" + lock_gb_gain +
                ", lock_blue_gain=" + lock_blue_gain +
                ", lock_red_colton=" + lock_red_colton +
                ", lock_green_colton=" + lock_green_colton +
                ", lock_blue_colton=" + lock_blue_colton +
                ", lock_source='" + lock_source + '\'' +
                ", area_compensation='" + area_compensation + '\'' +
                ", smartir='" + smartir + '\'' +
                ", smartir_level=" + smartir_level +
                ", high_light_compensation='" + high_light_compensation + '\'' +
                ", dehaze='" + dehaze + '\'' +
                ", inf_type='" + inf_type + '\'' +
                '}';
    }
}
