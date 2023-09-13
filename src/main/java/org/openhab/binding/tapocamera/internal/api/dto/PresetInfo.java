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
package org.openhab.binding.tapocamera.internal.api.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * The type Preset info.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public class PresetInfo {
    /**
     * The Id.
     */
    @SerializedName("id")
    public List<Integer> ids;
    /**
     * The Name.
     */
    @SerializedName("name")
    public List<String> names;
    /**
     * The Position pan.
     */
    @SerializedName("position_pan")
    public List<Double> pansPosition;
    /**
     * The Position tilt.
     */
    @SerializedName("position_tilt")
    public List<Double> tiltsPosition;
    /**
     * The Read only.
     */
    @SerializedName("read_only")
    public List<Integer> readOnly;

    @Override
    public String toString() {
        return "PresetInfo{" + "ids=" + ids + ", names=" + names + ", pansPosition=" + pansPosition + ", tiltsPosition="
                + tiltsPosition + ", readOnly=" + readOnly + '}';
    }
}
/*
 * {method='getPresetConfig', errorCode=0,
 * result={"preset":{"preset":{"id":["1","2"],"name":["Отмеченная зона 1","Отмеченная зона 2"],"read_only":["0","0"],
 * "position_pan":["0.940538","0.355739"],"position_tilt":["1.000000","-0.391960"]}}}}
 */
