package com.yxh.ejj.bean

class WebSocketRequestPacket(
    var code: Int, //请求状态码 200为成功，其余失败
    var message: String?,
    var data: WebSocketDataPacket? = null,
//    var data: MutableList<WebSocketRequestData?> = arrayListOf(),
) {
    companion object {

        class WebSocketDataPacket(
            //仪器型号
            var instrument: String?,
            //结果
            var result: WebSocketData?,
            //日志
            var logs: List<WebSocketData?>,
        )

        class WebSocketData(
            /** 时间戳 */
            var timestamp: Long,
            var content: String,
            var type: String,
        )

        private const val TYPE_LOG = "logs"
        private const val TYPE_RESULT = "result"

        private var instrument: String? = null //仪器型号
        private var instrumentInfo: WebSocketData? = null //当前仪器数据
        private var instrumentCommandStr: WebSocketData? = null //仪器采集命令
        private var sourceResult: WebSocketData? = null //原始试验数据
        private var result: WebSocketData? = null //解析后的试验数据

        fun setInstrument(str: String) {
            instrument = str
        }

        fun setInstrumentInfo(str: String) {
            instrumentInfo = buildWebSocketData(content = str, type = "FromPanelToSo")
        }

        fun setInstrumentCommandStr(str: String) {
            instrumentCommandStr = buildWebSocketData(content = str, type = "FromSoToBase")
        }

        fun setSourceResult(str: String) {
            sourceResult = buildWebSocketData(content = str, type = "FromBaseToSo")
        }

        fun setResult(str: String) {
            result = buildWebSocketData(content = str, type = "FromSoToPanel")
        }

        private fun buildWebSocketData(
            timestamp: Long = System.currentTimeMillis(),
            content: String,
            type: String,
        ): WebSocketData {
            return WebSocketData(timestamp, content, type)
        }

        fun buildSuccessfulPacket(): WebSocketRequestPacket {
            return WebSocketRequestPacket(
                200, "成功", WebSocketDataPacket(
                    instrument,
                    result,
                    listOf(instrumentInfo,
                        instrumentCommandStr,
                        sourceResult))
            ).also {
                instrument = null
                instrumentInfo = null
                instrumentCommandStr = null
                sourceResult = null
                result = null
            }
        }
    }
}