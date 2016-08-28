package com.zuihuibao.mq.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import static com.alibaba.fastjson.util.TypeUtils.castToBigDecimal;
import static com.alibaba.fastjson.util.TypeUtils.castToBoolean;
import static com.alibaba.fastjson.util.TypeUtils.castToByte;
import static com.alibaba.fastjson.util.TypeUtils.castToBytes;
import static com.alibaba.fastjson.util.TypeUtils.castToDate;
import static com.alibaba.fastjson.util.TypeUtils.castToDouble;
import static com.alibaba.fastjson.util.TypeUtils.castToFloat;
import static com.alibaba.fastjson.util.TypeUtils.castToInt;
import static com.alibaba.fastjson.util.TypeUtils.castToLong;
import static com.alibaba.fastjson.util.TypeUtils.castToShort;
import static com.alibaba.fastjson.util.TypeUtils.castToSqlDate;
import static com.alibaba.fastjson.util.TypeUtils.castToTimestamp;

/**
 * Created by millions on 16/8/20.
 * 帮助解析JSONObject
 */
public class JsonParseHelper {
    public static Optional<JSONObject> getJSONObject(JSONObject jsonObject, String key) {
        return Optional.ofNullable(jsonObject.getJSONObject(key));
    }

    public static Optional<JSONArray> getJSONArray(JSONObject jsonObject, String key) {
        return Optional.ofNullable(jsonObject.getJSONArray(key));
    }

    public static <T> Optional<T> getObject(JSONObject jsonObject, String key, Class<T> clazz) {
        return Optional.ofNullable(jsonObject.getObject(key, clazz));
    }

    public static Optional<Boolean> getBoolean(JSONObject jsonObject, String key) {

        return Optional.of(castToBoolean(jsonObject.getBoolean(key)));
    }

    public static Optional<byte[]> getBytes(JSONObject jsonObject, String key) {
        return Optional.of(castToBytes(jsonObject.getBytes(key)));
    }

    public static Optional<Byte> getByte(JSONObject jsonObject, String key) {

        return Optional.of(castToByte(jsonObject.getByte(key)));
    }

    public static Optional<Short> getShort(JSONObject jsonObject, String key) {

        return Optional.of(castToShort(jsonObject.getShort(key)));
    }

    public static Optional<Integer> getInteger(JSONObject jsonObject, String key) {

        return Optional.ofNullable(castToInt(jsonObject.getInteger(key)));
    }

    public static Optional<Long> getLong(JSONObject jsonObject, String key) {

        return Optional.ofNullable(castToLong(jsonObject.getLong(key)));
    }

    public static Optional<Float> getFloat(JSONObject jsonObject, String key) {

        return Optional.of(castToFloat(jsonObject.getFloat(key)));
    }

    public static Optional<Double> getDouble(JSONObject jsonObject, String key) {

        return Optional.of(castToDouble(jsonObject.getDouble(key)));
    }

    public static Optional<BigDecimal> getBigDecimal(JSONObject jsonObject, String key) {

        return Optional.of(castToBigDecimal(jsonObject.getBigDecimal(key)));
    }

    public static Optional<String> getString(JSONObject jsonObject, String key) {
        Object value = jsonObject.get(key);
        String valueString = null;
        if (null != value) {
            valueString = value.toString();
        }
        return Optional.ofNullable(valueString);
    }

    public static Optional<Date> getDate(JSONObject jsonObject, String key) {

        return Optional.ofNullable(castToDate(jsonObject.getDate(key)));
    }

    public static java.util.Optional<java.sql.Date> getSqlDate(JSONObject jsonObject, String key) {

        return java.util.Optional.of(castToSqlDate(jsonObject.getSqlDate(key)));
    }

    public static Optional<Timestamp> getTimestamp(JSONObject jsonObject, String key) {

        return Optional.of(castToTimestamp(jsonObject.getTimestamp(key)));
    }
}
