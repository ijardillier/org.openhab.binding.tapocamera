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
 * The type Image switch.
 */
public class ImageSwitch {
    /**
     * The Switch mode.
     */
    @SerializedName("switch_mode")
    public String switchMode; // "common",
    /**
     * The Schedule start time.
     */
    @SerializedName("schedule_start_time")
    public Integer scheduleStartTime; // "21600" - seconds GMT ?
    /**
     * The Schedule end time.
     */
    @SerializedName("schedule_end_time")
    public Integer scheduleEndTime; // "64800",
    /**
     * The Flip type.
     */
    @SerializedName("flip_type")
    public String flipType; // "off", "center"
    /**
     * The Rotate type.
     */
    @SerializedName("rotate_type")
    public String rotateType; // "off",
    /**
     * The Lens distortion correction.
     */
    @SerializedName("ldc")
    public String lensDistortionCorrection; // "off",
    /**
     * The Night vision mode.
     */
    @SerializedName("night_vision_mode")
    public String nightVisionMode; // "inf_night_vision" = "on", "wtl_night_vision" = "off", "md_night_vision" = "auto"
    /**
     * The Wtl intensity level.
     */
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
