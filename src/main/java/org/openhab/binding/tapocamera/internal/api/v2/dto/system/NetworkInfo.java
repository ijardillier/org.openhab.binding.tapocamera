package org.openhab.binding.tapocamera.internal.api.v2.dto.system;

public class NetworkInfo {
    public String ifname; // "br-wan",
    public String type; // "bridge",
    public String wan_type; // "dhcp",
    public String speed_duplex; // "auto",
    public String proto; // "dhcp",
    public Integer mtu; // "1480",
    public String auto; // "1",
    public String netmask; // "255.255.255.0",
    public String macaddr; // "30:DE:4B:50:21:54",
    public String fac_macaddr; // "30:DE:4B:50:21:54",
    public String ipaddr; // "192.168.2.104",
    public String gateway; // "192.168.2.1",
    public String dns; // "192.168.2.1"

    public String link_type;
}