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

import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_GROUP_ALARM_CONTROL;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_GROUP_DETECTIONS;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_GROUP_IMAGE;
import static org.openhab.binding.tapocamera.internal.TapoCameraBindingConstants.CHANNEL_GROUP_SYSTEM;

/**
 * The enum Tapo camera channels.
 */
public enum TapoCameraChannels {
    /**
     * The Channel led status.
     */
// system group
    CHANNEL_LED_STATUS("led-status", CHANNEL_GROUP_SYSTEM),
    CHANNEL_PRIVACY_MODE("privacy-mode", CHANNEL_GROUP_SYSTEM),
    /**
     * Channel manual alarm tapo camera channels.
     */
    CHANNEL_MANUAL_ALARM("manual-alarm", CHANNEL_GROUP_SYSTEM),
    /**
     * Channel speaker volume tapo camera channels.
     */
    CHANNEL_SPEAKER_VOLUME("speaker-volume", CHANNEL_GROUP_SYSTEM),
    /**
     * Channel microphone volume tapo camera channels.
     */
    CHANNEL_MICROPHONE_VOLUME("microphone-volume", CHANNEL_GROUP_SYSTEM),

    /**
     * The Channel alarm enabled.
     */
// alarm control group
    CHANNEL_ALARM_ENABLED("alarm-enabled", CHANNEL_GROUP_ALARM_CONTROL),
    /**
     * Channel alarm type tapo camera channels.
     */
    CHANNEL_ALARM_TYPE("alarm-type", CHANNEL_GROUP_ALARM_CONTROL),
    /**
     * Channel alarm mode tapo camera channels.
     */
    CHANNEL_ALARM_MODE("alarm-mode", CHANNEL_GROUP_ALARM_CONTROL),
    /**
     * Channel alarm light type tapo camera channels.
     */
    CHANNEL_ALARM_LIGHT_TYPE("alarm-light-type", CHANNEL_GROUP_ALARM_CONTROL),
    CHANNEL_LAST_ALARM_TYPE("last-alarm-type", CHANNEL_GROUP_ALARM_CONTROL),
    CHANNEL_LAST_ALARM_TIME("last-alarm-time", CHANNEL_GROUP_ALARM_CONTROL),

    /**
     * The Channel motion detection enabled.
     */
// detections group
    CHANNEL_MOTION_DETECTION_ENABLED("motion-detection-enabled", CHANNEL_GROUP_DETECTIONS),
    /**
     * Channel motion detection sensitivity tapo camera channels.
     */
    CHANNEL_MOTION_DETECTION_SENSITIVITY("motion-detection-sensitivity", CHANNEL_GROUP_DETECTIONS),
    /**
     * Channel motion detection digital sensitivity tapo camera channels.
     */
    CHANNEL_MOTION_DETECTION_DIGITAL_SENSITIVITY("motion-detection-digital-sensitivity", CHANNEL_GROUP_DETECTIONS),
    /**
     * Channel person detection enabled tapo camera channels.
     */
    CHANNEL_PERSON_DETECTION_ENABLED("person-detection-enabled", CHANNEL_GROUP_DETECTIONS),
    /**
     * Channel person detection sensitivity tapo camera channels.
     */
    CHANNEL_PERSON_DETECTION_SENSITIVITY("person-detection-sensitivity", CHANNEL_GROUP_DETECTIONS),
    /**
     * Channel line crossing detection enabled tapo camera channels.
     */
    CHANNEL_LINE_CROSSING_DETECTION_ENABLED("line-crossing-detection-enabled", CHANNEL_GROUP_DETECTIONS),
    /**
     * Channel intrusion detection enabled tapo camera channels.
     */
    CHANNEL_INTRUSION_DETECTION_ENABLED("intrusion-detection-enabled", CHANNEL_GROUP_DETECTIONS),

    CHANNEL_IMAGE_FLIP("flip", CHANNEL_GROUP_IMAGE),
    CHANNEL_IMAGE_LENS_CORRECTION("ldc", CHANNEL_GROUP_IMAGE),
    CHANNEL_IMAGE_NIGHT_VISION("night-vision", CHANNEL_GROUP_IMAGE),
    CHANNEL_IMAGE_CONTRAST("contrast", CHANNEL_GROUP_IMAGE),
    CHANNEL_IMAGE_SATURATION("saturation", CHANNEL_GROUP_IMAGE),
    CHANNEL_IMAGE_SHARPNESS("sharpness", CHANNEL_GROUP_IMAGE),
    CHANNEL_IMAGE_LUMA("luma", CHANNEL_GROUP_IMAGE),
    ;

    private String channelId;
    private String groupId;

    TapoCameraChannels(String channelId, String groupId) {
        this.channelId = channelId;
        this.groupId = groupId;
    }

    /**
     * Gets channel id.
     *
     * @return the channel id
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Sets channel id.
     *
     * @param channelId the channel id
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    /**
     * Gets group id.
     *
     * @return the group id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets group id.
     *
     * @param groupId the group id
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return groupId + "#" + channelId;
    }
}
