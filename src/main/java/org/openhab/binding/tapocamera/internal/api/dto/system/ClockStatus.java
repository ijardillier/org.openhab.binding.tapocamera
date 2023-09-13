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
package org.openhab.binding.tapocamera.internal.api.dto.system;

import com.google.gson.annotations.SerializedName;

/**
 * The type Clock status.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public class ClockStatus {
    /**
     * The Local time.
     */
    @SerializedName("local_time")
    public String localTime;

    @Override
    public String toString() {
        return "ClockStatus{" + "localTime='" + localTime + '\'' + ", secondsFrom1970=" + secondsFrom1970 + '}';
    }

    /**
     * The Seconds from 1970.
     */
    @SerializedName("seconds_from_1970")
    public Long secondsFrom1970;
}
