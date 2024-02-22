package com.yxh.ejj.service

import com.google.gson.Gson
import com.yxh.ejj.bean.WebSocketRequestPacket
import com.yxh.ejj.global.SocketServer
import com.yxh.ejj.global.WebSocketReceivedMessageCallback


class WebSocketServerThread(callback: WebSocketReceivedMessageCallback): Thread() {

    private var webSocketServer: SocketServer? = null
    private val gson: Gson by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Gson() }

    init {
        webSocketServer = SocketServer(28952, callback)
        webSocketServer?.isReuseAddr = true
    }

    override fun run() {
        super.run()
        println("webSocketServer开启服务")
        webSocketServer?.start()
    }

    fun disconnect() {
        println("webSocketServer停止服务")
        webSocketServer?.stop()
        interrupt()
    }

    fun sendMessage(any: Any) {
        webSocketServer?.broadcast(gson.toJson(any))
    }

    fun sendSuccessMessage() {
        val webSocketRequestPacket = WebSocketRequestPacket.buildSuccessfulPacket()
        webSocketServer?.broadcast(gson.toJson(webSocketRequestPacket).also {
            println("webSocketServer发送数据: $it")
        })
    }

    fun sendErrorMessage(errorStr: String) {
        val webSocketRequestPacket = WebSocketRequestPacket(-1, errorStr)
        webSocketServer?.broadcast(gson.toJson(webSocketRequestPacket).also {
            println("webSocketServer发送数据: $it")
        })
    }
}