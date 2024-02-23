package com.yxh.ejj.bean.protocol

/**
 * 通信协议 -- 底座返回的仪器试验数据结构包
 */
class ProtocolForBaseInstrumentDataPacket(
    var mid: String?,
    var errcode: Int?,
    var body: BodyPacket?,
    var msgType: String?
) {

    inner class BodyPacket(
        var state: String?,
        var data: BaseResultPayload?,
    )

    inner class BaseResultPayload(
        /** 试验结果载荷，仪器报文存放位置 */
        var payload: String?,
    )
}