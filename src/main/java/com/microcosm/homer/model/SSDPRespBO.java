package com.microcosm.homer.model;

import com.microcosm.homer.utils.NetUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author caojiancheng
 * @date 2022-04-20 20:00
 */
@Data
public class SSDPRespBO {

    /**
     * 设备描述的URL地址
     */
    private String location;

    /**
     * max-age指定通知消息存活时间，（如：max-age=1800）
     * 如果超过此时间间隔，控制点可以认为设备不存在
     */
    private String cacheControl;

    /**
     * 不同服务的统一服务名
     */
    private String usn;

    /**
     * 服务类型
     */
    private String st;

    /**
     * 响应的服务类型
     */
    private String nt;

    /**
     * 设备上下线宣告消息
     */
    private String nts;

    public static final Map<String, BiConsumer<SSDPRespBO, String>> biConsumerMap;

    static {
        biConsumerMap = new HashMap<>();
        biConsumerMap.put("st", SSDPRespBO::setSt);
        biConsumerMap.put("nt", SSDPRespBO::setNt);
        biConsumerMap.put("nts", SSDPRespBO::setNts);
        biConsumerMap.put("usn", SSDPRespBO::setUsn);
        biConsumerMap.put("location", SSDPRespBO::setLocation);
        biConsumerMap.put("cache-control", SSDPRespBO::setCacheControl);
    }
}
