package com.microcosm.homer.test;

import com.microcosm.homer.HomerApplication;
import com.microcosm.homer.enums.SSDPStEnum;
import com.microcosm.homer.model.*;
import com.microcosm.homer.service.DeviceService;
import com.microcosm.homer.service.SSDPService;
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
        /*SSDPRespBO ssdpRespBO = list.get(0);
        String desUrl = ssdpRespBO.getLocation();
        Result<DeviceDescBO> deviceDesc = deviceService.getDeviceDesc(desUrl);
        DeviceDescBO deviceDescBO = deviceDesc.getData();
        System.out.println(deviceDescBO.getFriendlyName());
        List<ServiceVO> serviceList = deviceDescBO.getServiceList();
        ServiceVO serviceVO = serviceList.stream().filter(s -> SSDPStEnum.AV_TRANSPORT_V1.getType().equals(s.getServiceType())).findFirst().orElse(null);
        int num = 0;
        StringBuilder url = new StringBuilder();
        for (int i = 0; i < desUrl.length(); i++) {
            if (desUrl.charAt(i) == '/') {
                num++;
            }
            url.append(desUrl.charAt(i));
            if (num == 3) {
                break;
            }
        }
        ActionBO urlAction = new ActionBO();
        urlAction.setProgress("0");
        urlAction.setSoapAction("\"" + serviceVO.getServiceType() + "#SetAVTransportURI\"");
        urlAction.setResourceUrl("");
        urlAction.setActionUrl(url + serviceVO.getControlUrl());
        Result<Void> setUrlResult = deviceService.setResourceUrl(urlAction);
        System.out.println(setUrlResult.success());
        Assert.assertTrue(result.success());
        System.out.println(JSON.toJSONString(result));*/
    }
}
