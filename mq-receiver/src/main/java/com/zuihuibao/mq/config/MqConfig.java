package com.zuihuibao.mq.config;

import com.zuihuibao.mq.dispatch.MessageDispatcher;
import com.zuihuibao.mq.dispatch.RabbitmqMessageDispatcher;
import java.io.IOException;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.retry.MissingMessageIdAdvice;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.retry.interceptor.StatefulRetryOperationsInterceptor;
import org.springframework.retry.policy.MapRetryContextCache;

/**
 * Created by millions on 16/8/19.
 * message queue 配置
 */

@Configuration
@ComponentScan(value = {"com.zuihuibao.mq.dispatch.receiver"})
@ImportResource(locations = {"classpath*:mybatis/mybatis-spring.xml"})
@Import({AspectConfig.class, RestTemplateConfig.class, SpringConfig.class})
@EnableAspectJAutoProxy
public class MqConfig {

  //rabbit mq 服务器配置
  private
  @Value("${mq.host}")
  String host;
  private
  @Value("${mq.port}")
  String port;
  private
  @Value("${mq.username}")
  String username;
  private
  @Value("${mq.password}")
  String password;

  //queue和exchange配置
  private
  @Value("${mq.queue.name}")
  String queueName;
  private
  @Value("${mq.exchange}")
  String exchange;
  private
  @Value("${mq.exchange.key}")
  String exchangeKey;

  //出错重试配置
  //重试次数
  private
  @Value("${mq.setting.retryNum}")
  int retryNum;
  //初次出错重试的间隔时间
  private
  @Value("${mq.setting.initRetryInterval}")
  int initRetryInterval;
  //出错重试间隔时间增加倍率
  private
  @Value("${mq.setting.retryMultiplier}")
  int retryMultiplier;
  //出错重试最大间隔
  private
  @Value("${mq.setting.retryMaxInterval}")
  int retryMaxInterval;

  @Bean
  public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() throws IOException {
    PropertySourcesPlaceholderConfigurer propertyConfigurer =
        new PropertySourcesPlaceholderConfigurer();
    propertyConfigurer.setLocations(
        new PathMatchingResourcePatternResolver()
            .getResources("classpath*:config/**/*.properties"));
    return propertyConfigurer;
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory connectionFactory =
        new CachingConnectionFactory(host, Integer.parseInt(port));
    connectionFactory.setUsername(username);
    connectionFactory.setPassword(password);
    return connectionFactory;
  }

  @Bean
  public AmqpAdmin amqpAdmin() {
    return new RabbitAdmin(connectionFactory());
  }

  @Bean
  Queue queue() {
    return new Queue(queueName, false);
  }

  @Bean
  TopicExchange exchange() {
    return new TopicExchange(exchange);
  }

  @Bean
  Binding binding() {
    return BindingBuilder.bind(queue()).to(exchange()).with(exchangeKey);
  }

  @Bean
  MessageDispatcher messageDispatcher() {
    return new RabbitmqMessageDispatcher();
  }

  @Bean
  MessageListenerAdapter listenerAdapter() {
    return new MessageListenerAdapter(messageDispatcher(), "dispatch");
  }

  @Bean
  public StatefulRetryOperationsInterceptor statefulRetryOperationsInterceptor() {
    return RetryInterceptorBuilder.stateful()
        .maxAttempts(retryNum)
        .backOffOptions(initRetryInterval,
            retryMultiplier,
            retryMaxInterval) // initialInterval, multiplier, maxInterval
        .build();
  }

  //避免由于客户端没有带messageId导致receiver出现问题
  @Bean
  public MissingMessageIdAdvice missingIdAdvice() {
    return new MissingMessageIdAdvice(new MapRetryContextCache());
  }

  @Bean
  MessageConverter simpleMessageConverter() {
    SimpleMessageConverter messageConverter = new SimpleMessageConverter();
    messageConverter.setCreateMessageIds(true);
    return messageConverter;
  }

  @Bean
  SimpleMessageListenerContainer container() {
    SimpleMessageListenerContainer container
        = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory());
    container.setQueueNames(queueName);
    container.setMessageListener(listenerAdapter());
    container.setAdviceChain(
        new Advice[] {missingIdAdvice(), statefulRetryOperationsInterceptor()});
    container.setMessageConverter(simpleMessageConverter());
    return container;
  }
}
