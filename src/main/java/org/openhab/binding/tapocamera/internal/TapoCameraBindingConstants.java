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

    // List of all Channel ids
    public static final String CHANNEL_LED_STATUS = "led-status";

    public static final String CHANNEL_ALARM_ENABLED = "alarm-enabled";
    public static final String CHANNEL_ALARM_TYPE = "alarm-type";
    public static final String CHANNEL_ALARM_MODE = "alarm-mode";
    public static final String CHANNEL_ALARM_LIGHT_TYPE = "alarm-light-type";

    public static final String CHANNEL_MOTION_DETECTION_ENABLED = "motion-detection-enabled";
    public static final String CHANNEL_MOTION_DETECTION_SENSITIVITY = "motion-detection-sensitivity";
    public static final String CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY = "motion-detection-digital-sensitivity";

    public static final String CHANNEL_PEOPLE_DETECTION_ENABLED = "people-detection-enabled";
    public static final String CHANNEL_PEOPLE_DETECTION_SENSITIVITY = "people-detection-sensitivity";
    public static final String CHANNEL_MANUAL_ALARM = "manual-alarm";
}
