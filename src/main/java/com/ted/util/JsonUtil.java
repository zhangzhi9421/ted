package com.ted.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Json工具类
 *
 * @author Ted
 */
@Data
@Component
public class JsonUtil<T> {
    /**
     * 返回码：成功
     */
    public static final int SUCCESS_CODE = 200;
    /**
     * 返回码：失败
     */
    public static final int ERROR_CODE = 400;
    /**
     * 返回码：其他
     */
    public static final int OTHER_ERROR_CODE = 300;
    /**
     * Jackson对象
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();
    // 状态码
    private int code = SUCCESS_CODE;
    // 状态描述
    private String message = "";
    // 返回数据
    private T data;

    /**
     * 构造方法
     */
    public JsonUtil() {
    }

    /**
     * 成功
     *
     * @param data 返回数据
     */
    public JsonUtil(T data) {
        this.data = data;
    }

    /**
     * 自定义返回值
     *
     * @param code    状态码
     * @param message 状态描述
     * @param data    返回数据
     */
    public JsonUtil(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功
     *
     * @return Json处理结果
     */
    public String success() {
        return getResult(SUCCESS_CODE, this.message, this.data);
    }

    /**
     * 成功
     *
     * @param data 返回数据
     * @return Json处理结果
     */
    public String success(T data) {
        return getResult(SUCCESS_CODE, this.message, data);
    }

    /**
     * 成功
     *
     * @param message 状态描述
     * @param data    返回数据
     * @return Json结果
     */
    public String success(String message, T data) {
        return getResult(SUCCESS_CODE, message, data);
    }

    /**
     * 错误（默认400）
     *
     * @return Json处理结果
     */
    public String error() {
        return getResult(ERROR_CODE, this.message, this.data);
    }

    /**
     * 错误（默认400）
     *
     * @param data 返回数据
     * @return Json结果
     */
    public String error(T data) {
        return getResult(ERROR_CODE, this.message, data);
    }

    /**
     * 错误（默认400）
     *
     * @param message 状态描述
     * @param data    返回数据
     * @return Json结果
     */
    public String error(String message, T data) {
        return getResult(ERROR_CODE, message, data);
    }

    /**
     * 错误（默认400）
     *
     * @param code    状态码
     * @param message 状态描述
     * @param data    返回数据
     * @return Json结果
     */
    public String error(int code, String message, T data) {
        return getResult(code, message, data);
    }

    /**
     * 返回结果
     *
     * @param code    状态码
     * @param message 状态描述
     * @param data    返回数据
     * @return Json结果
     */
    private String getResult(int code, String message, T data) {
        JsonUtil<Object> jsonUtil = new JsonUtil<>(code, message, data);
        String result = null;
        try {
            result = MAPPER.writeValueAsString(jsonUtil);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
