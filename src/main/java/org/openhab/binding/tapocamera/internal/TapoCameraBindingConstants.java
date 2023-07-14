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

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_CAMERA = new ThingTypeUID(BINDING_ID, "camera");

    public static final String CHANNEL_GROUP_DEVICE = "device";
    public static final String CHANNEL_GROUP_SYSTEM = "system";
    public static final String CHANNEL_GROUP_IMAGE = "image";
    public static final String CHANNEL_GROUP_ALARM_CONTROL = "alarm-control";
    public static final String CHANNEL_GROUP_DETECTIONS = "detections";
    public static final String CHANNEL_GROUP_MOTOR = "motor";
    public static final String CHANNEL_GROUP_PRESET = "preset";
}
