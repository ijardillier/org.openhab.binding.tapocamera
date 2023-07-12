/*
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 *  See the NOTICE file(s) distributed with this work for additional
 *  information.
 *
 * This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.tapocamera.internal.dto;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class MotionDetection implements Serializable {
    @SerializedName("enabled")
    private String enabled = "off";
    @SerializedName("sensitivity")
    private String sensitivity = "low";

    @SerializedName("digital_sensitivity")
    private String digitalSensitivity = "30";

    public MotionDetection() {
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(String sensitivity) {
        this.sensitivity = sensitivity;
    }

    public String getDigitalSensitivity() {
        return digitalSensitivity;
    }

    public void setDigitalSensitivity(String digitalSensitivity) {
        this.digitalSensitivity = digitalSensitivity;
    }

    @Override
    public String toString() {
        return "MotionDetection{" + "enabled='" + enabled + '\'' + ", sensitivity='" + sensitivity + '\''
                + ", digitalSensitivity='" + digitalSensitivity + '\'' + '}';
    }
}
