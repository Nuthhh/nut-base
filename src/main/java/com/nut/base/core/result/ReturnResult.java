package com.nut.base.core.result;

/**
 * @Auther: han jianguo
 * @Date: 2019/11/1 10:25
 * @Description:
 **/
public class ReturnResult {

    private Integer code;
    private String msg;
    private Object data;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
