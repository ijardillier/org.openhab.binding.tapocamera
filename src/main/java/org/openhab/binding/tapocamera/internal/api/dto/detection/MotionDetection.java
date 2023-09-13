/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.tapocamera.internal.api.dto.detection;

import com.google.gson.annotations.SerializedName;

/**
 * The type Motion detection.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public class MotionDetection extends DetectionInfo {

    public String enhanced; // C200 - support (галкка Обнаружение людей), C310 - not support

    /**
     * The Digital sensitivity.
     */
    @SerializedName("digital_sensitivity")
    public Integer digitalSensitivity; // "20", 0 - 100 // C200 has only 3 position (20, 60, 80), C310 - 10 positions
    /**
     * The Sensitivity.
     */
    public String sensitivity; // "low", "medium", "high"

    @Override
    public String toString() {
        return "MotionDetection{" + "enhanced='" + enhanced + '\'' + ", digitalSensitivity=" + digitalSensitivity
                + ", sensitivity='" + sensitivity + '\'' + ", enabled='" + enabled + '\'' + '}';
    }
}
