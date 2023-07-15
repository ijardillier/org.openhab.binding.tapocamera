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

package org.openhab.binding.tapocamera.internal.api.dto.system;

/**
 * The type Network info.
 */
public class NetworkInfo {
    /**
     * The Ifname.
     */
    public String ifname; // "br-wan",
    /**
     * The Type.
     */
    public String type; // "bridge",
    /**
     * The Wan type.
     */
    public String wan_type; // "dhcp",
    /**
     * The Speed duplex.
     */
    public String speed_duplex; // "auto",
    /**
     * The Proto.
     */
    public String proto; // "dhcp",
    /**
     * The Mtu.
     */
    public Integer mtu; // "1480",
    /**
     * The Auto.
     */
    public String auto; // "1",
    /**
     * The Netmask.
     */
    public String netmask; // "255.255.255.0",
    /**
     * The Macaddr.
     */
    public String macaddr; // "30:DE:4B:50:21:54",
    /**
     * The Fac macaddr.
     */
    public String fac_macaddr; // "30:DE:4B:50:21:54",
    /**
     * The Ipaddr.
     */
    public String ipaddr; // "192.168.2.104",
    /**
     * The Gateway.
     */
    public String gateway; // "192.168.2.1",
    /**
     * The Dns.
     */
    public String dns; // "192.168.2.1"

    /**
     * The Link type.
     */
    public String link_type;
}