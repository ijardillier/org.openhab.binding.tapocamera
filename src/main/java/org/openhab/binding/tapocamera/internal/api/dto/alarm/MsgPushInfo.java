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
package org.openhab.binding.tapocamera.internal.api.dto.alarm;

import com.google.gson.annotations.SerializedName;

/**
 * The type Msg push info.
 *
 * @author "Dmintry P (d51x)" - Initial contribution
 */
public class MsgPushInfo {
    @Override
    public String toString() {
        return "MsgPushInfo{" + "notificationEnabled='" + notificationEnabled + '\'' + ", richNotificationEnabled='"
                + richNotificationEnabled + '\'' + '}';
    }

    /**
     * The Notification enabled.
     */
    @SerializedName("notification_enabled")
    public String notificationEnabled;
    /**
     * The Rich notification enabled.
     */
    @SerializedName("rich_notification_enabled")
    public String richNotificationEnabled;
}
