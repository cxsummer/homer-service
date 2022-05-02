package com.microcosm.homer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author caojiancheng
 * @date 2022-04-21 16:19
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {

    SUCCESS(0, "success"),
    FAIL(1, "fail"),
    SSDP_SEARCH_FAIL(2, "ssdp搜索设备失败"),
    GET_DEVICE_DESC_FAIL(3, "请求设备描述地址失败"),
    PARM_SERVER_TYPE_FAIL(3, "参数服务类型错误");

    private int code;
    private String message;
}
