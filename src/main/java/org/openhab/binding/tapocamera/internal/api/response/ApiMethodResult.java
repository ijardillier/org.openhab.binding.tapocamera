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

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * The type Api method result.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public class ApiMethodResult {
    /**
     * The Method.
     */
    @SerializedName("method")
    public String method;
    /**
     * The Error code.
     */
    @SerializedName("error_code")
    public Integer errorCode;
    /**
     * The Result.
     */
    @SerializedName("result")
    public JsonObject result;

    @Override
    public String toString() {
        return "ApiMethodResult{" + "method='" + method + '\'' + ", errorCode=" + errorCode + ", result=" + result
                + '}';
    }
}
