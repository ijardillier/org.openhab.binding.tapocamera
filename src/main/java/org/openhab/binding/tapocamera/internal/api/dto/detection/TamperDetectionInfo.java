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
 * The type Tamper detection info.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public class TamperDetectionInfo extends DetectionInfo {
    @Override
    public String toString() {
        return "TamperDetectionInfo{" + "sensitivity='" + sensitivity + '\'' + ", digitalSensitivity="
                + digitalSensitivity + ", enabled='" + enabled + '\'' + '}';
    }

    /**
     * The Sensitivity.
     */
    public String sensitivity;
    /**
     * The Digital sensitivity.
     */
    @SerializedName("digital_sensitivity")
    public Integer digitalSensitivity;
}
