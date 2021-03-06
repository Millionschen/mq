package com.zuihuibao.mq.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by millions on 16/8/23.
 * http请求的restTemplate
 */

@Configuration
public class RestTemplateConfig {

  private
  @Value("${http.maxTotal}")
  int maxTotal;
  private
  @Value("${http.defaultMaxPerRoute}")
  int defaultMaxPerRoute;
  private
  @Value("${http.connectTimeout}")
  int connectTimeout;
  private
  @Value("${http.readTimeout}")
  int readTimeout;

  @Bean
  public HttpClientConnectionManager httpClientConnectionManager() {
    PoolingHttpClientConnectionManager poolingHttpClientConnectionManager
        = new PoolingHttpClientConnectionManager();
    poolingHttpClientConnectionManager.setMaxTotal(maxTotal);
    poolingHttpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
    return poolingHttpClientConnectionManager;
  }

  @Bean
  public HttpClientBuilder httpClientBuilder() {
    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
    httpClientBuilder.setConnectionManager(httpClientConnectionManager());
    return httpClientBuilder;
  }

  @Bean
  public HttpClient httpClient() {
    return httpClientBuilder().setRedirectStrategy(new LaxRedirectStrategy()).build();
  }

  @Bean
  public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory factory
        = new HttpComponentsClientHttpRequestFactory(httpClient());
    factory.setConnectTimeout(connectTimeout);
    factory.setReadTimeout(readTimeout);
    return factory;
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate
        = new RestTemplate(httpComponentsClientHttpRequestFactory());
    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    messageConverters.add(new StringHttpMessageConverter());
    //        messageConverters.add(new MarshallingHttpMessageConverter());
    messageConverters.add(new FormHttpMessageConverter());
    messageConverters.add(new FastJsonHttpMessageConverter());
    restTemplate.setMessageConverters(messageConverters);
    return restTemplate;
  }
}
