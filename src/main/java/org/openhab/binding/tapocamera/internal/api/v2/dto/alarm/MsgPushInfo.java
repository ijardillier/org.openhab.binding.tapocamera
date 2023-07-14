package org.openhab.binding.tapocamera.internal.api.v2.dto.alarm;

import com.google.gson.annotations.SerializedName;

public class MsgPushInfo {
    @SerializedName("notification_enabled")
    public String notificationEnabled;
    @SerializedName("rich_notification_enabled")
    public String richNotificationEnabled;
}
