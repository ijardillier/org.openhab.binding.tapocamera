/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * The type Api response.
 *
 * @author "Ingrid JARDILLIER (ijardillier)"
 */
public class ApiResponse {

    /**
     * The Error code.
     */
    @SerializedName("error_code")
    public int errorCode;

    /**
     * The Result.
     */
    @SerializedName("result")
    public JsonObject result;

    @Override
    public String toString() {
        return "ApiResponse{" + "errorCode=" + errorCode + ", result=" + result + '}';
    }
}
