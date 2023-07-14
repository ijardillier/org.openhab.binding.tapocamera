package org.openhab.binding.tapocamera.internal.api.v2.dto.system;

import com.google.gson.annotations.SerializedName;

public class DeviceInfoFull {
    @SerializedName("device_model")
    public String deviceModel; // "C310",
    @SerializedName("hw_version")
    public String hwVersion; // "2.0",
    @SerializedName("fw_description")
    public String fwDescription; // "C310 2.0",
    @SerializedName("product_id")
    public String productId; // "00c31020",
    @SerializedName("device_name")
    public String deviceName; // "C310 2.0",
    @SerializedName("device_info")
    public String deviceInfo; // "C310 2.0 IPC",
    @SerializedName("dev_id")
    public String devId; // "8021D929848CDC9FE26ACFDDCBA219D020A82738",
    @SerializedName("hw_id")
    public String hwId; // "4F7FF895442ABA53E14CD582ED5BA10F",
    public String mac; // "30-DE-4B-50-21-54",
    @SerializedName("cur_isp_version")
    public String curIspVersion; // "2",
    public String sensor; // "SC3338",
    @SerializedName("fw_cur_id")
    public String fwCurId; // "E4AA8AA9F0244830363D45108A493DEE",
    @SerializedName("sw_version")
    public String swVersion; // "1.3.5 Build 230426 Rel.65257n(4555)",
    @SerializedName("sys_software_revision")
    public String sysSoftwareRevision; // "0x500a0103",
    @SerializedName("sys_software_revision_minor")
    public String sysSoftwareRevisionMinor; // "0x0005",
    @SerializedName("device_base")
    public String deviceBase; // "C310",
    @SerializedName("lens_name")
    public String lensName; // "C7327Z",
    @SerializedName("device_type")
    public String deviceType; // "SMART.IPCAMERA",
    public Integer features; // "3",
    @SerializedName("domain_name")
    public String domainName; // "tplogin.cn",
    public String language; // "EN",
    @SerializedName("enable_dns")
    public Integer enableDns; // "1",
    @SerializedName("manufacturer_name")
    public String manufacturerName; // "TP-LINK",
    @SerializedName("friendly_name")
    public String friendlyName; // "IPC",
    @SerializedName("model_description")
    public String modelDescription; // "IPC",
    @SerializedName("manufacturer_url")
    public String manufacturerUrl; // "http://www.tp-link.com",
    @SerializedName("vendor_id")
    public String vendorId; // "0x00000001",
    @SerializedName("zone_code")
    public String zoneCode; // "0x0",
    @SerializedName("roi_reg_num")
    public Integer roiRegNum; // "1",
    @SerializedName("cover_reg_num")
    public Integer coverRegNum; // "4",
    @SerializedName("md_reg_num")
    public Integer mdRegNum; // "32",
    @SerializedName("td_reg_num")
    public Integer tdRegNum; // "1",
    @SerializedName("id_reg_num")
    public Integer idRegNum; // "4",
    @SerializedName("cd_reg_num")
    public Integer cdRegNum; // "4",
    @SerializedName("ac_reg_num")
    public Integer acRegNum; // "1",
    @SerializedName("plugin_obtain_way")
    public String pluginObtainWay; // "web",
    @SerializedName("product_type")
    public String productType; // "ipc",
    @SerializedName("fw_shared_prefix")
    public String fwSharedPrefix; // "Tapo_C310v2",
    @SerializedName("ext_fw_upgrade")
    public Integer extFwUpgrade; // "1"

    @Override
    public String toString() {
        return "DeviceInfoFull{" +
                "deviceModel='" + deviceModel + '\'' +
                ", hwVersion='" + hwVersion + '\'' +
                ", fwDescription='" + fwDescription + '\'' +
                ", productId='" + productId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", devId='" + devId + '\'' +
                ", hwId='" + hwId + '\'' +
                ", mac='" + mac + '\'' +
                ", curIspVersion='" + curIspVersion + '\'' +
                ", sensor='" + sensor + '\'' +
                ", fwCurId='" + fwCurId + '\'' +
                ", swVersion='" + swVersion + '\'' +
                ", sysSoftwareRevision='" + sysSoftwareRevision + '\'' +
                ", sysSoftwareRevisionMinor='" + sysSoftwareRevisionMinor + '\'' +
                ", deviceBase='" + deviceBase + '\'' +
                ", lensName='" + lensName + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", features=" + features +
                ", domainName='" + domainName + '\'' +
                ", language='" + language + '\'' +
                ", enableDns=" + enableDns +
                ", manufacturerName='" + manufacturerName + '\'' +
                ", friendlyName='" + friendlyName + '\'' +
                ", modelDescription='" + modelDescription + '\'' +
                ", manufacturerUrl='" + manufacturerUrl + '\'' +
                ", vendorId='" + vendorId + '\'' +
                ", zoneCode='" + zoneCode + '\'' +
                ", roiRegNum=" + roiRegNum +
                ", coverRegNum=" + coverRegNum +
                ", mdRegNum=" + mdRegNum +
                ", tdRegNum=" + tdRegNum +
                ", idRegNum=" + idRegNum +
                ", cdRegNum=" + cdRegNum +
                ", acRegNum=" + acRegNum +
                ", pluginObtainWay='" + pluginObtainWay + '\'' +
                ", productType='" + productType + '\'' +
                ", fwSharedPrefix='" + fwSharedPrefix + '\'' +
                ", extFwUpgrade=" + extFwUpgrade +
                '}';
    }
}
