package com.microcosm.homer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

/**
 * @author caojiancheng
 * @date 2022-04-20 15:19
 */
@Slf4j
@RestController
@RequestMapping("/video")
public class VideoController {

    @GetMapping("/m3u8/{path}")
    public void getM3U8(@PathVariable String path, HttpServletResponse response) {
        response.setContentType("application/vnd.apple.mpegurl");
        try (RandomAccessFile ts = new RandomAccessFile("/Users/admin/IdeaProjects/video/" + path + "/local.m3u8", "rw")) {
            byte[] buffer = new byte[(int) ts.length()];
            ts.read(buffer);
            String str = new String(buffer);
            response.getWriter().write(str.replace("{}", ""));
            response.getWriter().flush();
        } catch (Exception e) {
            log.error("错误", e);
        }
    }

    @GetMapping("/ts/{name}/ts/{path}")
    public void getTs(@PathVariable String name, @PathVariable String path, HttpServletResponse response) {
        response.setContentType("image/bmp");
        try (FileInputStream fis = new FileInputStream("/Users/admin/IdeaProjects/video/" + name + "/ts/" + path);
             BufferedInputStream in = new BufferedInputStream(fis);
             BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream())) {
            int count;
            byte[] buffer = new byte[8192];
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            out.flush();
        } catch (Exception e) {
            log.error("错误", e);
        }
    }

}
