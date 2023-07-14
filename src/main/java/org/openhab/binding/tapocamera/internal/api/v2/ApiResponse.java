package org.openhab.binding.tapocamera.internal.api.v2;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("error_code")
    public int errorCode;

    @SerializedName("result")
    public JsonObject result;


    @Override
    public String toString() {
        return "ApiResponse{" +
                "errorCode=" + errorCode +
                ", result=" + result +
                '}';
    }


}
