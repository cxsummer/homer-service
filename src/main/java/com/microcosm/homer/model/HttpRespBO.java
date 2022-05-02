package com.microcosm.homer.model;

import com.alibaba.fastjson2.JSON;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author caojiancheng
 * @date 2022-04-22 10:19
 */
@Data
public class HttpRespBO {
    /**
     * 响应码
     */
    private int code;

    /**
     * 响应描述
     */
    private String message;

    /**
     * 响应头
     */
    Map<String, List<String>> headerMap;

    /**
     * 响应体
     */
    private byte[] body;

    public boolean success() {
        return code == 200;
    }

    public boolean ok() {
        return code == 200 && body != null;
    }

    public static String getMsg(HttpRespBO httpRespBO) {
        return Optional.ofNullable(httpRespBO).map(resp -> "code:" + resp.code +
                ",msg:" + resp.message + ",header:" + JSON.toJSONString(resp.headerMap) +
                ",body:" + new String(resp.body, StandardCharsets.UTF_8)).orElse("");
    }
}
