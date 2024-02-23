package com.yxh.ejj.global

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.yxh.ejj.DeviceCommunicationJNI
import com.yxh.ejj.bean.WebSocketRequestPacket
import com.yxh.ejj.utils.NRCodeUtil
import com.yxh.ejj.utils.ProtocolPacketParsedUtils
import com.yxh.ejj.utils.tryRead
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

@SuppressLint("MissingPermission")
class BluetoothSocketClientThread(
    private val bluetoothDevice: BluetoothDevice,
    private val listener: (Int, String?) -> Unit,
) : Thread() {
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    override fun run() {
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(ConstantStore.uuid)
            listener.invoke(0, null)
            bluetoothSocket?.connect()
            listener.invoke(1, null)
            inputStream = bluetoothSocket?.inputStream
            outputStream = bluetoothSocket?.outputStream
            while (!interrupted()) {
                inputStream?.let { inStream ->
                    while (inStream.available() == 0) {
                        sleep(100)
                    }
                    var resultArray = ByteArray(0) // 最终字节数组
                    val bytes = ByteArray(1024) // 分段读取
                    var count: Int
                    while (inStream.tryRead(bytes).also { count = it } > 0) {
                        val subArray = ByteArray(resultArray.size + count)
                        System.arraycopy(resultArray, 0, subArray, 0, resultArray.size)
                        System.arraycopy(bytes, 0, subArray, resultArray.size, count)
                        resultArray = subArray
                    }
                    WebSocketRequestPacket.setSourceResult(NRCodeUtil.bytes2HexString(resultArray))
                    println("接收到仪器返回的报文: ${NRCodeUtil.bytes2HexString(resultArray)}")
                    println("接收到仪器返回的报文: ${String(resultArray)}")
                    ProtocolPacketParsedUtils.dealWithByteArrayReturnedByBase(resultArray, object : (Int, String) -> Unit {
                        override fun invoke(p1: Int, p2: String) {
                            listener.invoke(2, p2)
                        }
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            listener.invoke(-1, null)
        }
    }

    fun disconnect() {
        interrupt()
        inputStream?.close()
        outputStream?.close()
        bluetoothSocket?.close()
        inputStream = null
        outputStream = null
        bluetoothSocket = null
    }

    fun sendMessage(str: String) {
        try {
            outputStream?.write(str.toByteArray(Charset.defaultCharset()))
            outputStream?.flush()
            println("发送数据: $str")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMessage(bytes: ByteArray) {
        try {
            outputStream?.write(bytes)
            outputStream?.flush()
            println("发送数据: ${String(bytes)}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isConnect(): Boolean {
        return bluetoothSocket?.isConnected == true
    }

    /**
     * 将底座返回的数据交给so库解析
     */
    private fun analyzeResponse(dataForLib: String) {
        val dataContent = dataForLib.substring(0, dataForLib.lastIndexOf("{"))
        val dataSize =
            dataForLib.substring(dataForLib.lastIndexOf(":") + 1, dataForLib.length - 1).toInt()
        val result = DeviceCommunicationJNI.InterfaceDeviceDataAnalysis(dataContent, dataSize)
        listener.invoke(2, result)
    }

    /**
     * 将国网报文交给so库解析
     */
    private fun analyzeResponse2(dataForLib: String) {
        val result = DeviceCommunicationJNI.InterfaceDeviceDataAnalysis(dataForLib, dataForLib.length)
        listener.invoke(2, result)
    }


}