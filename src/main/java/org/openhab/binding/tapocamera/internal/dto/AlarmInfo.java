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
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class AlarmInfo implements Serializable {
    @SerializedName("alarm_type")
    private String alarmType = "0";

    @SerializedName("enabled")
    private String enabled = "off";

    @SerializedName("alarm_mode")
    private List<String> alarmMode = new ArrayList<>();

    @SerializedName("light_type")
    private String lightType = "0";

    public AlarmInfo() {
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public List<String> getAlarmMode() {
        return alarmMode;
    }

    public void setAlarmMode(List<String> alarmMode) {
        this.alarmMode = alarmMode;
    }

    public String getLightType() {
        return lightType;
    }

    public void setLightType(String lightType) {
        this.lightType = lightType;
    }

    @Override
    public String toString() {
        return "AlarmInfo{" + "alarmType='" + alarmType + '\'' + ", enabled='" + enabled + '\'' + ", alarmM0ode="
                + alarmMode + ", lightType='" + lightType + '\'' + '}';
    }
}
