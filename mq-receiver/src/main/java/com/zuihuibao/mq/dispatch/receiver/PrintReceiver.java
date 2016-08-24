package com.zuihuibao.mq.dispatch.receiver;

import com.alibaba.fastjson.JSONObject;
import com.zuihuibao.mq.util.JsonParseHelper;
import com.zuihuibao.web.dao.UserInfoMapper;
import com.zuihuibao.web.model.UserInfo;
import com.zuihuibao.web.model.UserInfoExample;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by millions on 16/8/20.
 * 测试类 打印出所有接收到的数据
 */

@Component("PrintReceiver")
public class PrintReceiver extends JsonObjectReceiver<PrintReceiver.PrintReceiverDto> {
  private static Logger logger = LoggerFactory.getLogger(PrintReceiver.class);

  @Autowired
  private UserInfoMapper userInfoMapper;

  @Override protected Optional<PrintReceiver.PrintReceiverDto> parse(JSONObject jsonObject) {
    Optional<String> stringOptional = JsonParseHelper.getString(jsonObject, "data");
    if (stringOptional.isPresent()) {
      return Optional.of(new PrintReceiverDto(stringOptional.get()));
    }
    return Optional.empty();
  }

  @Override protected void handle(PrintReceiver.PrintReceiverDto dto) {
    UserInfoExample userInfoExample = new UserInfoExample();
    userInfoExample.createCriteria().andMobileEqualTo("15618930895");
    List<UserInfo> userInfos = userInfoMapper.selectByExample(userInfoExample);
    if (null != userInfos) {
      logger.info("user name:" + userInfos.get(0).getUserId());
    }
    logger.info(dto.getContent());
  }

  static class PrintReceiverDto {
    private String content;

    private PrintReceiverDto(String content) {
      this.content = content;
    }

    private String getContent() {
      return content;
    }
  }
}
