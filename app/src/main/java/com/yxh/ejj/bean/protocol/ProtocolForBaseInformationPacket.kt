package com.yxh.ejj.bean.protocol

data class ProtocolForBaseInformationPacket(
    var devices: List<DevicePacket>?,
) {
    inner class DevicePacket(
        var deviceId: String?,
        var services: List<ServicePacket>?,
    )

    inner class ServicePacket(
        var eventTime: String?,
        var serviceId: String?,
        var data: DataPacket?,
    )

    inner class DataPacket(
        var temperature: Float?,
        var humidity: Float?,
        var batteryLevel: Int?,
    )
}