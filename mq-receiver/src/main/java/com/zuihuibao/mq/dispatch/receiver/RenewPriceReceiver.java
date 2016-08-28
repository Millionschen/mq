package com.zuihuibao.mq.dispatch.receiver;

import com.alibaba.fastjson.JSONObject;
import com.zuihuibao.mq.service.AutoPriceService;
import com.zuihuibao.mq.util.JsonParseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by millions on 2016/8/28.
 * 进行续保报价
 */

@Component("RenewPriceReceiver")
public class RenewPriceReceiver extends JsonObjectReceiver<RenewPriceReceiver.RenewPriceReceiverDto> {

    @Autowired
    private AutoPriceService autoPriceService;

    @Override
    protected Optional<RenewPriceReceiverDto> parse(JSONObject jsonObject) {
        Optional<JSONObject> dataOptional = JsonParseHelper.getJSONObject(jsonObject, "data");
        if (dataOptional.isPresent()) {
            JSONObject data = dataOptional.get();
            //获取user_id, renew_order_id, province, city
            Optional<Long> userId = JsonParseHelper.getLong(data, "user_id");
            Optional<String> renewOrderId = JsonParseHelper.getString(data, "renew_order_id");
            Optional<String> province = JsonParseHelper.getString(data, "province");
            Optional<String> city = JsonParseHelper.getString(data, "city");
            if (!userId.isPresent() || !renewOrderId.isPresent() || !province.isPresent() || !city.isPresent()) {
                return Optional.empty();
            }
            RenewPriceReceiverDto dto = new RenewPriceReceiverDto();
            dto.setUserId(userId.get());
            dto.setRenewOrderId(renewOrderId.get());
            dto.setProvince(province.get());
            dto.setCity(city.get());
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    protected void handle(RenewPriceReceiverDto o) {
        autoPriceService.renewPrice(o.getRenewOrderId(), o.getUserId(), o.getProvince(), o.getCity());
    }

    static class RenewPriceReceiverDto {
        private long userId;
        private String renewOrderId;
        private String province;
        private String city;

        private RenewPriceReceiverDto() {}

        void setUserId(long userId) {
            this.userId = userId;
        }

        void setRenewOrderId(String renewOrderId) {
            this.renewOrderId = renewOrderId;
        }

        void setProvince(String province) {
            this.province = province;
        }

        void setCity(String city) {
            this.city = city;
        }

        public long getUserId() {
            return userId;
        }

        public String getRenewOrderId() {
            return renewOrderId;
        }

        public String getProvince() {
            return province;
        }

        public String getCity() {
            return city;
        }
    }
}
