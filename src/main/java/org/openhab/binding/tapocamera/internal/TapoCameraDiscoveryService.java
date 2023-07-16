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

package org.openhab.binding.tapocamera.internal;

import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.TAPO_DEVICE_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Component;

import org.openhab.binding.tapocamera.internal.api.response.ApiDeviceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = DiscoveryService.class, configurationPid = "discovery.tapocamera")
@NonNullByDefault
public class TapoCameraDiscoveryService extends AbstractDiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(TapoCameraDiscoveryService.class);

    private @Nullable ScheduledFuture<?> backgroundFuture;
    public static List<TapoCameraBridge> tapoCameraBridgeBusList = new ArrayList<TapoCameraBridge>();

    public TapoCameraDiscoveryService() {
        super(Collections.singleton(TapoCameraBindingConstants.THING_TYPE_BRIDGE), 30, false);
    }

    @Override
    public synchronized void abortScan() {
        super.abortScan();
    }

    @Override
    protected synchronized void stopScan() {

        ScheduledFuture<?> scan = backgroundFuture;
        if (scan != null) {
            scan.cancel(true);
            backgroundFuture = null;
        }
        super.stopScan();
    }

    @Override
    protected void startScan() {
        logger.debug("Start scan");

        for (TapoCameraBridge bridge : tapoCameraBridgeBusList) {
            List<ApiDeviceResponse> devices = bridge.getDevices();
            if (devices != null && !devices.isEmpty()) {
                for (ApiDeviceResponse device : devices) {
                    if (device.deviceType.equals(TAPO_DEVICE_TYPE)) {
                        logger.debug("found: {}", device.toString());
                        ThingUID thingUID = new ThingUID(TapoCameraBindingConstants.THING_TYPE_CAMERA,
                                bridge.getThing().getUID(), device.deviceMac.toLowerCase());
                        DiscoveryResult results = DiscoveryResultBuilder.create(thingUID)
                                .withProperty("device_id", device.deviceMac.toLowerCase())
                                .withRepresentationProperty("device_id")
                                .withLabel(device.alias + " (Model: " + device.deviceName + ")")
                                .withBridge(bridge.getThing().getUID())
                                .build();
                        thingDiscovered(results);
                    }
                }
            }
        }
    }

    @Override
    protected void startBackgroundDiscovery() {
        logger.debug("startBackgroundDiscovery");
    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.debug("stopBackgroundDiscovery");
        super.stopBackgroundDiscovery();
    }

}

