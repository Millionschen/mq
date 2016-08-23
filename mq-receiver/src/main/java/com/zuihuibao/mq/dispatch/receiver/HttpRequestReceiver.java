package com.zuihuibao.mq.dispatch.receiver;

import com.alibaba.fastjson.JSONObject;
import com.zuihuibao.mq.util.HttpUtils;
import com.zuihuibao.mq.util.JsonParseHelper;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by millions on 16/8/23.
 * 接收url请求
 */
@Component("HttpRequestReceiver")
public class HttpRequestReceiver
        extends JsonObjectReceiver<HttpRequestReceiver.HttpRequestReceiverDto> {

    private static Logger logger = LoggerFactory.getLogger(HttpRequestReceiver.class);

    private HttpUtils httpUtils;

    @Autowired
    public void setHttpUtils(HttpUtils httpUtils) {
        this.httpUtils = httpUtils;
    }

    @Override
    protected Optional<HttpRequestReceiverDto> parse(JSONObject jsonObject) {
        Optional<String> urlOptional = JsonParseHelper.getString(jsonObject, "url");
        if (urlOptional.isPresent()) {
            return Optional.of(new HttpRequestReceiverDto(urlOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    protected void handle(HttpRequestReceiverDto o) {
        HttpEntity<String> httpEntity = httpUtils.postUrlencodedForm(o.getUrl(), String.class);
        logger.info("response: " + httpEntity.getBody());
    }


    static class HttpRequestReceiverDto {
        private String url;

        HttpRequestReceiverDto(String url) {
            this.url = url;
        }

        String getUrl() {
            return url;
        }
    }
}
