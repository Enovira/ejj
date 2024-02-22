package com.yxh.ejj.service

import com.yxh.ejj.global.ConstantStore
import com.yxh.ejj.utils.TimerManager
import com.yxh.ejj.utils.tryRead
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.Timer
import java.util.TimerTask

/**
 * @description 线程: Socket服务端
 */
class SocketChatServerThread(
    private val port: Int,
    private val listener: (Int, ByteArray?) -> Unit,
) : Thread() {
    private var serverSocket: ServerSocket? = null
    private var socket: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var timerManager: TimerManager? = null
    private var lastReceivedResponsePacketTime: Long = 0
    private var timer: Timer? = null

    override fun run() {
        try {
            serverSocket = ServerSocket(port)
            listener.invoke(ConstantStore.SOCKET_STATUS_CREATED_AND_WAITING, null)
            socket = serverSocket?.accept()
            listener.invoke(ConstantStore.SOCKET_STATUS_CONNECTED, null)
//            startHeartBeatTimer() // 开启心跳计时器
            inputStream = socket?.getInputStream()
            outputStream = socket?.getOutputStream()
            clearTimer()
            timer = Timer().apply {
                schedule(object : TimerTask() {
                    override fun run() {
                        sendMessage("这是服务端发送的信息~")
                    }
                }, 2000, 5000)
            }
            inputStream?.let { inp ->
                while (!interrupted()) {
                    while (inp.available() == 0) {
                        sleep(100)
                    }
                    var resultArray = ByteArray(0)
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (inp.tryRead(buffer).also { len = it } != -1) {
                        resultArray = mergeByteArray(resultArray, buffer, len)
                    }
                    listener.invoke(ConstantStore.SOCKET_TRANSFER_DATA, resultArray)
//                    App.instance().eventVM.socketMessage.postValue(resultArray)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 通过socket发送数据
     * @param str 字符串
     */
    fun sendMessage(str: String) {
        Thread {
            try {
                outputStream?.run {
                    val bytes = str.toByteArray(StandardCharsets.UTF_8)
                    println("socketChatThread sendMessage")
                    write(bytes)
                    flush()
                }
            } catch (e: Exception) {
                clearTimer()
                e.printStackTrace()
            }
        }.start()
    }

    private fun startHeartBeatTimer() {
        if (timerManager == null) {
            timerManager = TimerManager()
        }
        lastReceivedResponsePacketTime = System.currentTimeMillis()
        timerManager?.startTimer(0, 10000, object : TimerTask() {
            override fun run() {
                println("发送心跳包------")
                sendMessage("0")
                if (System.currentTimeMillis() - lastReceivedResponsePacketTime > 35000) {
                    timerManager?.clearTimer()
                    timerManager = null
                }
            }
        })
    }

    /**
     * 通过socket发送数据
     * @param bytes 字节数组
     */
    fun sendMessage(bytes: ByteArray) {
        Thread {
            try {
                outputStream?.run {
                    write(bytes)
                    flush()
                }
            } catch (e: Exception) {
                clearTimer()
                e.printStackTrace()
            }
        }.start()
    }

    fun disconnect() {
        println("手动结束socket连接=========================================")
        interrupt()
        inputStream?.close()
        outputStream?.flush()
        outputStream?.close()
        socket?.close()
        serverSocket?.close()
        inputStream = null
        outputStream = null
        socket = null
        serverSocket = null
        clearTimer()
        listener.invoke(ConstantStore.SOCKET_STATUS_DISCONNECT, null)
    }

    private fun mergeByteArray(firstBytes: ByteArray, secondBytes: ByteArray, len: Int): ByteArray {
        val bytes = ByteArray(firstBytes.size + len)
        System.arraycopy(firstBytes, 0, bytes, 0, firstBytes.size)
        System.arraycopy(secondBytes, firstBytes.size, bytes, 0, len)
        return bytes
    }

    private fun clearTimer() {
        timer?.cancel()
        timer = null
    }
}