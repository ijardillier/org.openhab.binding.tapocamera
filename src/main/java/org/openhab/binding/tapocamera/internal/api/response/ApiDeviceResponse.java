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
package org.openhab.binding.tapocamera.internal.api.response;

/**
 * The type Api device response.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public class ApiDeviceResponse {
    /**
     * The Device type.
     */
    public String deviceType; // "SMART.IPCAMERA",
    /**
     * The Role.
     */
    public Integer role; // 0,
    /**
     * The Fw ver.
     */
    public String fwVer; // "1.3.5 Build 230426 Rel.65257n(4555)",
    /**
     * The App server url.
     */
    public String appServerUrl; // "https://eu-wap.tplinkcloud.com",
    /**
     * The Device region.
     */
    public String deviceRegion; // "eu-west-1",
    /**
     * The Device id.
     */
    public String deviceId; // "8021D929848CDC9FE26ACFDDCBA219D020A82738",
    /**
     * The Device name.
     */
    public String deviceName; // "C310",
    /**
     * The Device hw ver.
     */
    public String deviceHwVer; // "2.0",
    /**
     * The Alias.
     */
    public String alias; // "Tapo_Camera_2154",
    /**
     * The Device mac.
     */
    public String deviceMac; // "30DE4B502154",
    /**
     * The Oem id.
     */
    public String oemId; // "8254843D1E8318CB5C2BC8BF94D189FA",
    /**
     * The Device model.
     */
    public String deviceModel; // "C310",
    /**
     * The Hw id.
     */
    public String hwId; // "4F7FF895442ABA53E14CD582ED5BA10F",
    /**
     * The Fw id.
     */
    public String fwId; // "E4AA8AA9F0244830363D45108A493DEE",
    /**
     * The Is same region.
     */
    public Boolean isSameRegion; // true,
    /**
     * The Status.
     */
    public Integer status; // 0

    @Override
    public String toString() {
        return "ApiDeviceResponse{" + "deviceType='" + deviceType + '\'' + ", role=" + role + ", fwVer='" + fwVer + '\''
                + ", appServerUrl='" + appServerUrl + '\'' + ", deviceRegion='" + deviceRegion + '\'' + ", deviceId='"
                + deviceId + '\'' + ", deviceName='" + deviceName + '\'' + ", deviceHwVer='" + deviceHwVer + '\''
                + ", alias='" + alias + '\'' + ", deviceMac='" + deviceMac + '\'' + ", oemId='" + oemId + '\''
                + ", deviceModel='" + deviceModel + '\'' + ", hwId='" + hwId + '\'' + ", fwId='" + fwId + '\''
                + ", isSameRegion=" + isSameRegion + ", status=" + status + '}';
    }
}
