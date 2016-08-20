package com.zuihuibao.mq.dispatch.receiver;

import com.alibaba.fastjson.JSONObject;
import com.zuihuibao.mq.dispatch.Receiver;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

abstract class JsonObjectReceiver<T> implements Receiver {

  Logger logger = LoggerFactory.getLogger(JsonObjectReceiver.class);

  @Override public void receive(Object object) {
    if (!(object instanceof JSONObject)) {
      throw new AmqpRejectAndDontRequeueException("message is not JSONObject" + object);
    }
    JSONObject jsonObject = (JSONObject) object;
    Optional<T> optional = parse(jsonObject);
    if (!optional.isPresent()) {
      String msg = "message parse error " + jsonObject.toJSONString();
      logger.info(msg);
      throw new AmqpRejectAndDontRequeueException(msg);
    }
    handle(optional.get());
  }

  abstract protected Optional<T> parse(JSONObject jsonObject);

  abstract protected void handle(T o);
}
