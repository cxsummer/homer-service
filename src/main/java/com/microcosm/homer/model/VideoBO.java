package com.microcosm.homer.model;

import lombok.Data;

/**
 * @author caojiancheng
 * @date 2022-05-02 22:13
 */
@Data
public class VideoBO {
    /**
     * 视频名
     */
    private String name;

    /**
     * 视频地址
     */
    private String url;

    /**
     * 视频封面
     */
    private String cover;

    /**
     * 视频标签
     */
    private String label;
}
