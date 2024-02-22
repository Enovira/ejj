package com.yxh.ejj.bean

/**
 * 从So库获取通过蓝牙发送给仪器的报文
 */
data class RequestMessagePacketFromDeviceSo(
    var all: Int?,
    var cur: Int?,
    var data: RequestMessageDataFieldFromDeviceSo
)