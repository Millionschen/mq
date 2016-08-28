package com.zuihuibao.mq.service.impl;

import com.google.common.collect.Sets;
import com.zuihuibao.mq.service.AutoPriceService;
import com.zuihuibao.mq.service.SessionService;
import com.zuihuibao.mq.task.RenewPriceTask;
import com.zuihuibao.mq.util.HttpUtils;
import com.zuihuibao.mq.util.enums.DbConst;
import com.zuihuibao.web.dao.CarInsSystemConfigMapper;
import com.zuihuibao.web.dao.RenewAutoPriceLogMapper;
import com.zuihuibao.web.dao.UserRefundRateDimMapper;
import com.zuihuibao.web.model.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class AutoPriceServiceImpl implements AutoPriceService {

    private static final Logger logger = LoggerFactory.getLogger(AutoPriceService.class);

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private CarInsSystemConfigMapper carInsSystemConfigMapper;

    @Autowired
    private UserRefundRateDimMapper userRefundRateDimMapper;

    @Autowired
    private HttpUtils httpUtils;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RenewAutoPriceLogMapper renewAutoPriceLogMapper;

    @Override
    public void renewPrice(String renewOrderId, long userId, String province, String city) {
        String sessionId = getSessionId(userId);
        if ("".equals(sessionId)) {
            logger.warn("cannot get sessionId. renew_order_id:" + renewOrderId);
            return;
        }

        Set<String> onlineInsCompanies = getOnlineInsuranceCompanies();
        logger.info("renew_order_id " + renewOrderId + " online companies" + onlineInsCompanies);
        if (onlineInsCompanies.isEmpty()) {
            return;
        }
        Set<String> openInsCompanies = getOpenInsuranceCompanies(province, city);

        logger.info("renew_order_id " + renewOrderId + " open companies " + openInsCompanies);

        Set<String> priceCompanies = Sets.intersection(onlineInsCompanies, openInsCompanies);
        if (priceCompanies.isEmpty()) {
            return;
        }

        logger.info("renew_order_id " + renewOrderId + " price companies " + priceCompanies);

        String timestamp =  (new DateTime()).toString("yyyy-MM-dd HH:mm:ss");

        RenewPriceTask.Builder builder = new RenewPriceTask.Builder();

        builder.httpUtils(httpUtils)
                .sessionId(sessionId)
                .mapper(renewAutoPriceLogMapper)
                .userId(userId)
                .timestamp(timestamp)
                .renewOrderId(renewOrderId);

        for(String company : priceCompanies) {
            logger.info("renew_order_id " + renewOrderId + " schedule " + company);
            scheduledExecutorService.schedule(builder.company(company).build(), 0, TimeUnit.NANOSECONDS);
        }
    }

    private Set<String> getOnlineInsuranceCompanies() {
        CarInsSystemConfigExample example = new CarInsSystemConfigExample();
        example.createCriteria().andOnlineStatusEqualTo(DbConst.INS_ONLINE.value());
        List<CarInsSystemConfig> configList = carInsSystemConfigMapper.selectByExample(example);
        if (null != configList) {
            HashSet<String> insuranceCompanySet = new HashSet<>();
            configList.forEach((insSystemConfig) -> insuranceCompanySet.add(insSystemConfig.getEnName()));
            return insuranceCompanySet;
        }
        return Collections.emptySet();
    }

   private Set<String> getOpenInsuranceCompanies(String province, String city) {
       UserRefundRateDimExample example = new UserRefundRateDimExample();
       example.createCriteria().andProvinceEqualTo(province).andCityEqualTo(city);
       List<UserRefundRateDim> refundRateDimList = userRefundRateDimMapper.selectByExample(example);
       if (null != refundRateDimList) {
           HashSet<String> insuranceCompanySet = new HashSet<>();
           refundRateDimList.forEach((refundRateDim) -> insuranceCompanySet.add(refundRateDim.getInsuranceCompany()));
           return insuranceCompanySet;
       }
       return Collections.emptySet();
   }


    private String getSessionId(long userId) {
        Session session = sessionService.ensureUserSession(userId);
        if (null == session) {
            return "";
        }
        return session.getSessionId();
    }
}
