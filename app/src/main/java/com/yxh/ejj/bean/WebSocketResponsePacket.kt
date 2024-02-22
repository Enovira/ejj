package com.yxh.ejj.bean

data class WebSocketResponsePacket(
    var instrument: String, //仪器型号
    var baud: Int?, //波特率
    /**
     * 连接方式 "0": 蓝牙 "1": 底座直连 “2”: 底座-交叉线…………
     * */
    var connectType: String?,
)