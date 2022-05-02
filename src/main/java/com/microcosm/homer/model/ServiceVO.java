package com.microcosm.homer.model;

import lombok.Data;

/**
 * @author caojiancheng
 * @date 2022-04-21 19:47
 */
@Data
public class ServiceVO {
    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 服务表示符，是服务实例的唯一标识
     */
    private String serviceId;

    /**
     * 向服务发出控制消息的URL
     */
    private String controlUrl;

    /**
     * 订阅该服务事件的URL
     */
    private String eventSubUrl;

    /**
     * 设备描述文档URL
     */
    private String scpDUrl;
}
