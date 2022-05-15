package com.microcosm.homer.model;

import lombok.Data;

import java.util.List;

/**
 * @author caojiancheng
 * @date 2022-04-21 19:39
 */
@Data
public class DeviceDescBO {
    /**
     * 地址
     */
    private String url;

    /**
     * 根地址
     */
    private String rootUrl;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备名称
     */
    private String friendlyName;

    /**
     * 设备uuid
     */
    private String udn;

    /**
     * 服务列表
     */
    private List<ServiceVO> serviceList;
}
