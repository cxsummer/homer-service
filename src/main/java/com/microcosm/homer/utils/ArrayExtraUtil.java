package com.microcosm.homer.utils;

import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @author caojiancheng
 * @date 2022-04-21 15:06
 */
public class ArrayExtraUtil {

    private ArrayExtraUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * byte数组扩容
     */
    public static byte[] byteExpansion(byte[] origin, int num) {
        return Optional.ofNullable(origin).map(o -> {
            byte[] temp = new byte[o.length + num];
            IntStream.range(0, o.length).forEach(i -> temp[i] = o[i]);
            return temp;
        }).orElseGet(() -> new byte[num]);
    }
}
