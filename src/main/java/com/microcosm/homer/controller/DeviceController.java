package com.microcosm.homer.controller;

import com.microcosm.homer.enums.SSDPStEnum;
import com.microcosm.homer.model.DeviceDescBO;
import com.microcosm.homer.model.Result;
import com.microcosm.homer.service.SSDPService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.microcosm.homer.enums.ResultEnum.PARM_SERVER_TYPE_FAIL;

/**
 * @author caojiancheng
 * @date 2022-05-01 21:29
 */
@Slf4j
@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private SSDPService ssdpService;

    @GetMapping("/search")
    public Result<List<DeviceDescBO>> search(String serviceType) {
        return ssdpServiceTemp(serviceType, ssdpService::discover);
    }

    @GetMapping("/notify/start")
    public Result<Void> notifyStart(String serviceType) {
        return ssdpServiceTemp(serviceType, ssdpService::receiveNotify);
    }

    @GetMapping("/notify/stop")
    public Result<Void> notifyStop(String serviceType) {
        return ssdpServiceTemp(serviceType, ssdpService::stopReceiveNotify);
    }

    private <T> Result<T> ssdpServiceTemp(String serviceType, Function<SSDPStEnum, Result<T>> mapper) {
        return Optional.ofNullable(serviceType).map(SSDPStEnum::getEnumByType)
                .map(mapper).orElseGet(() -> Result.fail(PARM_SERVER_TYPE_FAIL));
    }
}
