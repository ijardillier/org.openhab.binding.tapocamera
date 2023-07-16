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
 * The type Device info full.
 */
public class DeviceInfoFull {
    /**
     * The Device model.
     */
    @SerializedName("device_model")
    public String deviceModel; // "C310",

    @Override
    public String toString() {
        return "DeviceInfoFull{" +
                "deviceModel='" + deviceModel + '\'' +
                ", hwVersion='" + hwVersion + '\'' +
                ", fwDescription='" + fwDescription + '\'' +
                ", productId='" + productId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", devId='" + devId + '\'' +
                ", hwId='" + hwId + '\'' +
                ", mac='" + mac + '\'' +
                ", curIspVersion='" + curIspVersion + '\'' +
                ", sensor='" + sensor + '\'' +
                ", fwCurId='" + fwCurId + '\'' +
                ", swVersion='" + swVersion + '\'' +
                ", sysSoftwareRevision='" + sysSoftwareRevision + '\'' +
                ", sysSoftwareRevisionMinor='" + sysSoftwareRevisionMinor + '\'' +
                ", deviceBase='" + deviceBase + '\'' +
                ", lensName='" + lensName + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", features=" + features +
                ", domainName='" + domainName + '\'' +
                ", language='" + language + '\'' +
                ", enableDns=" + enableDns +
                ", manufacturerName='" + manufacturerName + '\'' +
                ", friendlyName='" + friendlyName + '\'' +
                ", modelDescription='" + modelDescription + '\'' +
                ", manufacturerUrl='" + manufacturerUrl + '\'' +
                ", vendorId='" + vendorId + '\'' +
                ", zoneCode='" + zoneCode + '\'' +
                ", roiRegNum=" + roiRegNum +
                ", coverRegNum=" + coverRegNum +
                ", mdRegNum=" + mdRegNum +
                ", tdRegNum=" + tdRegNum +
                ", idRegNum=" + idRegNum +
                ", cdRegNum=" + cdRegNum +
                ", acRegNum=" + acRegNum +
                ", pluginObtainWay='" + pluginObtainWay + '\'' +
                ", productType='" + productType + '\'' +
                ", fwSharedPrefix='" + fwSharedPrefix + '\'' +
                ", extFwUpgrade=" + extFwUpgrade +
                '}';
    }

    /**
     * The Hw version.
     */
    @SerializedName("hw_version")
    public String hwVersion; // "2.0",
    /**
     * The Fw description.
     */
    @SerializedName("fw_description")
    public String fwDescription; // "C310 2.0",
    /**
     * The Product id.
     */
    @SerializedName("product_id")
    public String productId; // "00c31020",
    /**
     * The Device name.
     */
    @SerializedName("device_name")
    public String deviceName; // "C310 2.0",
    /**
     * The Device info.
     */
    @SerializedName("device_info")
    public String deviceInfo; // "C310 2.0 IPC",
    /**
     * The Dev id.
     */
    @SerializedName("dev_id")
    public String devId; // "8021D929848CDC9FE26ACFDDCBA219D020A82738",
    /**
     * The Hw id.
     */
    @SerializedName("hw_id")
    public String hwId; // "4F7FF895442ABA53E14CD582ED5BA10F",
    /**
     * The Mac.
     */
    public String mac; // "30-DE-4B-50-21-54",
    /**
     * The Cur isp version.
     */
    @SerializedName("cur_isp_version")
    public String curIspVersion; // "2",
    /**
     * The Sensor.
     */
    public String sensor; // "SC3338",
    /**
     * The Fw cur id.
     */
    @SerializedName("fw_cur_id")
    public String fwCurId; // "E4AA8AA9F0244830363D45108A493DEE",
    /**
     * The Sw version.
     */
    @SerializedName("sw_version")
    public String swVersion; // "1.3.5 Build 230426 Rel.65257n(4555)",
    /**
     * The Sys software revision.
     */
    @SerializedName("sys_software_revision")
    public String sysSoftwareRevision; // "0x500a0103",
    /**
     * The Sys software revision minor.
     */
    @SerializedName("sys_software_revision_minor")
    public String sysSoftwareRevisionMinor; // "0x0005",
    /**
     * The Device base.
     */
    @SerializedName("device_base")
    public String deviceBase; // "C310",
    /**
     * The Lens name.
     */
    @SerializedName("lens_name")
    public String lensName; // "C7327Z", C310 - support, C200 - not support
    /**
     * The Device type.
     */
    @SerializedName("device_type")
    public String deviceType; // "SMART.IPCAMERA",
    /**
     * The Features.
     */
    public Integer features; // "3",
    /**
     * The Domain name.
     */
    @SerializedName("domain_name")
    public String domainName; // "tplogin.cn",
    /**
     * The Language.
     */
    public String language; // "EN",
    /**
     * The Enable dns.
     */
    @SerializedName("enable_dns")
    public Integer enableDns; // "1",
    /**
     * The Manufacturer name.
     */
    @SerializedName("manufacturer_name")
    public String manufacturerName; // "TP-LINK",
    /**
     * The Friendly name.
     */
    @SerializedName("friendly_name")
    public String friendlyName; // "IPC",
    /**
     * The Model description.
     */
    @SerializedName("model_description")
    public String modelDescription; // "IPC",
    /**
     * The Manufacturer url.
     */
    @SerializedName("manufacturer_url")
    public String manufacturerUrl; // "http://www.tp-link.com",
    /**
     * The Vendor id.
     */
    @SerializedName("vendor_id")
    public String vendorId; // "0x00000001",
    /**
     * The Zone code.
     */
    @SerializedName("zone_code")
    public String zoneCode; // "0x0",
    /**
     * The Roi reg num.
     */
    @SerializedName("roi_reg_num")
    public Integer roiRegNum; // "1",
    /**
     * The Cover reg num.
     */
    @SerializedName("cover_reg_num")
    public Integer coverRegNum; // "4",
    /**
     * The Md reg num.
     */
    @SerializedName("md_reg_num")
    public Integer mdRegNum; // "32",
    /**
     * The Td reg num.
     */
    @SerializedName("td_reg_num")
    public Integer tdRegNum; // "1",
    /**
     * The Id reg num.
     */
    @SerializedName("id_reg_num")
    public Integer idRegNum; // "4",
    /**
     * The Cd reg num.
     */
    @SerializedName("cd_reg_num")
    public Integer cdRegNum; // "4",
    /**
     * The Ac reg num.
     */
    @SerializedName("ac_reg_num")
    public Integer acRegNum; // "1",
    /**
     * The Plugin obtain way.
     */
    @SerializedName("plugin_obtain_way")
    public String pluginObtainWay; // "web",
    /**
     * The Product type.
     */
    @SerializedName("product_type")
    public String productType; // "ipc",
    /**
     * The Fw shared prefix.
     */
    @SerializedName("fw_shared_prefix")
    public String fwSharedPrefix; // "Tapo_C310v2",
    /**
     * The Ext fw upgrade.
     */
    @SerializedName("ext_fw_upgrade")
    public Integer extFwUpgrade; // "1"

}
