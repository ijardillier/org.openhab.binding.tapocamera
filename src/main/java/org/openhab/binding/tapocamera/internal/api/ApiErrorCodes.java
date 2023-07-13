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

package org.openhab.binding.tapocamera.internal.api;

import java.util.Arrays;

public enum ApiErrorCodes {
    ERROR_40401(-40401, "Invalid stok value"),
    ERROR_40101(-40101, "Section in module no supported"), // Parameter to set does not exist
    ERROR_40105(-40105, "Method does not exist"),
    ERROR_40106(-40106, "Module does not exist"), // Parameter to get/do does not exist
    ERROR_40209(-40209, "Invalid login credentials"),
    ERROR_40210(-40210, "Function not supported"),
    ERROR_64303(-64303, "Action cannot be done while camera is in patrol mode."),
    ERROR_64324(-64324, "Privacy mode is ON, not able to execute"),
    ERROR_64302(-64302, "Preset ID not found"),
    ERROR_64321(-64321, "Preset ID was deleted so no longer exists"),
    ERROR_64304(-64304, "Maximum Pan/Tilt range reached"),
    ERROR_71112(-71112, "error 71112"),
    ERROR_UNKNOWN(-99999, "Unknown error"),;

    private Integer code;
    private String messgae;

    ApiErrorCodes(Integer code, String messgae) {
        this.code = code;
        this.messgae = messgae;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessgae() {
        return messgae;
    }

    public void setMessgae(String messgae) {
        this.messgae = messgae;
    }

    public static ApiErrorCodes getErrorByCode(Integer code) {
        return Arrays.stream(ApiErrorCodes.values()).filter(v -> v.code.equals(code)).findFirst().orElse(ERROR_UNKNOWN);
    }
}
