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
package org.openhab.binding.tapocamera.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link TapoCameraConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author "Ingrid JARDILLIER (ijardillier"
 */
@NonNullByDefault
public class TapoCameraConfiguration {

    /**
     * The hostname.
     */
    public String hostname = "";

    /**
     * The username.
     */
    public String username = "";

    /**
     * The password.
     */
    public String password = "";

    /**
     * The cloud password.
     */
    public String cloudPassword = "";

    /**
     * The reconnect interval.
     */
    public int reconnectInterval = 60;

    /**
     * The polling interval.
     */
    public int pollingInterval = 5;

    public String cloudUsername = "";
}
