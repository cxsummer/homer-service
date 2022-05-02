package com.microcosm.homer.service;

import com.microcosm.homer.model.DeviceDescBO;
import com.microcosm.homer.model.Result;
import com.microcosm.homer.enums.SSDPStEnum;

import java.util.List;

/**
 * @author caojiancheng
 * @date 2022-04-20 19:49
 */
public interface SSDPService {
    /**
     * 多播搜索消息
     *
     * @param ssdpStEnum 服务类型
     * @return Result
     */
    Result<List<DeviceDescBO>> discover(SSDPStEnum ssdpStEnum);

    /**
     * 接收此服务类型的组播信息
     *
     * @param ssdpStEnum 服务类型
     * @return Result
     */
    Result<Void> receiveNotify(SSDPStEnum ssdpStEnum);

    /**
     * 停止接收此服务类型的组播信息
     *
     * @param ssdpStEnum 服务类型
     * @return Result
     */
    Result<Void> stopReceiveNotify(SSDPStEnum ssdpStEnum);
}
