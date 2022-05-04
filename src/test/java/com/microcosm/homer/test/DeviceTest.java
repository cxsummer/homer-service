package com.microcosm.homer.test;

import com.microcosm.homer.HomerApplication;
import com.microcosm.homer.enums.SSDPStEnum;
import com.microcosm.homer.model.*;
import com.microcosm.homer.service.DeviceService;
import com.microcosm.homer.service.SSDPService;
import com.microcosm.homer.utils.NetUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author caojiancheng
 * @date 2022-04-22 18:19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HomerApplication.class)
public class DeviceTest {

    @Autowired
    private SSDPService ssdpService;

    @Autowired
    private DeviceService deviceService;

    @Test
    public void discoverTest() {
        Result<List<DeviceDescBO>> ssdpResult = ssdpService.discover(SSDPStEnum.AV_TRANSPORT_V1);
        List<DeviceDescBO> list = ssdpResult.getData();
        System.out.println(list);

        DeviceDescBO deviceDescBO = list.get(0);
        System.out.println(deviceDescBO.getFriendlyName());
        List<ServiceVO> serviceList = deviceDescBO.getServiceList();
        ServiceVO serviceVO = serviceList.stream().filter(s -> SSDPStEnum.AV_TRANSPORT_V1.getType().equals(s.getServiceType())).findFirst().orElse(null);

        ActionBO urlAction = new ActionBO();
        urlAction.setProgress("0");
        urlAction.setSoapAction("\"" + serviceVO.getServiceType() + "#SetAVTransportURI\"");
        urlAction.setResourceUrl("http://192.168.8.2:8088/video/m3u8/100");
        urlAction.setActionUrl(NetUtil.resolveRootUrl(deviceDescBO.getUrl())+"/" + serviceVO.getControlUrl());
        Result<Void> setUrlResult = deviceService.setResourceUrl(urlAction);
        System.out.println(setUrlResult.success());
    }
}
