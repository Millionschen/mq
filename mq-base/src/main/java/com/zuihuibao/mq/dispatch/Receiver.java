package com.zuihuibao.mq.dispatch;

/**
 * Created by millions on 16/8/19.
 * 接收消息体 处理消息
 */
public interface Receiver {
  void receive(Object message);
}
