package com.zuihuibao.mq.service.impl;

import com.alibaba.fastjson.JSON;
import com.zuihuibao.mq.service.SessionService;
import com.zuihuibao.mq.util.enums.DbConst;
import com.zuihuibao.web.dao.SessionMapper;
import com.zuihuibao.web.model.Session;
import org.apache.ibatis.jdbc.RuntimeSqlException;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SessionServiceImpl implements SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    @Autowired
    private SessionMapper sessionMapper;

    @Override
    public Session ensureUserSession(long userId) {
        Session session = null;
        try {
            session = sessionMapper.selectByUserIdAndHasLogin(userId, DbConst.SESSION_HAS_LOGIN.value());
        } catch (Exception e) {
            logger.info("ensureUserSession error:" + e.getCause() + " message:" + e.getMessage());
        }
        if (null == session) {
            session = new Session();
            session.setHasLogin(DbConst.SESSION_HAS_LOGIN.value());
            session.setUserId(userId);
            session.setLoginType(DbConst.SESSION_APP_LOGIN.value());
            session.setSessionId(UuidUtil.getTimeBasedUuid().toString());
            if (0 == sessionMapper.insertSelective(session)) {
                logger.info("insert into session fail:" + JSON.toJSONString(session));
                throw new RuntimeSqlException("cannot insert session");
            }
        }
        return session;
    }
}
