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

package org.openhab.binding.tapocamera.internal.api.v1.response;

import com.google.gson.annotations.SerializedName;

public class Old_ApiDeviceInfo extends ApiResponse {

    @SerializedName("basic_info")
    public BasicInfo basicInfo = new BasicInfo();

    public class BasicInfo {
        @SerializedName("device_type")
        public String deviceType = "";
        @SerializedName("device_model")
        public String deviceModel = "";
        @SerializedName("device_name")
        public String deviceName = "";
        @SerializedName("device_info")
        public String deviceInfo = "";
        @SerializedName("device_alias")
        public String friendlyName = "";
        @SerializedName("hw_version")
        public String hwVersion = "";
        @SerializedName("sw_version")
        public String swVersion = "";
        @SerializedName("dev_id")
        public String devId = "";
    }
}
