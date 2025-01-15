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
package org.openhab.binding.tapocamera.internal.api.utils;

import java.util.Random;

/**
 * The Hex utils.
 *
 * @author "Ingrid JARDILLIER (ijardillier)"
 */
public class HexUtils {

    private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
            'F' };

    private static Random random = new Random();

    /**
     * Generate random hex string of wanted size.
     *
     * @param size the size
     * @return hex string
     */
    public static String randomHexString(int size) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append(hexDigits[random(hexDigits.length)]);
        }
        return sb.toString();
    }

    /**
     * Gets a random number from 0 (including) to "max" (excluding) argument, i.e.
     * returns a value less than "max".
     *
     * @param maxValue maxValue
     * @return result
     */
    public static int random(int maxValue) {
        return random.nextInt(maxValue);
    }
}
