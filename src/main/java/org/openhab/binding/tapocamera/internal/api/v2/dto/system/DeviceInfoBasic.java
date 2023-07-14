package org.openhab.binding.tapocamera.internal.api.v2.dto.system;

import com.google.gson.annotations.SerializedName;

public class DeviceInfoBasic {
    public Boolean ffs;
    @SerializedName("device_type")
    public String deviceType; //  SMART.IPCAMERA
    @SerializedName("device_model")
    public String deviceModel; // C310
    @SerializedName("device_name")
    public String deviceName; // "C310 2.0"
    @SerializedName("device_info")
    public String deviceInfo; // "C310 2.0 IPC"
    @SerializedName("hw_version")
    public String hwVersion; // "2.0"
    @SerializedName("sw_version")
    public String swVersion; // "1.3.5 Build 230426 Rel.65257n(4555)"
    @SerializedName("device_alias")
    public String deviceAlias; // "Tapo_Camera_2154"
    public String avatar; // "Home"
    public Long longitude;
    public Long latitude;
    @SerializedName("has_set_location_info")
    public Integer hasSetLocationInfo; // 0
    public Integer features; // "3"
    public String barcode; // ""
    public String mac; // "30-DE-4B-50-21-54"
    @SerializedName("dev_id")
    public String devId; // "8021D929848CDC9FE26ACFDDCBA219D020A82738"
    @SerializedName("oem_id")
    public String oemId; // "8254843D1E8318CB5C2BC8BF94D189FA"
    @SerializedName("hw_desc")
    public String hwDesc; // "00000000000000000000000000000000"
    @SerializedName("is_cal")
    public Boolean isCal; // true

    @Override
    public String toString() {
        return "DeviceInfoBasic{" +
                "ffs=" + ffs +
                ", deviceType='" + deviceType + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", hwVersion='" + hwVersion + '\'' +
                ", swVersion='" + swVersion + '\'' +
                ", deviceAlias='" + deviceAlias + '\'' +
                ", avatar='" + avatar + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", hasSetLocationInfo=" + hasSetLocationInfo +
                ", features=" + features +
                ", barcode='" + barcode + '\'' +
                ", mac='" + mac + '\'' +
                ", devId='" + devId + '\'' +
                ", oemId='" + oemId + '\'' +
                ", hwDesc='" + hwDesc + '\'' +
                ", isCal=" + isCal +
                '}';
    }
}
