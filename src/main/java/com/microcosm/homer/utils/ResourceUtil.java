package com.microcosm.homer.utils;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author caojiancheng
 * @date 2022-04-22 16:44
 */
@Slf4j
public class ResourceUtil {

    private ResourceUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final LoadingCache<String, String> fileTextCache = Caffeine.newBuilder()
            .maximumSize(10).expireAfterAccess(100, TimeUnit.MINUTES).build(ResourceUtil::getFileText);

    public static String getFileText(String path) {
        int len;
        ClassPathResource classPathResource = new ClassPathResource(path);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             InputStream inputStream = classPathResource.getInputStream()) {
            byte[] bytes = new byte[inputStream.available()];
            while ((len = inputStream.read(bytes)) > -1) {
                bos.write(bytes, 0, len);
            }
            return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("获取{}文件失败", path, e);
            return null;
        }
    }
}
