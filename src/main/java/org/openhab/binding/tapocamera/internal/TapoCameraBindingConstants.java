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
package org.openhab.binding.tapocamera.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link TapoCameraBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
@NonNullByDefault
public class TapoCameraBindingConstants {

    private static final String BINDING_ID = "tapocamera";
    public static final String THING_TYPE_ID = "camera";
    private static final String BRIDGE_TYPE_ID = "bridge";
    public static final String TAPO_CLOUD_URL = "https://eu-wap.tplinkcloud.com";

    public static final String TAPO_DEVICE_TYPE = "SMART.IPCAMERA";

    /**
     * The constant THING_TYPE_CAMERA.
     */
    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_CAMERA = new ThingTypeUID(BINDING_ID, THING_TYPE_ID);
    public static final ThingTypeUID THING_TYPE_BRIDGE = new ThingTypeUID(BINDING_ID, BRIDGE_TYPE_ID);

    /**
     * The constant CHANNEL_GROUP_DEVICE.
     */
    public static final String CHANNEL_GROUP_DEVICE = "device";
    /**
     * The constant CHANNEL_GROUP_SYSTEM.
     */
    public static final String CHANNEL_GROUP_SYSTEM = "system";
    /**
     * The constant CHANNEL_GROUP_IMAGE.
     */
    public static final String CHANNEL_GROUP_IMAGE = "image";
    /**
     * The constant CHANNEL_GROUP_ALARM_CONTROL.
     */
    public static final String CHANNEL_GROUP_ALARM_CONTROL = "alarm-control";
    /**
     * The constant CHANNEL_GROUP_DETECTIONS.
     */
    public static final String CHANNEL_GROUP_DETECTIONS = "detections";
    /**
     * The constant CHANNEL_GROUP_MOTOR.
     */
    public static final String CHANNEL_GROUP_MOTOR = "motor";
    /**
     * The constant CHANNEL_GROUP_PRESET.
     */
    public static final String CHANNEL_GROUP_PRESET = "presets";
}
