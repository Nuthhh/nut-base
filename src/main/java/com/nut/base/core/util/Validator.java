package com.nut.base.core.util;

import java.util.List;
import java.util.Map;

/**
 * @Auther: han jianguo
 * @Date: 2019/10/12 15:43
 * @Description:
 **/
public class Validator {

    public static boolean isNull(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNotNull(List list) {
        return !isNull(list);
    }

    public static <T> boolean isNull(T... array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isNotNull(T... array) {
        return !isNull(array);
    }

    public static boolean isNull(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotNull(Map map) {
        return !isNull(map);
    }
}
