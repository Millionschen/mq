package com.zuihuibao.mq.service;

import com.zuihuibao.web.model.Session;
import org.springframework.stereotype.Service;

/**
 * Created by millions on 2016/8/28.
 * 操作用户session
 */

public interface SessionService {

    /**
     * 根据用户id查询session 如果没有session 则生成一个session
     * @param userId int
     * @return Session | null
     */
    Session ensureUserSession(long userId);
}
