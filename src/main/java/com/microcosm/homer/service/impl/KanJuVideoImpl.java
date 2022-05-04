package com.microcosm.homer.service.impl;

import com.microcosm.homer.model.HttpRespBO;
import com.microcosm.homer.model.Result;
import com.microcosm.homer.model.VideoBO;
import com.microcosm.homer.service.VideoAbstract;
import com.microcosm.homer.utils.HttpUtil;
import com.microcosm.homer.utils.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author caojiancheng
 * @date 2022-05-02 22:23
 */
@Slf4j
@Service
public class KanJuVideoImpl extends VideoAbstract {

    private static final String ROOT_URL = "https://www.789kanju.com";
    private static final String SEARCH_URL = ROOT_URL + "/index.php/vod/search.html?wd=";
    private final Pattern m3u8Pat = Pattern.compile("player_aaaa.*?\"url\":\"(.*?)\"");
    private final Pattern videoDetailPat = Pattern.compile("<a href=\"(.*?)\">(.*?)</a>");
    private final Pattern videosPat = Pattern.compile("<ul class=\"stui-content__playlist clearfix\">(.*?)</ul>");
    private final Pattern searchPat = Pattern.compile("<a class=\"stui-vodlist__thumb lazyload\".*?" +
            "href=\"(.*?)\" title=\"(.*?)\" data-original=\"(.*?)\".*?<span class=\"pic-text text-right\">(.*?)</span>");

    private final ExecutorService executor = new ThreadPoolExecutor(50, 50,
            0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public Result<List<VideoBO>> search(String keyword) {
        String url = SEARCH_URL + keyword;
        HttpRespBO httpRespBO = HttpUtil.httpGet(url);
        return Optional.ofNullable(httpRespBO).map(resp -> {
            String res = new String(resp.getBody(), StandardCharsets.UTF_8);
            List<VideoBO> list = new ArrayList<>();
            Matcher matcher = searchPat.matcher(res);
            while (matcher.find()) {
                VideoBO videoBO = new VideoBO();
                videoBO.setName(matcher.group(2));
                videoBO.setCover(matcher.group(3));
                videoBO.setLabel(matcher.group(4));
                videoBO.setUrl(ROOT_URL + matcher.group(1));
                list.add(videoBO);
            }
            return Result.success(list);
        }).orElseGet(() -> Result.fail(url + "搜索失败"));
    }

    @Override
    public Result<List<VideoBO>> detail(String url) {
        HttpRespBO httpRespBO = HttpUtil.httpGet(url);
        return Optional.ofNullable(httpRespBO).map(resp -> {
            String res = new String(resp.getBody(), StandardCharsets.UTF_8);
            String content = "";
            List<VideoBO> list = new ArrayList<>();
            Matcher matcher = videosPat.matcher(res);
            if (matcher.find()) {
                String item = matcher.group();
                content = item;
            }
            matcher = videoDetailPat.matcher(content);
            while (matcher.find()) {
                VideoBO videoBO = new VideoBO();
                videoBO.setName(matcher.group(2));
                videoBO.setUrl(ROOT_URL + matcher.group(1));
                list.add(videoBO);
            }
            return Result.success(list);
        }).orElseGet(() -> Result.fail(url + "获取详情失败"));
    }

    @Override
    public Result<Void> down(VideoBO videoBO) {
        String url = videoBO.getUrl();
        String name = videoBO.getName();
        HttpRespBO httpRespBO = HttpUtil.httpGet(url);
        String res = new String(httpRespBO.getBody(), StandardCharsets.UTF_8);
        Matcher matcher = m3u8Pat.matcher(res);
        if (matcher.find()) {
            String m3u8Url = matcher.group(1).replace("\\", "");
            HttpRespBO m3u8Resp = HttpUtil.httpGet(m3u8Url);
            res = new String(m3u8Resp.getBody(), StandardCharsets.UTF_8);
            String[] arrays = res.split("\n");
            m3u8Resp = HttpUtil.httpGet(NetUtil.resolveRootUrl(m3u8Url) + arrays[2]);
            res = new String(m3u8Resp.getBody(), StandardCharsets.UTF_8);
            String rootPath = "/Users/admin/IdeaProjects/video/" + name;
            File file = new File(rootPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            try (RandomAccessFile originM3u8File = new RandomAccessFile(rootPath + "/origin.m3u8", "rw")) {
                originM3u8File.writeBytes(res);
            } catch (Exception e) {
                log.error("错误", e);
            }
            StringBuilder local = new StringBuilder();
            arrays = res.split("\n");
            AtomicInteger i = new AtomicInteger();
            List<String> urlList = new ArrayList<>();
            Arrays.stream(arrays).forEach(a -> {
                if (!a.startsWith("#")) {
                    urlList.add(a);
                    a = "{}/video/ts/" + name + "/ts/" + i.getAndIncrement() + ".ts";
                }
                local.append(a).append("\n");
            });
            try (RandomAccessFile localM3u8File = new RandomAccessFile(rootPath + "/local.m3u8", "rw")) {
                localM3u8File.writeBytes(local.toString());
            } catch (Exception e) {
                log.error("错误", e);
            }
            String rootPath2 = rootPath + "/" + "ts";
            file = new File(rootPath2);
            if (!file.exists()) {
                file.mkdirs();
            }
            List<Future<Boolean>> futureList = IntStream.range(0, urlList.size()).mapToObj(ui -> executor.submit(() -> {
                String urlItem = urlList.get(ui);
                HttpRespBO respBO = HttpUtil.httpGet(urlItem);
                if (respBO == null) {
                    respBO = HttpUtil.httpGet(urlItem);
                    if (respBO == null) {
                        respBO = HttpUtil.httpGet(urlItem);
                        if (respBO == null) {
                            respBO = HttpUtil.httpGet(urlItem);
                        }
                    }
                }
                try (RandomAccessFile ts = new RandomAccessFile(rootPath2 + "/" + ui + ".ts", "rw")) {
                    ts.write(respBO.getBody());
                    return true;
                } catch (Exception e) {
                    log.error("错误aaa", e);
                    return false;
                }
            })).collect(Collectors.toList());
            futureList.forEach(future -> {
                try {
                    future.get();
                } catch (Exception e) {
                    log.error("错误", e);
                }
            });
            log.info("完成");
        }
        return null;
    }

    public static void main(String[] args) {
        /*AtomicInteger i = new AtomicInteger();
        KanJuVideoImpl kanJuVideo = new KanJuVideoImpl();
        Result<List<VideoBO>> result = kanJuVideo.detail("https://www.789kanju.com/index.php/vod/detail/id/501093.html");
        result.getData().forEach(videoBO -> {
            videoBO.setName(i.incrementAndGet() + "");
            kanJuVideo.down(videoBO);
        });*/

        VideoBO videoBO = new VideoBO();
        videoBO.setName("100");
        videoBO.setUrl("https://www.789kanju.com/index.php/vod/play/id/843314/sid/8/nid/1.html");
        KanJuVideoImpl kanJuVideo = new KanJuVideoImpl();
        kanJuVideo.down(videoBO);
    }
}
