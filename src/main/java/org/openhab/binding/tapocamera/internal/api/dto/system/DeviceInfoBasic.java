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
 * The type Device info basic.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public class DeviceInfoBasic {

    /**
     * The manufacturer name. Ex : "TP-LINK"
     */
    @SerializedName("manufacturer_name")
    public String manufacturerName;

    /**
     * The device type. Ex : "SMART.IPCAMERA"
     */
    @SerializedName("device_type")
    public String deviceType;

    /**
     * The device model. Ex : "C325WB"
     */
    @SerializedName("device_model")
    public String deviceModel;

    /**
     * The device name. Ex : "C325WB 1.0"
     */
    @SerializedName("device_name")
    public String deviceName;

    /**
     * The device info. Ex : "C325WB 1.0 IPC"
     */
    @SerializedName("device_info")
    public String deviceInfo;

    /**
     * The device alias. Ex : "Caméra - Parking"
     */
    @SerializedName("device_alias")
    public String deviceAlias;

    /**
     * The region. Ex : "EU"
     */
    @SerializedName("region")
    public String region;

    /**
     * The avatar. Ex :
     */
    public String avatar; // "camera c310"

    /**
     * The MAC address. Ex : "3C-52-A1-A4-44-01"
     */
    @SerializedName("mac")
    public String macAddress;

    /**
     * The hardware version. Ex : "1.0"
     */
    @SerializedName("hw_version")
    public String hwVersion;

    /**
     * The software version. Ex : "1.2.4 Build 240822 Rel.58658n"
     */
    @SerializedName("sw_version")
    public String swVersion;

    /**
     * The dev id. Ex : "802198753A6D25AFD31E70151AFE1A6421B42D55"
     */
    @SerializedName("dev_id")
    public String devId;

    /**
     * The hardware id. Ex : "27E58F86343A05F1F3A90E5F2F7952CA"
     */
    @SerializedName("hw_id")
    public String hwId;

    /**
     * The hardware desc. Ex : "00000000000000000000000000000000"
     */
    @SerializedName("hw_desc")
    public String hwDesc;

    /**
     * The OEM id. Ex : "45A364BF130D6772384832DF1C48546B"
     */
    @SerializedName("oem_id")
    public String oemId;

    /**
     * The features. Ex : 3
     */
    public Integer features;

    /**
     * The barcode. Ex : ""
     */
    public String barcode;

    /**
     * The mobilr access. Ex : "0"
     */
    @SerializedName("mobile_access")
    public String mobileAccess;

    /**
     * The ffs. Ex : false.
     */
    public Boolean ffs;

    /**
     * The is cal. Ex : true
     */
    @SerializedName("is_cal")
    public Boolean isCal;

    /**
     * The not RTSP constrain. Ex : 1
     */
    @SerializedName("no_rtsp_constrain")
    public Integer noRtspConstrain;

    /**
     * The has set location info. Ex : 1
     */
    @SerializedName("has_set_location_info")
    public Integer hasSetLocationInfo;

    /**
     * The longitude. Ex : 31662
     */
    public Long longitude;

    /**
     * The latitude. Ex : 456822
     */
    public Long latitude;

    @Override
    public String toString() {
        return "DeviceInfoBasic{" + "manufacturerName=" + manufacturerName + '\'' + ", deviceType='" + deviceType + '\''
                + ", deviceInfo='" + deviceInfo + '\'' + ", deviceAlias='" + deviceAlias + '\'' + '}';
    }
}
