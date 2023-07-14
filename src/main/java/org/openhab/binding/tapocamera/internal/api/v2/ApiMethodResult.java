package org.openhab.binding.tapocamera.internal.api.v2;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ApiMethodResult {
    @SerializedName("method")
    public String method;
    @SerializedName("error_code")
    public Integer errorCode;
    @SerializedName("result")
    public JsonObject result;

}
