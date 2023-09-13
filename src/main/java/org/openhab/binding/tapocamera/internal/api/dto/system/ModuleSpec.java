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
package org.openhab.binding.tapocamera.internal.api.dto.system;

import java.util.List;

/**
 * The type Module spec.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public class ModuleSpec {
    /**
     * The Led.
     */
    public Integer led; // led support = 1, no led - no parameter, C200 - support, C310 - support
    /**
     * The Motor.
     */
    public Integer motor; // motor support = 1, no motor - no parameter, C200 - support, C310 - not support
    /**
     * The Smart detection.
     */
    public Integer smart_detection; // Person detection??? C200 - not support, C310 - support
    /**
     * The Smart msg push capability.
     */
    public Integer smart_msg_push_capability; // ??? C200 - not support, C310 - support
    /**
     * The Custom area compensation.
     */
    public Integer custom_area_compensation; // ??? C200 - not support, C310 - support
    /**
     * The Ae weighting table resolution.
     */
    public String ae_weighting_table_resolution; // "5*5" ??? C200 - not support, C310 - support

    /**
     * The Audio.
     */
    public List<String> audio; // ["speaker", "microphone"]
    /**
     * The Lens mask.
     */
    public Integer lens_mask; // privacy mode
    /**
     * The Target track.
     */
    public Integer target_track; // 1 or 0, C200 - support, C310 - support

    /**
     * The Linecrossing detection.
     */
    public Integer linecrossing_detection; // C200 - not support, C310 - support
    /**
     * The Intrusion detection.
     */
    public Integer intrusion_detection; // C200 - not support, C310 - support
    /**
     * The Audioexception detection.
     */
    public Integer audioexception_detection; // C200 - not support, C310 - not support

    @Override
    public String toString() {
        return "ModuleSpec{" + "led=" + led + ", motor=" + motor + ", smart_detection=" + smart_detection
                + ", smart_msg_push_capability=" + smart_msg_push_capability + ", custom_area_compensation="
                + custom_area_compensation + ", ae_weighting_table_resolution='" + ae_weighting_table_resolution + '\''
                + ", audio=" + audio + ", lens_mask=" + lens_mask + ", target_track=" + target_track
                + ", linecrossing_detection=" + linecrossing_detection + ", intrusion_detection=" + intrusion_detection
                + ", audioexception_detection=" + audioexception_detection + '}';
    }
}
