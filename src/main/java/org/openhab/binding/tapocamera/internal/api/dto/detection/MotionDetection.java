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

package org.openhab.binding.tapocamera.internal.api.dto.detection;

import com.google.gson.annotations.SerializedName;

/**
 * The type Motion detection.
 */
public class MotionDetection extends DetectionInfo {
    /**
     * The Digital sensitivity.
     */
    @SerializedName("digital_sensitivity")
    public Integer digitalSensitivity; // "20", 0 - 100
    /**
     * The Sensitivity.
     */
    public String sensitivity; // "low", "medium", "high"

    @Override
    public String toString() {
        return "MotionDetection{" +
                "digitalSensitivity=" + digitalSensitivity +
                ", sensitivity='" + sensitivity + '\'' +
                ", enabled='" + enabled + '\'' +
                '}';
    }
}