package com.zuihuibao.mq.task;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuihuibao.mq.service.SessionService;
import com.zuihuibao.mq.util.HttpUtils;
import com.zuihuibao.mq.util.JsonParseHelper;
import com.zuihuibao.web.dao.RenewAutoPriceLogMapper;
import com.zuihuibao.web.model.RenewAutoPriceLog;
import com.zuihuibao.web.model.Session;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by millions on 2016/8/28.
 * 续保报价
 */
public class RenewPriceTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RenewPriceTask.class);

    private final long userId;
    private final String company;
    private final String timestamp;
    private final String renewOrderId;
    private final HttpUtils httpUtils;
    private final RenewAutoPriceLogMapper mapper;
    private final String sessionId;

    private RenewPriceTask(Builder builder) {
        userId = builder.userId;
        company = builder.company;
        timestamp = builder.timestamp;
        renewOrderId = builder.renewOrderId;
        httpUtils = builder.httpUtils;
        mapper = builder.mapper;
        sessionId = builder.sessionId;
    }

    @Override
    public void run() {
        logger.info("running renew_order_id:" + renewOrderId + " company:" + company);
        Optional<JSONObject> priceResultOptional = renewPrice();
        if (priceResultOptional.isPresent()) {
            JSONObject priceResult = priceResultOptional.get();
            RenewAutoPriceLog renewAutoPriceLog = new RenewAutoPriceLog();

            Optional<String> orderIdOptional = JsonParseHelper.getString(priceResult, "order_id");
            if (!orderIdOptional.isPresent()) {
                logger.info("price no order_id renew_order_id:" + renewOrderId + " insurance_company:" + company);
                return;
            }
            renewAutoPriceLog.setOrderId(orderIdOptional.get());

            renewAutoPriceLog.setUserId(userId);

            renewAutoPriceLog.setRenewOrderId(renewOrderId);

            Optional<String> licenseOptional = JsonParseHelper.getString(priceResult, "license_no");
            if (licenseOptional.isPresent()) {
                renewAutoPriceLog.setLicenseNo(licenseOptional.get());
            }

            Optional<String> frameNoOptional = JsonParseHelper.getString(priceResult, "frame_no");
            if (frameNoOptional.isPresent()) {
                renewAutoPriceLog.setFrameNo(frameNoOptional.get());
            }

            Optional<String> userRefundOptional = JsonParseHelper.getString(priceResult, "user_refund");
            if (userRefundOptional.isPresent()) {
                String userRefundString = userRefundOptional.get();
                if ("".equals(userRefundString)) {
                    userRefundString = "0.00";
                }
                int userRefund = (int)(Float.parseFloat(userRefundString) * 100);
                renewAutoPriceLog.setUserRefund(userRefund);
            }

            Optional<String> totalPremiumOptional = JsonParseHelper.getString(priceResult, "total_premium");
            if (totalPremiumOptional.isPresent()) {
                int totalPremium = (int)(Float.parseFloat(totalPremiumOptional.get()) * 100);
                renewAutoPriceLog.setTotalPremium(totalPremium);
            }

            int rows =  mapper.insertSelective(renewAutoPriceLog);
            if (rows < 1) {
                logger.info("price result insert fail" + JSON.toJSONString(renewAutoPriceLog));
            }
        }
    }


    private Optional<JSONObject> renewPrice() {

        //1. 构造报价所需的参数以及cookie
        Map<String, String> cookies = Maps.newHashMap();
        cookies.put("ZHBSESSID", sessionId);
        List<NameValuePair> nameValuePairs = Lists.newArrayList();
        nameValuePairs.add(new BasicNameValuePair("order_id", renewOrderId));
        nameValuePairs.add(new BasicNameValuePair("post_time_stamp", timestamp));
        nameValuePairs.add(new BasicNameValuePair("insurance_company", company));
        String api = "http://121.43.230.71:11289/yiiapp/car-ins/renew-price";

        //2. 处理并返回结果
        ResponseEntity<String> responseEntity
                = httpUtils.postUrlencodedForm(api, String.class, nameValuePairs, cookies);
        if (null == responseEntity) {
            logger.warn("renew_order_id" + renewOrderId + " insurance_company " + company + " http request fail");
            return Optional.empty();
        }
        String responseBody = responseEntity.getBody();
        logger.info("renew_order_id" + renewOrderId + " insurance_company " + company + responseBody);
        JSONObject response;
        try {
            response = JSON.parseObject(responseBody);
        } catch (Exception e) {
            logger.info("parse json exception" + e.getMessage() + e.getCause());
            return Optional.empty();
        }

        Optional<Integer> returnCodeOptional = JsonParseHelper.getInteger(response, "return_code");
        if (!returnCodeOptional.isPresent() || 0 != returnCodeOptional.get()) {
            return Optional.empty();
        }

        return JsonParseHelper.getJSONObject(response, "data");
    }

    public static class Builder {
        private long userId;
        private String company;
        private String timestamp;
        private String renewOrderId;
        private HttpUtils httpUtils;
        private RenewAutoPriceLogMapper mapper;
        private String sessionId;

        public Builder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder renewOrderId(String renewOrderId) {
            this.renewOrderId = renewOrderId;
            return this;
        }

        public Builder httpUtils(HttpUtils httpUtils) {
            this.httpUtils = httpUtils;
            return this;
        }

        public Builder mapper(RenewAutoPriceLogMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public RenewPriceTask build() {
            return new RenewPriceTask(this);
        }
    }
}
