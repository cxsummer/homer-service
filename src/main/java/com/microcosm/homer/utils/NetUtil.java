package com.microcosm.homer.utils;

import java.util.Optional;

/**
 * @author caojiancheng
 * @date 2022-04-21 15:55
 */
public class NetUtil {

    private NetUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 响应头是否结束
     */
    public static boolean headerEnd(byte[] bytes, int index) {
        return index > 3
                && bytes[index - 1] == '\n'
                && bytes[index - 2] == '\r'
                && bytes[index - 3] == '\n'
                && bytes[index - 4] == '\r';
    }

    public static String resolveRootUrl(String url) {
        return Optional.ofNullable(url)
                .map(String::toCharArray)
                .map(NetUtil::resolveRootUrl).orElse(null);
    }

    public static String resolveRootUrl(char[] chars) {
        int i = 0;
        int num = 0;
        for (; i < chars.length; i++) {
            char c = chars[i];
            if (c == '/') {
                num++;
            }
            if (num == 3) {
                break;
            }
        }
        return num > 1 ? new String(chars, 0, i) : null;
    }
}
