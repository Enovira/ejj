package com.yxh.ejj.global

import java.util.UUID

object ConstantStore {

    val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // 通用UUID

    const val port = 28952 //socket服务端端口
    /** 命令 */
    const val COMMAND = "command"
    /** 命令: 启动服务 */
    const val COMMAND_START_SERVICE = "command_StartService"
    /** 命令: 停止服务 */
    const val COMMAND_STOP_SERVICE = "command_stopService"

    /** 命令: 扫描蓝牙 */
    const val COMMAND_START_SCAN_BLE = "command_startScanBle"
    /** 命令: 停止扫描蓝牙 */
    const val COMMAND_STOP_SCAN_BLE = "command_stopScanBle"
    /** 命令: 已配对设备 */
    const val COMMAND_BONDED_DEVICE_LIST = "command_bondedDevices"
    /** 命令: 连接设备 */
    const val COMMAND_CONNECT_DEVICE = "command_connectDevice"
    /** 命令: 断开ble连接 */
    const val COMMAND_DISCONNECT_BLE = "command_disconnectBle"
    /** 命令: 发送数据 */
    const val COMMAND_SEND_MESSAGE = "command_SendMessage"
    /** 命令: 发送仪器采集命令 */
    const val COMMAND_SEND_ACQUIRE_COMMAND = "command_sendAcquireCommand"
    /** 命令: 发送开启蓝牙服务端命令 */
    const val COMMAND_START_BLUETOOTH_SOCKET_SERVER = "command_startBluetoothSocketServer"

    // 蓝牙状态
    const val SOCKET_STATUS_CREATED_AND_WAITING = 1
    const val SOCKET_STATUS_CONNECTED = 2
    const val SOCKET_STATUS_DISCONNECT = -1
    const val SOCKET_TRANSFER_DATA = 99

    //蓝牙信息
    const val BLUETOOTH_DEVICE_MAC = "bluetoothDeviceMacAddress"

    const val ERROR_CODE = -1
    const val SUCCEED_CODE = 200
}