package com.microcosm.homer.service.impl;

import com.microcosm.homer.enums.ResultEnum;
import com.microcosm.homer.model.*;
import com.microcosm.homer.service.DeviceService;
import com.microcosm.homer.service.SSDPService;
import com.microcosm.homer.enums.SSDPStEnum;
import com.microcosm.homer.utils.ArrayExtraUtil;
import com.microcosm.homer.utils.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.microcosm.homer.model.SSDPReqBO.buildDiscover;

/**
 * @author caojiancheng
 * @date 2022-04-21 11:54
 */
@Slf4j
@Service
public class SSDPServiceImpl implements SSDPService {

    @Value("${ssdp.timeout:2000}")
    private long timeout;

    @Autowired
    private DeviceService deviceService;

    private Thread notifyThread;
    private final Set<SSDPStEnum> notifyServiceTypes = new HashSet<>();
    public static final List<DeviceDescBO> notifyDeviceList = new ArrayList<>();
    private final ExecutorService executor = new ThreadPoolExecutor(0, 5,
            0, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public Result<List<DeviceDescBO>> discover(SSDPStEnum ssdpStEnum) {
        List<DeviceDescBO> list = new ArrayList<>();
        SSDPReqBO ssdpReqBO = buildDiscover(ssdpStEnum);
        try (DatagramSocket udpSocket = new DatagramSocket()) {
            int port = ssdpReqBO.getSsdpPort();
            InetAddress address = InetAddress.getByName(ssdpReqBO.getSsdpIp());
            byte[] body = ssdpReqBO.toString().getBytes(StandardCharsets.UTF_8);
            udpSocket.send(new DatagramPacket(body, body.length, address, port));
            receiveSSDP(udpSocket, r -> executor.execute(() -> setDeviceDesc(r, list)));
            return Result.success(list);
        } catch (SocketTimeoutException e) {
            return Result.success(list);
        } catch (Exception e) {
            log.error("组播ssdp搜索设备失败", e);
            return list.isEmpty() ? Result.fail(ResultEnum.SSDP_SEARCH_FAIL) : Result.success(list);
        }
    }

    @Override
    public synchronized Result<Void> receiveNotify(SSDPStEnum ssdpStEnum) {
        notifyServiceTypes.add(ssdpStEnum);
        if (notifyThread == null) {
            notifyThread = new Thread(this::runNotify);
            notifyThread.setDaemon(true);
            notifyThread.start();
        }
        return Result.empty();
    }

    @Override
    public synchronized Result<Void> stopReceiveNotify(SSDPStEnum ssdpStEnum) {
        notifyServiceTypes.remove(ssdpStEnum);
        if (notifyThread != null && notifyServiceTypes.isEmpty()) {
            notifyThread.interrupt();
            notifyThread = null;
        }
        return Result.empty();
    }

    private void runNotify() {
        log.info("ssdp notify监听开始");
        try (MulticastSocket socket = new MulticastSocket(1900)) {
            socket.joinGroup(InetAddress.getByName("239.255.255.250"));
            while (!Thread.currentThread().isInterrupted()) {
                receiveSSDP(socket, this::runNotify);
            }
        } catch (Exception e) {
            log.error("ssdp notify异常", e);
        } finally {
            log.info("ssdp notify监听结束");
        }
    }

    //notifyDeviceList只有一个线程操作，没有并发问题
    private void runNotify(SSDPRespBO ssdpRespBO) {
        if (ssdpRespBO != null) {
            String nts = ssdpRespBO.getNts();
            String url = ssdpRespBO.getLocation();
            SSDPStEnum nt = SSDPStEnum.getEnumByType(ssdpRespBO.getNt());
            if (nts.equals("ssdp:alive") && notifyServiceTypes.contains(nt) &&
                    notifyDeviceList.stream().map(DeviceDescBO::getUrl).noneMatch(url::equals)) {
                setDeviceDesc(ssdpRespBO, notifyDeviceList);
            }
            if (nts.equals("ssdp:byebye")) {
                notifyDeviceList.removeIf(deviceDescBO -> deviceDescBO.getUrl().equals(url));
            }
        }
    }

    private void receiveSSDP(DatagramSocket udpSocket, Consumer<SSDPRespBO> consumer) throws IOException {
        long time;
        int resIndex = 0;
        byte[] res = new byte[1024];
        byte[] data = new byte[1024];
        long endTime = System.currentTimeMillis() + timeout;
        DatagramPacket dp = new DatagramPacket(data, data.length);
        while ((time = endTime - System.currentTimeMillis()) > 0) {
            udpSocket.setSoTimeout((int) time);
            udpSocket.receive(dp);
            int length = dp.getLength();
            for (int i = 0; i < length; i++) {
                if (resIndex == res.length) {
                    res = ArrayExtraUtil.byteExpansion(res, 1024);
                }
                res[resIndex++] = data[i];
                if (NetUtil.headerEnd(res, resIndex)) {
                    String str = new String(res, 0, resIndex);
                    consumer.accept(buildSSDPResp(str));

                    resIndex = 0;
                    res = new byte[1024];
                }
            }
        }
    }

    private void setDeviceDesc(SSDPRespBO ssdpRespBO, List<DeviceDescBO> list) {
        if (ssdpRespBO != null) {
            String location = ssdpRespBO.getLocation();
            Result<DeviceDescBO> result = deviceService.getDeviceDesc(location);
            if (result.success()) {
                DeviceDescBO deviceDescBO = result.getData();
                deviceDescBO.setUrl(location);
                list.add(deviceDescBO);
            }
        }
    }

    private SSDPRespBO buildSSDPResp(String resp) {
        String[] respArray = resp.split("\r\n");
        if (!respArray[0].contains(" 200 OK")) {
            log.error("响应失败:{}", resp);
            return null;
        }
        SSDPRespBO ssdpRespBO = new SSDPRespBO();
        buildSSDPResp(Arrays.stream(respArray), ssdpRespBO);
        return ssdpRespBO;
    }

    private void buildSSDPResp(Stream<String> stream, SSDPRespBO ssdpRespBO) {
        stream.skip(1).filter(h -> h.contains(":")).forEach(item -> {
            int splitIndex = item.indexOf(":");
            String key = item.substring(0, splitIndex).trim().toLowerCase();
            Optional.of(key).map(SSDPRespBO.biConsumerMap::get).ifPresent(biConsumer ->
                    biConsumer.accept(ssdpRespBO, item.substring(splitIndex + 1).trim()));
        });
    }
}
