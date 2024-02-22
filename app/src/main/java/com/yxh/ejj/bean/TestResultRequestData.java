package com.yxh.ejj.bean;

import java.io.Serializable;

public
/**
 * @Description  {具体做什么}
 * @Author wushan
 * @Date 2021/7/19 16:53
 */

class TestResultRequestData implements Serializable {

    private String deviceCode;

    private String comMode;

    private int baud;

    private String testMode;


    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getComMode() {
        return comMode;
    }

    public void setComMode(String comMode) {
        this.comMode = comMode;
    }

    public int getBaud() {
        return baud;
    }

    public void setBaud(int baud) {
        this.baud = baud;
    }

    public String getTestMode() {
        return testMode;
    }

    public void setTestMode(String testMode) {
        this.testMode = testMode;
    }

    @Override
    public String toString() {
        return "TestResultRequestData{" +
                "deviceCode='" + deviceCode + '\'' +
                ", comMode='" + comMode + '\'' +
                ", baud=" + baud +
                ", testMode='" + testMode + '\'' +
                '}';
    }
}
