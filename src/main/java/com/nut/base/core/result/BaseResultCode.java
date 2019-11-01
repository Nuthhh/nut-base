package com.nut.base.core.result;

/**
 * @Auther: han jianguo
 * @Date: 2019/11/1 10:59
 * @Description:
 **/
public interface BaseResultCode {

    ResultCode SUCCESS = new ResultCode(0, "成功");
    ResultCode ERROR = new ResultCode(-10, "错误");
    // 1 参数错误
    // 2 用户权限错误
    // 3 接口状态

}
