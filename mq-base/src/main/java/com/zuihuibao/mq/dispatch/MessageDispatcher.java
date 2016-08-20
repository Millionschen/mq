package com.zuihuibao.mq.dispatch;

/**
 * Created by millions on 16/8/17. 消息分发接口 从消息队列获取消息体并进行分发
 */
public interface MessageDispatcher {
  void dispatch(String messageBody);
}
