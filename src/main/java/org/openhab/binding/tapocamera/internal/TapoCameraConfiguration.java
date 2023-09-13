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

/**
 * The {@link TapoCameraConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
@NonNullByDefault
public class TapoCameraConfiguration {

    /**
     * The Hostname.
     */
    public String hostname = "";
    /**
     * The Username.
     */
    public String username = "";
    /**
     * The Password.
     */
    public String password = "";
    /**
     * The Cloud password.
     */
    public String cloudPassword = "";
    /**
     * The Reconnect interval.
     */
    public int reconnectInterval = 60;
    /**
     * The Polling interval.
     */
    public int pollingInterval = 5;

    public String cloudToken = "";
    public String cloudUsername = "";
}
