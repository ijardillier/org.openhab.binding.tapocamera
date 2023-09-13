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
package org.openhab.binding.tapocamera.internal.api.dto.audio;

/**
 * The type Audio microphone info.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public class AudioMicrophoneInfo {
    @Override
    public String toString() {
        return "AudioMicrophoneInfo{" + "sampling_rate=" + sampling_rate + ", channels=" + channels + ", encode_type='"
                + encode_type + '\'' + ", mute='" + mute + '\'' + ", noise_cancelling='" + noise_cancelling + '\''
                + ", volume=" + volume + '}';
    }

    /**
     * The Sampling rate.
     */
    public Integer sampling_rate; // "8",
    /**
     * The Channels.
     */
    public Integer channels; // "1",
    /**
     * The Encode type.
     */
    public String encode_type; // "G711alaw",
    /**
     * The Mute.
     */
    public String mute; // "off",
    /**
     * The Noise cancelling.
     */
    public String noise_cancelling; // "on",
    /**
     * The Volume.
     */
    public Integer volume; // "100"
}
