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
package org.openhab.binding.tapocamera.internal.api;

import java.util.List;

import org.openhab.binding.tapocamera.internal.api.response.ApiDeviceResponse;

/**
 * The interface Tapo camera cloud api.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public interface TapoCameraCloudApi {
    /**
     * Gets cloud token.
     *
     * @param username the username
     * @param password the password
     * @return the cloud token
     */
    String getCloudToken(String username, String password);

    /**
     * Gets cloud devices.
     *
     * @param token the token
     * @return the cloud devices
     * @throws ApiException the api exception
     */
    List<ApiDeviceResponse> getCloudDevices(String token) throws ApiException;
}
