package com.zuihuibao.mq.service;



public interface AutoPriceService {

    /**
     * 按照去年投保的 省份 城市进行续保报价
     * @param renewOrderId String
     * @param province String
     * @param city String
     */
    void renewPrice(String renewOrderId, long userId, String province, String city);
}
