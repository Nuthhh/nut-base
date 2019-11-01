package com.nut.base.core.result;

/**
 * @Auther: han jianguo
 * @Date: 2019/11/1 10:25
 * @Description:
 **/
public class ResultCode {

    private Integer code;
    private String msg;

    public ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ReturnResult setObject(Object data) {
        ReturnResult returnResult = new ReturnResult();
        returnResult.setCode(this.code);
        returnResult.setMsg(this.msg);
        returnResult.setData(data);
        return returnResult;
    }

    public ReturnResult setEmpty() {
        return setObject(new Object());
    }
}


