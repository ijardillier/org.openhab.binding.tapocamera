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

package org.openhab.binding.tapocamera.internal.api.dto.system;

import com.google.gson.annotations.SerializedName;

public class ConnectionType {
    @SerializedName("link_type")
    public String linkType; // "wifi", "ethernet"
    public String ssid;
    @SerializedName("rssi")
    public Integer signalLevel; // "3",
    @SerializedName("rssiValue")
    public Integer rssi; //  -51

    @Override
    public String toString() {
        return "ConnectionType{" +
                "linkType='" + linkType + '\'' +
                ", ssid='" + ssid + '\'' +
                ", signalLevel=" + signalLevel +
                ", rssi=" + rssi +
                '}';
    }
}
