package com.yxh.ejj;

public
/**
 * @Description  {底座数据转换}
 * @Author ws
 * @Date 2022/6/28 17:19
 */

class DeviceCommunicationJNI {

   static {
      System.loadLibrary("dc");
   }

   /**
    *
    * @param jsonData  pad组好的json请求数据包
    * @return  发给底座的msg
    */
   public static native String InterfaceJsonMagLoading(String jsonData, int cnt, int baud);

   /**
    *
    * @param msg 接收底座返回回来的数据
    * @return  经过so库处理后的json数据包
    */
   public static native String InterfaceDeviceDataAnalysis(String msg, int size);

}
