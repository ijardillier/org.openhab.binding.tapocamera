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

package org.openhab.binding.tapocamera.internal.api.dto.detection;

/**
 * The type Target auto track info.
 */
public class TargetAutoTrackInfo {
    @Override
    public String toString() {
        return "TargetAutoTrackInfo{" +
                "x_coord='" + x_coord + '\'' +
                ", y_coord='" + y_coord + '\'' +
                ", enabled='" + enabled + '\'' +
                '}';
    }

    /**
     * The X coord.
     */
    public String x_coord;
    /**
     * The Y coord.
     */
    public String y_coord;
    /**
     * The Enabled.
     */
    public String enabled;
}