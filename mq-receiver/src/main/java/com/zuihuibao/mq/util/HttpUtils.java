package com.zuihuibao.mq.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by millions on 2016/8/23.
 * http utilities
 */
@Component("httpUtils")
public class HttpUtils {
    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> ResponseEntity<T> get(String url, Class<T> tClass, Map<String, ?> uriVariables, Map<String, String> cookies) {
        HttpHeaders requestHeaders = new HttpHeaders();

        Optional<String> cookiesStringOpt = getCookieString(cookies);

        if (cookiesStringOpt.isPresent()) {
            requestHeaders.add("Cookie", cookiesStringOpt.get());
        }
        HttpEntity<T> requestEntity = new HttpEntity<>(null, requestHeaders);
        if (null == uriVariables) {
            uriVariables = new HashMap<>();
        }
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                tClass,
                uriVariables);
    }

    public <T> ResponseEntity<T> get(String url, Class<T> tClass) {
        return get(url, tClass, null, null);
    }

    public <T> ResponseEntity<T> postUrlencodedForm(String url, Class<T> tClass, List<? extends NameValuePair> postParams, Map<String, String> cookies) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Optional<String> cookiesStringOpt = getCookieString(cookies);

        if (cookiesStringOpt.isPresent()) {
            requestHeaders.add("Cookie", cookiesStringOpt.get());
        }

        if (cookiesStringOpt.isPresent()) {
            requestHeaders.add("Cookie", cookiesStringOpt.get());
        }

        String requestBody = null;
        if (null != postParams) {
            requestBody = URLEncodedUtils.format(postParams, Charset.forName("UTF-8"));
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, requestHeaders);
        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                tClass);
    }

    public <T> ResponseEntity<T> postUrlencodedForm(String url, Class<T> tClass) {
        return postUrlencodedForm(url, tClass, null, null);
    }

    private Optional<String> getCookieString(Map<String, String> cookies) {
        Optional<String> cookiesStringOpt = Optional.empty();
        if (null != cookies) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
                sb.append(";");
            }
            cookiesStringOpt = Optional.of(sb.toString());
        }
        return cookiesStringOpt;
    }

}
