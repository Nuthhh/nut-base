package com.nut.base.core.common;

/**
 * @Auther: han jianguo
 * @Date: 2019/10/12 14:55
 * @Description: 定义返回值常量
 **/
public interface Constants {

    // ------------ 常用返回值 --------------------
    Integer SUCCESS                                = 0; // 操作成功
    Integer DATA_EMPTY                             = -1; // 数据为空
    Integer DATA_FORMAT_ERROR                      = -2; // 数据格式不正确
    Integer EXCEPTION                              = -3; // 操作失败，发生异常

    // ------------ 时间常量 ----------------------
    Integer TIME_MINUTE                            = 60; // 一分钟
    Integer TIME_HOUR                              = 3600; // 一小时
    Integer TIME_DAY                               = 3600*24; // 一天
    Integer TIME_WEEK                              = 3600*24*7; // 一周
    Integer TIME_MONTH                             = 3600*24*30; //一月
    Integer TIME_YEAR                              = 3600*24*365; // 一年

}
