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

package org.openhab.binding.tapocamera.internal.api.dto.alarm;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * The type Msg alarm info.
 */
public class MsgAlarmInfo {
    /**
     * The Enabled.
     */
    public String enabled; // "off", "on"
    /**
     * The Alarm type.
     */
    @SerializedName("alarm_type")
    public Integer alarmType; // "0", "1"
    /**
     * The Alarm mode.
     */
    @SerializedName("alarm_mode")
    public List<String> alarmMode; // ["sound", "light"]
    /**
     * The Sound alarm enabled.
     */
    @SerializedName("sound_alarm_enabled")
    public String soundAlarmEnabled; // "off", "on"
    /**
     * The Light alarm enabled.
     */
    @SerializedName("light_alarm_enabled")
    public String lightAlarmEnabled; // "off", "on"
    /**
     * The Light type.
     */
    @SerializedName("light_type")
    public Integer lightType; // "0", "1"

    @Override
    public String toString() {
        return "MsgAlarmInfo{" +
                "enabled='" + enabled + '\'' +
                ", alarmType=" + alarmType +
                ", alarmMode=" + alarmMode +
                ", soundAlarmEnabled='" + soundAlarmEnabled + '\'' +
                ", lightAlarmEnabled='" + lightAlarmEnabled + '\'' +
                ", lightType=" + lightType +
                '}';
    }
}
