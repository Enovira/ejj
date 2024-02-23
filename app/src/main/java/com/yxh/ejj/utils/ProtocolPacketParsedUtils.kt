package com.yxh.ejj.utils

import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.yxh.ejj.App
import com.yxh.ejj.DeviceCommunicationJNI
import com.yxh.ejj.bean.TestResultRequest
import com.yxh.ejj.bean.TestResultRequestData
import com.yxh.ejj.bean.protocol.ProtocolForBaseInformationPacket
import com.yxh.ejj.bean.protocol.ProtocolForBaseInstrumentDataPacket
import com.yxh.ejj.bean.protocol.ProtocolForSoLibraryCommandPacket
import java.nio.charset.Charset

/**
 * 与底座和so库之间通讯报文转换类
 */
class ProtocolPacketParsedUtils {
    companion object {

        private var instrumentModel: String = "" //仪器型号
        private var instrumentBaud: Int = 9600 //仪器波特率
        private var requestCount: Int = 1 //同一个仪器第几次请求(部分仪器需多次请求)

        /**
         * 解析接收到的ByteArray
         */
        fun dealWithByteArrayReturnedByBase(byteArray: ByteArray, callback: ((Int, String) -> Unit)?) {
            try {
                val str = if (App.instance().eventVM.instrumentDataCodeIncludeChinese.value == true) {
                    String(byteArray, Charset.forName("GB2312"))
                } else {
                    String(byteArray)
                }
                if (str.startsWith("{\"body\":")) { //仪器试验数据包
                    val protocolForBaseInstrumentDataPacket: ProtocolForBaseInstrumentDataPacket = GsonUtils.fromJson(str, ProtocolForBaseInstrumentDataPacket::class.java)
                    parsePacketBySoLibrary(protocolForBaseInstrumentDataPacket, callback)
                } else if (str.startsWith("{\"devices\":")) { //仪器心跳包
//                    LogUtils.d("底座返回的心跳包:  $str")
                    println("底座返回的心跳包: $str")
                    val protocolForBaseInformationPacket: ProtocolForBaseInformationPacket = GsonUtils.fromJson(str, ProtocolForBaseInformationPacket::class.java)
                } else {
//                    LogUtils.d("无效结构包:  $str")
                    callback?.invoke(-1, "底座返回的为未知结构包，请重试")
                }
            } catch (e: Exception) {
                LogUtils.d(e)
                callback?.invoke(-1, "底座返回数据反序列化失败")
            }
        }

        /**
         * 解析底座返回的结构包，并通过so库去解析
         */
        private fun parsePacketBySoLibrary(protocolForBaseInstrumentDataPacket: ProtocolForBaseInstrumentDataPacket, callback: ((Int, String) -> Unit)?) {
            when(protocolForBaseInstrumentDataPacket.errcode) {
                0 -> {
                    protocolForBaseInstrumentDataPacket.body?.data?.payload?.let { payload ->
                        val result = DeviceCommunicationJNI.InterfaceDeviceDataAnalysis(payload, payload.length)
                        if (result == null || result == "fail" || result == "") {
                            callback?.invoke(-1, "so库解析失败")
                        } else if(result == "succeed") { //仪器需要多次通信(例如先预检，后读取)
//                            val curCount = YcApp.getInstance().eventViewModel.baseResponseCount.value?: 1
//                            YcApp.getInstance().eventViewModel.baseResponseCount.postValue(curCount+1)
                            getAcquireCommandInstrumentModel(instrumentModel, instrumentBaud, requestCount + 1)
                        } else {
                            callback?.invoke(1, result)
                        }
                    } ?: kotlin.run {
                        callback?.invoke(-1, "底座返回数据包中的payload为空")
                    }
                }
                -1 -> {
                    callback?.invoke(-1, "仪器回应超时")
                }
                -2 -> {
                    callback?.invoke(-1, "底座解析命令失败")
                }
            }
        }

        /**
         * @param baud 波特率
         * @param count 第几次请求
         * @param isLast 是否最后一次
         * */
        fun getAcquireCommandInstrumentModel(instrument: String = "JYR_40S", baud: Int = 9600, count: Int = 1, isLast: Boolean = false): String? {
            instrumentModel = instrument //保存试验仪器型号
            instrumentBaud = baud //保存试验仪器的波特率信息
            requestCount = count //保存第几次请求(部分仪器需要多次请求)
            val resultRequestData = TestResultRequestData()
            resultRequestData.deviceCode = instrument
            resultRequestData.comMode = "rs232"
            resultRequestData.baud = baud
            val request = TestResultRequest(resultRequestData, 1)
            LogUtils.d(Gson().toJson(request))
            val requestDataStr =
                DeviceCommunicationJNI.InterfaceJsonMagLoading(Gson().toJson(request), count, baud)
            if (requestDataStr == "fail" || requestDataStr == null) {
                LogUtils.d("so库无该仪器型号对应的协议")
            } else {
                LogUtils.d("so库返回的数据 $requestDataStr")
                val requestData = Gson().fromJson(requestDataStr, ProtocolForSoLibraryCommandPacket.ParamsPacket::class.java)
                requestData.data?.let { data ->
                    if (data.dataMsg != null && data.dataFormat != null) {
                        val protocolForSoLibraryCommandPacket = ProtocolForSoLibraryCommandPacket(
                            "appReq",  10001, "transmission", "", "", requestData
                        )
                        return GsonUtils.toJson(protocolForSoLibraryCommandPacket) //返回采集命令包
                    } else {
                        LogUtils.d("so库返回的命令为空")
                    }
                } ?: kotlin.run {
                    LogUtils.d("so库返回的命令为空")
                }
            }
            return "fail"
        }
    }
}