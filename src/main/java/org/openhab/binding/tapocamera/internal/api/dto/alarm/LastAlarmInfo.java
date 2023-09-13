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
package org.openhab.binding.tapocamera.internal.api.dto.alarm;

import com.google.gson.annotations.SerializedName;

/**
 * The type Last alarm info.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public class LastAlarmInfo {
    /**
     * The Last alarm type.
     */
    @SerializedName("last_alarm_type")
    public String lastAlarmType; // "motion",
    /**
     * The Last alarm time.
     */
    @SerializedName("last_alarm_time")
    public Integer lastAlarmTime; // "1689317707"

    @Override
    public String toString() {
        return "LastAlarmInfo{" + "lastAlarmType='" + lastAlarmType + '\'' + ", lastAlarmTime=" + lastAlarmTime + '}';
    }
}
