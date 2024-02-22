package com.yxh.ejj.bean;

import java.io.Serializable;

public
/**
 * @Description  {查询试验结果}
 * @Author wushan
 * @Date 2021/7/19 16:51
 */

class TestResultRequest implements Serializable {


    private TestResultRequestData data;
    private int code;

    public TestResultRequest(TestResultRequestData data, int code) {
        this.data = data;
        this.code = code;
    }

    public TestResultRequestData getData() {
        return data;
    }

    public void setData(TestResultRequestData data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "TestResultRequest{" +
                "data=" + data +
                ", code='" + code + '\'' +
                '}';
    }
}
