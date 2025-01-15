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
package org.openhab.binding.tapocamera.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.tapocamera.internal.api.ApiException;
import org.openhab.binding.tapocamera.internal.api.TapoCameraApiFactory;
import org.openhab.binding.tapocamera.internal.api.TapoCameraCloudApiImpl;
import org.openhab.binding.tapocamera.internal.api.response.ApiDeviceResponse;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Tapo camera bridge.
 *
 * @author "Ingrid JARDILLIER"
 */
public class TapoCameraBridge extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(TapoCameraBridge.class);

    /**
     * The cloud api.
     */
    public TapoCameraCloudApiImpl cloudApi;

    /**
     * The config.
     */
    public @Nullable TapoCameraConfiguration config;

    /**
     * Initializes a new Tapo camera bridge.
     *
     * @param bridge The bridge
     * @param apiFactory The api factory
     */
    public TapoCameraBridge(Bridge bridge, TapoCameraApiFactory apiFactory) {
        super(bridge);
        cloudApi = (TapoCameraCloudApiImpl) apiFactory.getCloudApi();
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);

        TapoCameraDiscoveryService.tapoCameraBridgeBusList.add(this);
        config = getConfigAs(TapoCameraConfiguration.class);
        if (config != null) {

            String token = cloudApi.getCloudToken(config.cloudUsername, config.cloudPassword);
            if (token == null || token.isEmpty()) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Can not connect to Tapo Cloud");
            } else {
                updateStatus(ThingStatus.ONLINE);
            }

        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Check bridge configuration");
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    /**
     * Gets devices.
     *
     * @return The devices
     */
    public List<ApiDeviceResponse> getDevices() {
        try {
            String token = cloudApi.getCloudToken(config.cloudUsername, config.cloudPassword);
            if (token != null && !token.isEmpty()) {
                return cloudApi.getCloudDevices(token);
            }
        } catch (ApiException e) {
            logger.error("Can not get devices from Tapo Cloud");
        }

        return new ArrayList<>();
    }
}
