package com.microcosm.homer.service.impl;

import com.alibaba.fastjson2.JSON;
import com.microcosm.homer.enums.ResultEnum;
import com.microcosm.homer.enums.UPNPActionEnum;
import com.microcosm.homer.model.*;
import com.microcosm.homer.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.microcosm.homer.utils.HttpUtil.httpGet;
import static com.microcosm.homer.utils.HttpUtil.httpPostXml;

/**
 * @author caojiancheng
 * @date 2022-04-21 19:57
 */
@Slf4j
@Service
public class DeviceServiceImpl implements DeviceService {

    @Override
    public Result<DeviceDescBO> getDeviceDesc(String desUrl) {
        HttpRespBO httpRespBO = httpGet(desUrl);
        return Optional.ofNullable(httpRespBO).map(this::buildDeviceDesc)
                .map(Result::success).orElseGet(() -> Result.fail(ResultEnum.GET_DEVICE_DESC_FAIL));
    }

    @Override
    public Result<Void> setResourceUrl(ActionBO actionBO) {
        String progress = actionBO.getProgress();
        String resourceUrl = actionBO.getResourceUrl();
        String xml = UPNPActionEnum.SET_URI.getXmlText();
        xml = xml.replace("{0}", progress).replace("{1}", resourceUrl);
        return executeAction(actionBO, xml);
    }

    @Override
    public Result<Void> playResource(ActionBO actionBO) {
        String speed = actionBO.getSpeed();
        String progress = actionBO.getProgress();
        String xml = UPNPActionEnum.PLAY.getXmlText();
        xml = xml.replace("{0}", progress).replace("{1}", speed);
        return executeAction(actionBO, xml);
    }

    private Result<Void> executeAction(ActionBO actionBO, String xml) {
        String actionUrl = actionBO.getActionUrl();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("SOAPACTION", actionBO.getSoapAction());
        HttpRespBO httpRespBO = httpPostXml(actionUrl, xml, headerMap);
        return Optional.ofNullable(httpRespBO).filter(HttpRespBO::success).map(r -> Result.empty()).orElseGet(() -> {
            log.error("执行动作失败,{},{},{}", actionUrl, xml, HttpRespBO.getMsg(httpRespBO));
            return Result.fail("设置播放资源失败");
        });
    }

    private DeviceDescBO buildDeviceDesc(HttpRespBO httpRespBO) {
        try {
            if (!httpRespBO.ok()) {
                log.error("设备描述响应错误:{}", JSON.toJSONString(httpRespBO));
                return null;
            }
            byte[] body = httpRespBO.getBody();
            DeviceDescBO deviceDescBO = new DeviceDescBO();
            deviceDescBO.setServiceList(new ArrayList<>());
            String xml = new String(body, StandardCharsets.UTF_8);
            Document doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();
            Element recordEle = rootElt.element("device");
            Element serviceList = recordEle.element("serviceList");
            Iterator<?> iterator = serviceList.elementIterator("service");
            deviceDescBO.setDeviceType(recordEle.elementTextTrim("deviceType"));
            deviceDescBO.setFriendlyName(recordEle.elementTextTrim("friendlyName"));
            while (iterator.hasNext()) {
                ServiceVO serviceVO = new ServiceVO();
                deviceDescBO.getServiceList().add(serviceVO);
                Element serviceElement = (Element) iterator.next();
                serviceVO.setScpDUrl(serviceElement.elementTextTrim("SCPDURL"));
                serviceVO.setServiceId(serviceElement.elementTextTrim("serviceId"));
                serviceVO.setControlUrl(serviceElement.elementTextTrim("controlURL"));
                serviceVO.setServiceType(serviceElement.elementTextTrim("serviceType"));
                serviceVO.setEventSubUrl(serviceElement.elementTextTrim("eventSubURL"));
            }
            return deviceDescBO;
        } catch (DocumentException e) {
            log.error("设备描述响应解析失败:{}", JSON.toJSONString(httpRespBO), e);
            return null;
        }
    }
}
