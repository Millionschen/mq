package com.zuihuibao.mq.dispatch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zuihuibao.mq.util.JsonParseHelper;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Created by millions on 16/8/19.
 * 转发rabbitmq的message
 */

public class RabbitmqMessageDispatcher implements MessageDispatcher, BeanFactoryAware {
    private static final Logger logger
            = LoggerFactory.getLogger(RabbitmqMessageDispatcher.class);
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void dispatch(String messageBody) {
        JSONObject jsonObject = JSON.parseObject(messageBody);
        if (null == jsonObject) {
            String errorMsg = "json parse error " + messageBody;
            logger.warn(errorMsg);
            throw new AmqpRejectAndDontRequeueException(errorMsg);
        }
        Optional<String> typeOptional = JsonParseHelper.getString(jsonObject, "type");
        if (!typeOptional.isPresent()) {
            String errorMsg = "no type is present " + messageBody;
            logger.warn(errorMsg);
            throw new AmqpRejectAndDontRequeueException(errorMsg);
        }
        Receiver receiver = beanFactory.getBean(typeOptional.get() + "Receiver", Receiver.class);
        receiver.receive(jsonObject);
    }
}
