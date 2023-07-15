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

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * The type Device info.
 */
public class DeviceInfo implements Serializable {
    private static final long serialVersionUID = 418701930052854366L;
    /**
     * The Basic.
     */
    @SerializedName("basic_info")
    public DeviceInfoBasic basic;
    /**
     * The Full.
     */
    @SerializedName("info")
    public DeviceInfoFull full;


    /*
    info:
        "device_model": "C310"
        "hw_version": "2.0"
        "product_id": "00c31020"
        "dev_id": "8021D929848CDC9FE26ACFDDCBA219D020A82738"
        "hw_id": "4F7FF895442ABA53E14CD582ED5BA10F"
        "mac": "30-DE-4B-50-21-54"
        "device_type": "SMART.IPCAMERA"
        "sw_version": "1.3.5 Build 230426 Rel.65257n(4555)"
        "device_base": "C310"

        "sensor": "SC3338"
        "lens_name": "C7327Z"

        "features": "3"
        "manufacturer_name": "TP-LINK",

     basic_info:
        "ffs": false
        "device_alias": "Tapo_Camera_2154"
        "avatar": "Home"
        "is_cal": true
     */
}
