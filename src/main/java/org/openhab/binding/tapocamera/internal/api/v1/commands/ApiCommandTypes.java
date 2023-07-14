/*
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 *  See the NOTICE file(s) distributed with this work for additional
 *  information.
 *
 * This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.tapocamera.internal.api.v1.commands;

public enum ApiCommandTypes {
    CMD_LOGIN("login"),
    CMD_GET("get"),
    CMD_SET("set"),
    CMD_DO("do"),
    CMD_UNKNOWN("unknown");

    private String name;

    ApiCommandTypes(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public static ApiCommandTypes getCommandByName(String name) {
        for (ApiCommandTypes c : ApiCommandTypes.values()) {
            if (c.name.equals(name)) {
                return c;
            }
        }
        return CMD_UNKNOWN;
    }
}
