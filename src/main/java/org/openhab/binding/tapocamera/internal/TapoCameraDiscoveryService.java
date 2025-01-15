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

import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.TAPO_DEVICE_TYPE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.tapocamera.internal.api.response.ApiDeviceResponse;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Tapo camera discovery service.
 *
 * @author "Ingrid JARDILLIER (ijardillier)"
 */
@Component(service = DiscoveryService.class, configurationPid = "discovery.tapocamera")
@NonNullByDefault
public class TapoCameraDiscoveryService extends AbstractDiscoveryService {

    private final Logger logger = LoggerFactory.getLogger(TapoCameraDiscoveryService.class);

    private @Nullable ScheduledFuture<?> backgroundFuture;

    /**
     * The Tapo camera bridge bus list.
     */
    public static List<TapoCameraBridge> tapoCameraBridgeBusList = new ArrayList<TapoCameraBridge>();

    /**
     * Initalizes a new Tapo camera discovery service.
     */
    public TapoCameraDiscoveryService() {
        super(Collections.singleton(TapoCameraBindingConstants.THING_TYPE_BRIDGE), 30, false);
    }

    @Override
    public synchronized void abortScan() {
        super.abortScan();
    }

    @Override
    protected synchronized void stopScan() {
        logger.debug("Stop scan");

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
                Map<String, String> ipMacs = getMacAddressFromArp();
                for (ApiDeviceResponse device : devices) {
                    if (device.deviceType.equals(TAPO_DEVICE_TYPE)) {
                        String ip = getIpByMac(ipMacs, device.deviceMac.toLowerCase());
                        logger.debug("found: {} with IP: {}", device, ip);
                        ThingUID thingUID = new ThingUID(TapoCameraBindingConstants.THING_TYPE_CAMERA,
                                bridge.getThing().getUID(), device.deviceMac.toLowerCase());
                        DiscoveryResult results = DiscoveryResultBuilder.create(thingUID)
                                .withProperty("device_id", device.deviceMac.toLowerCase()).withProperty("hostname", ip)
                                .withProperty("cloudPassword", bridge.config.cloudPassword)
                                .withRepresentationProperty("device_id").withRepresentationProperty("hostname")
                                .withLabel(device.alias + " (Model: " + device.deviceName + ")")
                                .withBridge(bridge.getThing().getUID()).build();
                        thingDiscovered(results);
                    }
                }
            }
        }
    }

    private String getIpByMac(Map<String, String> map, String mac) {

        if (map.isEmpty()) {
            return "";
        }

        return map.getOrDefault(mac, "");
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

    /**
     * Gets mac address from arp.
     *
     * @return The mac address from arp
     */
    public Map<String, String> getMacAddressFromArp() {
        Map<String, String> macs = new HashMap<>();

        File arp = new File("/proc/net/arp");
        if (!arp.exists()) {
            logger.warn("no arp available");
            return macs;
        }

        BufferedReader bufferedReader = null;

        try {
            FileReader fileReader = new FileReader(arp);
            bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        macs.put(mac.replace(":", ""), ip);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Tapo Camera Exception: {}", e.getMessage());
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                logger.error("Tapo Camera Exception: {}", e.getMessage());
            }
        }

        return macs;
    }
}
