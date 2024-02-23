package com.yxh.ejj.bean.protocol

/**
 * So库返回的仪器采集命令结构包
 */
data class ProtocolForSoLibraryCommandPacket(
    var msgType: String?,
    var mid: Long?,
    var cmd: String?,
    var serviceId: String?,
    var deviceId: String?,
    var paras: ParamsPacket?,
) {

    inner class ParamsPacket(
        var channelConfig: ChannelConfigPacket?,
        var data: DataPacket?,
    )

    inner class ChannelConfigPacket(
        var comMode: String?,
        var baud: Int?,
        var stopBit: Int?,
        var dataBits: Int?,
        var checkDigit: String?,
    )

    inner class DataPacket(
        var dataMsg: String?,
        var dataFormat: String?,
        var dataSize: Int?,
        var timeout: Long?,
    )
}