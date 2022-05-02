package com.microcosm.homer.utils;

import com.microcosm.homer.model.HttpRespBO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author caojiancheng
 * @date 2022-04-21 20:00
 */
@Slf4j
public class HttpUtil {

    private static final MediaType textXmlMediaType = MediaType.parse("text/xml");
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS).readTimeout(3, TimeUnit.SECONDS).build();

    private HttpUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static HttpRespBO httpGet(String url) {
        return httpGet(url, null);
    }

    public static HttpRespBO httpPost(String url) {
        return httpPost(url, null, null);
    }

    public static HttpRespBO httpPost(String url, Map<String, String> bodyMap) {
        return httpPost(url, null, bodyMap);
    }

    public static HttpRespBO httpGet(String url, Map<String, String> headerMap) {
        Headers headers = buildHeaders(headerMap);
        return execute(new Request.Builder().url(url).headers(headers).build());
    }

    public static HttpRespBO httpPost(String url, Map<String, String> headerMap, Map<String, String> bodyMap) {
        Headers headers = buildHeaders(headerMap);
        FormBody.Builder builder = new FormBody.Builder();
        Optional.ofNullable(bodyMap).orElseGet(HashMap::new).forEach(builder::add);
        return execute(new Request.Builder().url(url).headers(headers).post(builder.build()).build());
    }

    public static HttpRespBO httpPostXml(String url, String xml, Map<String, String> headerMap) {
        Headers headers = buildHeaders(headerMap);
        RequestBody body = RequestBody.create(xml, textXmlMediaType);
        return execute(new Request.Builder().url(url).headers(headers).post(body).build());
    }

    public static HttpRespBO execute(Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            HttpRespBO httpRespBO = new HttpRespBO();
            ResponseBody responseBody = response.body();
            httpRespBO.setCode(response.code());
            httpRespBO.setMessage(response.message());
            httpRespBO.setHeaderMap(response.headers().toMultimap());
            httpRespBO.setBody(responseBody == null ? null : responseBody.bytes());
            return httpRespBO;
        } catch (Exception e) {
            log.error("http {}请求失败:{}", request.method(), request.url(), e);
            return null;
        }
    }

    private static Headers buildHeaders(Map<String, String> headerMap) {
        headerMap = Optional.ofNullable(headerMap).orElseGet(HashMap::new);
        headerMap.putIfAbsent("User-Agent", "homer");
        return Headers.of(headerMap);
    }

}
