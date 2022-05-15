package com.microcosm.homer.service;

import com.microcosm.homer.model.ActionBO;
import com.microcosm.homer.model.DeviceDescBO;
import com.microcosm.homer.model.Result;

/**
 * @author caojiancheng
 * @date 2022-04-21 19:54
 */
public interface DeviceService {

    /**
     * 获取设备描述
     *
     * @param desUrl 设备描述url
     * @return Result
     */
    Result<DeviceDescBO> getDeviceDesc(String desUrl);

    /**
     * 设置播放资源
     *
     * @param actionBO 动作
     * @return Result
     */
    Result<Void> setResourceUrl(ActionBO actionBO);

    /**
     * 播放资源
     *
     * @param actionBO 动作
     * @return Result
     */
    Result<Void> playResource(ActionBO actionBO);


}
