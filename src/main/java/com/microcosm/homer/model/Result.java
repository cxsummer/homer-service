package com.microcosm.homer.model;

import com.microcosm.homer.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author caojiancheng
 * @date 2022-04-21 16:14
 */
@Data
@AllArgsConstructor
public class Result<T> {

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应描述
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public boolean failed() {
        return code != ResultEnum.SUCCESS.getCode();
    }

    public boolean success() {
        return code == ResultEnum.SUCCESS.getCode();
    }

    public static <E> Result<E> fail() {
        return fail(ResultEnum.FAIL.getMessage());
    }

    public static <E> Result<E> fail(String message) {
        return new Result<>(ResultEnum.FAIL.getCode(), message, null);
    }

    public static <E> Result<E> fail(ResultEnum resultEnum) {
        return new Result<>(resultEnum.getCode(), resultEnum.getMessage(), null);
    }

    public static Result<Void> empty() {
        return success(null);
    }

    public static <E> Result<E> success(E message) {
        return new Result<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), message);
    }
}
