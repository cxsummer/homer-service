package com.microcosm.homer.model;

import com.microcosm.homer.enums.SSDPStEnum;
import lombok.Data;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author caojiancheng
 * @date 2022-04-20 20:00
 */
@Data
public class SSDPReqBO {
    /**
     * 设备响应最长等待时间
     * 设备响应在0和这个值之间随机选择响应延迟的值
     * 这样可以为控制点响应平衡网络负载。
     */
    private int mx;

    /**
     * 设置协议查询的类型
     */
    private String man;

    /**
     * 请求行
     */
    private String line;

    /**
     * 身份ua
     */
    private String userAgent;

    /**
     * 服务查询的目标
     */
    private SSDPStEnum stEnum;

    /**
     * 协议保留端口
     */
    private int ssdpPort = 1900;

    /**
     * 协议保留地址
     */
    private String ssdpIp = "239.255.255.250";

    /**
     * 协议host
     */
    private String host = ssdpIp + ":" + ssdpPort;

    public static SSDPReqBO buildDiscover(SSDPStEnum stEnum) {
        SSDPReqBO ssdpReqBO = new SSDPReqBO();
        ssdpReqBO.setMx(5);
        ssdpReqBO.setStEnum(stEnum);
        ssdpReqBO.setMan("\"ssdp:discover\"");
        ssdpReqBO.setLine("M-SEARCH * HTTP/1.1");
        return ssdpReqBO;
    }

    public String toString() {
        Stream<String> stream = Stream.of(line
                , buildItem("MAN", man)
                , buildItem("HOST", host)
                , buildItem("MX", mx + "")
                , buildItem("ST", getStType(stEnum))
                , buildItem("USER-AGENT", userAgent)).filter(Objects::nonNull);
        return stream.collect(Collectors.joining("\r\n", "", "\r\n\r\n"));
    }

    private String buildItem(String key, String value) {
        return value == null ? null : key + ": " + value;
    }

    private String getStType(SSDPStEnum value) {
        return Optional.ofNullable(value).map(SSDPStEnum::getType).orElse(null);
    }
}
