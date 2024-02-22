package com.yxh.ejj.bean

/**
 * 从So库获取通过蓝牙发送给仪器的报文（RequestMessageResultFromDeviceSo）的数据域内容
 */
data class RequestMessageDataFieldFromDeviceSo(
    var comMode: String?,
    var baud: Int?,
    var stopBit: Int?,
    var dataBits: Int?,
    var checkDigit: String?,
    var dataMsg: String,
    var dataFormat: String?,
    var dataSize: Int,
    var code: Int
)