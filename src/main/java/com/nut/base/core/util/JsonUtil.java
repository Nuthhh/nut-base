package com.nut.base.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @Auther: han jianguo
 * @Date: 2019/10/12 15:09
 * @Description:
 **/
public class JsonUtil {

    public static String toString(Object object) {
        return JSON.toJSONString(object);
    }

    public static <T> T toBean(String str, Class<T> clz) {
        return JSONObject.parseObject(str, clz);
    }

}
