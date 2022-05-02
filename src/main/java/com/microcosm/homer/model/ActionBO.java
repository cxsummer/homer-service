package com.microcosm.homer.model;

import lombok.Data;

/**
 * @author caojiancheng
 * @date 2022-04-22 18:28
 */
@Data
public class ActionBO {
    /**
     * 动作url
     */
    private String actionUrl;

    /**
     * 资源url
     */
    private String resourceUrl;

    /**
     * 播放进度
     */
    private String progress;

    /**
     * 播放速度
     */
    private String speed;

    /**
     * soap动作
     */
    private String soapAction;
}
