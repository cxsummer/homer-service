package com.microcosm.homer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author caojiancheng
 * @date 2022-04-21 10:55
 */
@Getter
@AllArgsConstructor
public enum SSDPStEnum {
    ALL("ssdp:all", "所有设备和服务"),
    ROOT_DEVICE("upnp:rootdevice", "网络中的根设备"),
    DEVICE_UUID("uuid:device-UUID", "查询UUID标识的设备"),
    AV_TRANSPORT_V1("urn:schemas-upnp-org:service:AVTransport:1", "查询投屏服务");

    private String type;
    private String desc;

    public static SSDPStEnum getEnumByType(String type) {
        return Arrays.stream(SSDPStEnum.values())
                .filter(e -> e.getType().equals(type)).findFirst().orElse(null);
    }
}
