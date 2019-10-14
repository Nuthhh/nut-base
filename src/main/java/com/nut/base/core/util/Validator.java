package com.nut.base.core.util;

import java.util.List;

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
}
