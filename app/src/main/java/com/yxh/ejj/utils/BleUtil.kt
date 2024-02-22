package com.yxh.ejj.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.content.Context
import com.yxh.ejj.DeviceCommunicationJNI
import com.yxh.ejj.global.log
import java.nio.charset.StandardCharsets
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

@SuppressLint("MissingPermission")
object BleUtil {
    private val adapter = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothGatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var notifyCharacteristic: BluetoothGattCharacteristic? = null
    private var timer: Timer? = null
    private var heartBeatTimer: Timer? = null
    private var lastReceivedDataTime: Long= 0
    private var bytes: ByteArray = ByteArray(0)

    private const val targetBluetoothDeviceMac: String = "C4:DE:E2:20:98:8E"

    /** 目标ble的服务uuid */
    private const val mServiceUUID: String = "0000a002-0000-1000-8000-00805f9b34fb"
    /** 目标ble的服务uuid */
    private const val mWriteCharacteristicUUID: String = "0000c304-0000-1000-8000-00805f9b34fb"

    private const val mNotifyCharacteristicUUID: String = "0000c305-0000-1000-8000-00805f9b34fb"

    private const val mDescriptorUUID: String = "00002902-0000-1000-8000-00805f9b34fb"

    private var lastSendMessageTime: Long = 0

    private var callback: ((Int, String?) -> Unit)? = null

    private var bleDevice: BluetoothDevice? = null

    private var reConn = false

    private var thread: Thread? = null

    private var heartBeatByte: ByteArray = "$$$".toByteArray(StandardCharsets.UTF_8)

    private var reConnTimer: Timer? = null

    fun setBleCallback(callback: (Int, String?) -> Unit) {
        this.callback = callback
    }

    /** 开始扫描蓝牙 */
    fun startScan(scanCallback:ScanCallback) {
        println("开始扫描蓝牙")
        adapter.bluetoothLeScanner.startScan(scanCallback)
    }

    /** 停止扫描蓝牙 */
    fun stopScan(scanCallback:ScanCallback) {
        println("停止扫描蓝牙")
        adapter.bluetoothLeScanner.stopScan(scanCallback)
    }

    /** 断开连接 */
    fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    /** 建立Ble连接 */
    fun connectGatt(context: Context ,address: String) {
        val device = adapter.getRemoteDevice(address)
//        println("尝试建立Ble连接 name: ${device.name}, address: ${device.address}")
//        device.connectGatt(context, true, bleGattCallback)
        bleDevice = device
        thread?.interrupt()
        if (reConn) {
            thread = Thread {
                try {
                    Thread.sleep(8000)
                    device.connectGatt(context, false, bleGattCallback)
                    println("尝试建立Ble连接 name: ${device.name}, address: ${device.address}")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            thread?.start()
        } else {
            device.connectGatt(context, false, bleGattCallback)
            println("尝试建立Ble连接 name: ${device.name}, address: ${device.address}")
        }
    }

    /**
     * ble状态回调
     */
    private val bleGattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                callback?.invoke(1, null)
                println("gatt 连接成功")
                bluetoothGatt = gatt
                gatt?.discoverServices() //开始扫描services
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                callback?.invoke(-1, null)
                println("gatt 断开连接")
                gatt?.close()
                notifyCharacteristic?.let {
                    gatt?.setCharacteristicNotification(it, false)
                }
                bluetoothGatt = null
                stopHeartBeatTimer()
                startReConnTimer()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            gatt?.let { bluetoothGatt ->
                val service = bluetoothGatt.getService(UUID.fromString(mServiceUUID))
                service?.let { bluetoothGattService ->
                    writeCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(mWriteCharacteristicUUID))
                    val characteristic = bluetoothGattService.getCharacteristic(UUID.fromString(mNotifyCharacteristicUUID))
                    notifyCharacteristic = characteristic
                    characteristic?.let { bluetoothGattCharacteristic ->
                        val descriptor = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(mDescriptorUUID))
                        val result = bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true)

                        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT

                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        bluetoothGatt.writeDescriptor(descriptor)
//                            bluetoothGatt.requestMtu(512) //设置蓝牙分包长度,默认20字节，最大512字节
                        println("设置通知监听结果: $result")

                        startHeartBeatTimer()
                    }
                }
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic, value: ByteArray, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, value, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                println("onCharacteristicRead读取到数据: ${characteristic.value}")
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (!characteristic?.value.contentEquals(heartBeatByte)) {
                    println(characteristic?.value?.let { "onCharacteristicWrite写入成功 ${characteristic.uuid} ==> ${String(it)}" })
                }
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            characteristic?.let {
                lastReceivedDataTime = System.currentTimeMillis()
                if (timer == null && it.value.size >= 20) { //首次接收数据且数据长度>=20(BLE分包长度)，开启计时器
                    bytes = it.value
                    startTimer()
                } else if (timer == null && it.value.size < 20) { //首包长度小于20(BLE分包长度)，则直接为最终结果
                    log("最终结果: ${String(it.value)}")
                } else { //拼接最终结果
                    bytes = mergeByteArray(bytes, it.value)
                }
                println("onCharacteristicChanged: ${it.uuid} ==> ${String(it.value)}")
            }
        }
    }

    private fun mergeByteArray(frontBytes: ByteArray, behindBytes: ByteArray): ByteArray {
        val bytes = ByteArray(frontBytes.size + behindBytes.size)
        System.arraycopy(frontBytes, 0, bytes, 0, frontBytes.size)
        System.arraycopy(behindBytes, 0, bytes, frontBytes.size, behindBytes.size)
        return bytes
    }



    /**
     * ble传输数据给底座
     */
    fun sendData(str:String) {
        bluetoothGatt?.let { gatt ->
            writeCharacteristic?.let { characteristic ->
                Thread {
                    while (System.currentTimeMillis() - lastSendMessageTime < 500) {
                        Thread.sleep(10)
                    }
                    characteristic.value = str.toByteArray(StandardCharsets.UTF_8)
                    gatt.writeCharacteristic(characteristic)
                    lastSendMessageTime = System.currentTimeMillis()
                }.start()
            }
        }
    }

    /**
     * ble传输数据给底座
     */
    fun sendData(bytes: ByteArray) {
        bluetoothGatt?.let { gatt ->
            writeCharacteristic?.let { characteristic ->
                Thread {
                    while (System.currentTimeMillis() - lastSendMessageTime < 500) {
                        Thread.sleep(10)
                    }
                    characteristic.value = bytes
                    gatt.writeCharacteristic(characteristic)
                    lastSendMessageTime = System.currentTimeMillis()
                }.start()
            }
        }
    }

    /**
     * ble数据传输会有分包情况(默认20字符)
     * 每个包间隔大致50ms~150ms,设置为超时300ms后完整数据包接收结束
     */
    private fun startTimer() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (System.currentTimeMillis() - lastReceivedDataTime > 500) {
                    timer?.cancel()
                    timer = null
                    log("最终结果: ${String(bytes)}")
                    val dataForLib = String(bytes)
                    //底座返回的试验结果以"0101"开头、以"}"结尾
                    if (dataForLib.startsWith("0101") && dataForLib.last() == '}') {
                        analyzeResponse(dataForLib.replaceFirst("0101", ""))
                    }
                }
            }
        }, 0, 30)
    }

    /** 开启客户端心跳包 */
    private fun startHeartBeatTimer() {
        heartBeatTimer?.cancel()
        heartBeatTimer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    sendData(heartBeatByte)
                }
            }, 3000, 3000)
        }
    }

    /** 停止客户端心跳包 */
    private fun stopHeartBeatTimer() {
        heartBeatTimer?.cancel()
        heartBeatTimer = null
    }

    /**
     * 将数据交给so库解析
     */
    private fun analyzeResponse(dataForLib: String) {
        val dataContent = dataForLib.substring(0, dataForLib.lastIndexOf("{"))
        val dataSize = dataForLib.substring(dataForLib.lastIndexOf(":") + 1, dataForLib.length - 1).toInt()
        val result = DeviceCommunicationJNI.InterfaceDeviceDataAnalysis(dataContent, dataSize)
        callback?.invoke(2, result)
    }

    private fun startReConnTimer() {
        clearReConnTimer()
        var count = 0
        reConnTimer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    count += 1
                    reConn = true
                    if (count >= 8) {
                        reConn = false
                        clearReConnTimer()
                    }
                }
            }, 0, 1000)
        }
    }

    private fun clearReConnTimer() {
        reConnTimer?.cancel()
        reConnTimer = null
    }

    fun isConnected(context: Context): Boolean {
        context.getSystemService(BluetoothManager::class.java).let { bm ->
            return bm.getConnectedDevices(BluetoothProfile.GATT).size != 0
        }
    }

    /**
     * 扫描ble监听器
     */
//    private val scanCallback = object : ScanCallback() {
//        override fun onScanResult(callbackType: Int, result: ScanResult?) {
//            super.onScanResult(callbackType, result)
//            result?.device?.address?.let {
//                println(it)
//                if (it == targetBluetoothDeviceMac) {
//                    stopScan(this)
//                    connectGatt(context, it)
//                }
//            }
//        }
//    }
}