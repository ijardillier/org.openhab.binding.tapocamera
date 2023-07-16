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

/**
 * The type Device info basic.
 */
public class DeviceInfoBasic {
    /**
     * The Ffs.
     */
    public Boolean ffs;  // ??? C200 - no, C310 - false

    @Override
    public String toString() {
        return "DeviceInfoBasic{" +
                "ffs=" + ffs +
                ", deviceType='" + deviceType + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", hwVersion='" + hwVersion + '\'' +
                ", swVersion='" + swVersion + '\'' +
                ", deviceAlias='" + deviceAlias + '\'' +
                ", avatar='" + avatar + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", hasSetLocationInfo=" + hasSetLocationInfo +
                ", features=" + features +
                ", barcode='" + barcode + '\'' +
                ", mac='" + mac + '\'' +
                ", devId='" + devId + '\'' +
                ", oemId='" + oemId + '\'' +
                ", hwDesc='" + hwDesc + '\'' +
                ", isCal=" + isCal +
                '}';
    }

    /**
     * The Device type.
     */
    @SerializedName("device_type")
    public String deviceType; //  SMART.IPCAMERA
    /**
     * The Device model.
     */
    @SerializedName("device_model")
    public String deviceModel; // C310
    /**
     * The Device name.
     */
    @SerializedName("device_name")
    public String deviceName; // "C310 2.0"
    /**
     * The Device info.
     */
    @SerializedName("device_info")
    public String deviceInfo; // "C310 2.0 IPC"
    /**
     * The Hw version.
     */
    @SerializedName("hw_version")
    public String hwVersion; // "2.0"
    /**
     * The Sw version.
     */
    @SerializedName("sw_version")
    public String swVersion; // "1.3.5 Build 230426 Rel.65257n(4555)"
    /**
     * The Device alias.
     */
    @SerializedName("device_alias")
    public String deviceAlias; // "Tapo_Camera_2154"
    /**
     * The Avatar.
     */
    public String avatar; // "Home"
    /**
     * The Longitude.
     */
    public Long longitude;
    /**
     * The Latitude.
     */
    public Long latitude;
    /**
     * The Has set location info.
     */
    @SerializedName("has_set_location_info")
    public Integer hasSetLocationInfo; // 0
    /**
     * The Features.
     */
    public Integer features; // "3"
    /**
     * The Barcode.
     */
    public String barcode; // ""
    /**
     * The Mac.
     */
    public String mac; // "30-DE-4B-50-21-54"
    /**
     * The Dev id.
     */
    @SerializedName("dev_id")
    public String devId; // "8021D929848CDC9FE26ACFDDCBA219D020A82738"
    /**
     * The Oem id.
     */
    @SerializedName("oem_id")
    public String oemId; // "8254843D1E8318CB5C2BC8BF94D189FA"
    /**
     * The Hw desc.
     */
    @SerializedName("hw_desc")
    public String hwDesc; // "00000000000000000000000000000000"
    /**
     * The Is cal.
     */
    @SerializedName("is_cal")
    public Boolean isCal; // true  C310 - support, C200 - not support, but has call function

}
