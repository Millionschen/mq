package com.zuihuibao.mq.dispatch.receiver;

import com.alibaba.fastjson.JSONObject;
import com.zuihuibao.mq.util.JsonParseHelper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by millions on 16/8/23.
 * 接收url请求
 */
@Component("httpRequestReceiver")
public class HttpRequestReceiver
    extends JsonObjectReceiver<HttpRequestReceiver.HttpRequestReceiverDto> {

  @Autowired
  private RestTemplate restTemplate;

  @Override
  protected Optional<HttpRequestReceiverDto> parse(JSONObject jsonObject) {
    Optional<String> urlOptional = JsonParseHelper.getString(jsonObject, "url");
    if (urlOptional.isPresent()) {
      return Optional.of(new HttpRequestReceiverDto(urlOptional.get()));
    }
    return Optional.empty();
  }

  @Override protected void handle(HttpRequestReceiverDto o) {

  }

  static class HttpRequestReceiverDto {
    private String url;

    HttpRequestReceiverDto(String url) {
      this.url = url;
    }

    public String getUrl() {
      return url;
    }
  }
}
