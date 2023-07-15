/*
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 *  SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.tapocamera.internal.api.dto.image;

import com.google.gson.annotations.SerializedName;

/**
 * The type Image common.
 */
public class ImageCommon {
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
                ", inf_type='" + nightMode + '\'' +
                '}';
    }

    /**
     * The Luma.
     */
    public Integer luma; // "50",
    /**
     * The Contrast.
     */
    public Integer contrast; // "50",
    /**
     * The Chroma.
     */
    public Integer chroma; // "50",
    /**
     * The Saturation.
     */
    public Integer saturation; // "50",
    /**
     * The Sharpness.
     */
    public Integer sharpness; // "50",
    /**
     * The Exp type.
     */
    @SerializedName("exp_type")
    public String expType; // "auto",
    /**
     * The Shutter.
     */
    public String shutter; // "1/25",
    /**
     * The Focus type.
     */
    @SerializedName("focus_type")
    public String focusType; // "semi_auto",
    /**
     * The Focus limited.
     */
    @SerializedName("focus_limited")
    public Integer focusLimited; // "600",
    /**
     * The Exp gain.
     */
    @SerializedName("exp_gain")
    public Integer expGain; // "0",
    /**
     * The Inf start time.
     */
    @SerializedName("inf_start_time")
    public Integer infStartTime; // "64800",
    /**
     * The Inf end time.
     */
    @SerializedName("inf_end_time")
    public Integer infEndTime; // "21600",
    /**
     * The Inf sensitivity.
     */
    @SerializedName("inf_sensitivity")
    public Integer infSensitivity; // "4",
    /**
     * The Inf delay.
     */
    @SerializedName("inf_delay")
    public Integer infDelay; // "5",
    /**
     * The Wide dynamic.
     */
    @SerializedName("wide_dynamic")
    public String wideDynamic; // "off",
    /**
     * The Light freq mode.
     */
    @SerializedName("light_freq_mode")
    public String lightFreqMode; // "auto",
    /**
     * The Wd gain.
     */
    public Integer wd_gain; // "50",
    /**
     * The Wb type.
     */
    public String wb_type; // "auto",
    /**
     * The Wb r gain.
     */
    public Integer wb_R_gain; // "50",
    /**
     * The Wb g gain.
     */
    public Integer wb_G_gain; // "50",
    /**
     * The Wb b gain.
     */
    public Integer wb_B_gain; // "50",
    /**
     * The Lock red gain.
     */
    public Integer lock_red_gain; // "0",
    /**
     * The Lock gr gain.
     */
    public Integer lock_gr_gain; // "0",
    /**
     * The Lock gb gain.
     */
    public Integer lock_gb_gain; // "0",
    /**
     * The Lock blue gain.
     */
    public Integer lock_blue_gain; // "0",
    /**
     * The Lock red colton.
     */
    public Integer lock_red_colton; // "0",
    /**
     * The Lock green colton.
     */
    public Integer lock_green_colton; // "0",
    /**
     * The Lock blue colton.
     */
    public Integer lock_blue_colton; // "0",
    /**
     * The Lock source.
     */
    public String lock_source; // "local",
    /**
     * The Area compensation.
     */
    public String area_compensation; // "default",
    /**
     * The Smartir.
     */
    public String smartir; // "off",
    /**
     * The Smartir level.
     */
    public Integer smartir_level; // "100",
    /**
     * The High light compensation.
     */
    public String high_light_compensation; // "off",
    /**
     * The Dehaze.
     */
    public String dehaze; // "off",
    /**
     * The Inf type.
     */
    @SerializedName("inf_type")
    public String nightMode; // "off" - day, "on" - night, "Auto" - Auto

}
