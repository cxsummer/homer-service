package com.microcosm.homer.test;

import com.alibaba.fastjson2.JSON;
import com.microcosm.homer.HomerApplication;
import com.microcosm.homer.enums.SSDPStEnum;
import com.microcosm.homer.model.DeviceDescBO;
import com.microcosm.homer.model.Result;
import com.microcosm.homer.service.SSDPService;
import com.microcosm.homer.service.impl.SSDPServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author caojiancheng
 * @date 2022-04-21 17:50
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HomerApplication.class)
public class SSDPTest {

    @Autowired
    private SSDPService ssdpService;

    @Test
    public void discoverTest() {
        Result<List<DeviceDescBO>> result = ssdpService.discover(SSDPStEnum.AV_TRANSPORT_V1);
        Assert.assertTrue(result.success());
        System.out.println(JSON.toJSONString(result));
    }

    @Test
    public void notifyTest() throws InterruptedException {
        Result<Void> result = ssdpService.receiveNotify(SSDPStEnum.AV_TRANSPORT_V1);
        Assert.assertTrue(result.success());
        while (true) {
            List<DeviceDescBO> list = SSDPServiceImpl.notifyDeviceList;
            System.out.println(list);
            Thread.sleep(5000);
        }
    }
}
