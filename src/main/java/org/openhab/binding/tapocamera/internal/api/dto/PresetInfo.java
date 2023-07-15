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

package org.openhab.binding.tapocamera.internal.api.dto;

/**
 * The type Preset info.
 */
public class PresetInfo {
    @Override
    public String toString() {
        return "PresetInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position_pan=" + position_pan +
                ", position_tilt=" + position_tilt +
                ", read_only=" + read_only +
                '}';
    }

    /**
     * The Id.
     */
    public Integer id;
    /**
     * The Name.
     */
    public String name;
    /**
     * The Position pan.
     */
    public Double position_pan;
    /**
     * The Position tilt.
     */
    public Double position_tilt;
    /**
     * The Read only.
     */
    public Integer read_only;
}
