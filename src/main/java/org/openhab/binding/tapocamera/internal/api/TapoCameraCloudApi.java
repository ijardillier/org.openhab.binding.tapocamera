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
package org.openhab.binding.tapocamera.internal.api;

import java.util.List;

import org.openhab.binding.tapocamera.internal.api.response.ApiDeviceResponse;

/**
 * The Tapo camera cloud api interface.
 *
 * @author "Ingrid JARDILLIER (ijardillier)"
 */
public interface TapoCameraCloudApi {

    /**
     * Gets cloud token.
     *
     * @param username The username
     * @param password the password
     * @return The cloud token
     */
    String getCloudToken(String username, String password);

    /**
     * Gets cloud devices.
     *
     * @param token The token
     * @return The cloud devices
     * @throws ApiException The api exception
     */
    List<ApiDeviceResponse> getCloudDevices(String token) throws ApiException;
}
