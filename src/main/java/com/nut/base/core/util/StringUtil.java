package com.nut.base.core.util;

/**
 * @Auther: han jianguo
 * @Date: 2019/10/12 14:39
 * @Description:
 **/
public class StringUtil {

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String getNotEmptyStr(String source, String defaultStr) {
        return isNotEmpty(source) ? source : defaultStr;
    }

}
