package com.microcosm.homer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.microcosm.homer.utils.ResourceUtil.fileTextCache;

/**
 * @author caojiancheng
 * @date 2022-04-22 17:01
 */
@Getter
@AllArgsConstructor
public enum UPNPActionEnum {

    PLAY("upnp/action/play.xml", "播放资源"),
    SET_URI("upnp/action/set_uri.xml", "设置播放资源url");

    private String path;
    private String desc;

    public String getXmlText() {
        return fileTextCache.get(path);
    }
}
