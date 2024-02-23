package com.yxh.ejj.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.yxh.ejj.App
import com.yxh.ejj.DeviceCommunicationJNI
import com.yxh.ejj.R
import com.yxh.ejj.bean.protocol.ProtocolForSoLibraryCommandPacket
import com.yxh.ejj.bean.RequestMessagePacketFromDeviceSo
import com.yxh.ejj.bean.TestResultRequest
import com.yxh.ejj.bean.TestResultRequestData
import com.yxh.ejj.bean.WebSocketRequestPacket
import com.yxh.ejj.bean.WebSocketResponsePacket
import com.yxh.ejj.global.BluetoothSocketClientThread
import com.yxh.ejj.global.ConstantStore
import com.yxh.ejj.mvvm.view.MainActivity
import com.yxh.ejj.receiver.BluetoothBroadcastReceiver
import com.yxh.ejj.utils.BluetoothUtil
import com.yxh.ejj.utils.CustomNetworkUtil
import com.yxh.ejj.utils.NRCodeUtil
import com.yxh.ejj.utils.ProtocolPacketParsedUtils

class CustomService : Service() {

    private val receiver: BluetoothBroadcastReceiver by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { BluetoothBroadcastReceiver() }
    private var webSocketServerThread: WebSocketServerThread? = null
    private val channelId = "com.yxh.ejj.channelId"
    private val channelName = "com.yxh.ejj.channelName"
    private val notificationId = 76
    private var bluetoothSocketClientThread: BluetoothSocketClientThread? = null
    private var bluetoothUtil: BluetoothUtil? = null
    private val notificationManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        getSystemService(
            NotificationManager::class.java
        )
    }

    private val bleCallback = object: (Int, String?) -> Unit {
        override fun invoke(p1: Int, p2: String?) {
            if (p1 == 2) {
                customHandler.sendMessage(customHandler.obtainMessage(p1, p2))
            } else {
                customHandler.sendEmptyMessage(p1)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startWebSocketThread()
        startCustomForegroundService()
        registerBluetoothReceiver()
        bluetoothUtil = BluetoothUtil.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { it ->
//        launch3rdAppResult(seq = it.getStringExtra("seq") ?: "")
            when (it.action) {
                ConstantStore.COMMAND_STOP_SERVICE -> {
                    stopSelf()
                }
                ConstantStore.COMMAND_START_SCAN_BLE -> {
//                    BleUtil.startScan(scanCallback)
                    if (bluetoothUtil?.isDiscovering() == true) {
                        Toast.makeText(this, "正在搜索蓝牙中……", Toast.LENGTH_SHORT).show()
                        App.instance().eventVM.bluetoothEvent.discoveryDeviceEvent.postValue(false)
                    } else {
                        bluetoothUtil?.startDiscovery()
                        App.instance().eventVM.bluetoothEvent.discoveryDeviceEvent.postValue(true)
                    }
                }
                ConstantStore.COMMAND_STOP_SCAN_BLE -> {
//                    BleUtil.stopScan(scanCallback)
                    bluetoothUtil?.stopDiscovery()
                }
                ConstantStore.COMMAND_DISCONNECT_BLE -> {
//                    BleUtil.disconnect()
                    bluetoothSocketClientThread?.disconnect()
                }
                ConstantStore.COMMAND_BONDED_DEVICE_LIST -> {
                    App.instance().eventVM.bluetoothEvent.bondedDeviceList.postValue(bluetoothUtil?.getBondedDeviceList())
                }
                ConstantStore.COMMAND_SEND_MESSAGE -> {
//                    webSocketServerThread?.sendMessage(WebSocketData("这是服务端发送的消息！", "123"))
                    bluetoothSocketClientThread?.sendMessage(randomString(20))
                }
                ConstantStore.COMMAND_SEND_ACQUIRE_COMMAND -> {
                    ProtocolPacketParsedUtils.getAcquireCommandInstrumentModel()?.let { command ->
                        if (command == "fail") {
                            webSocketServerThread?.sendErrorMessage("从so库获取采集命令失败")
                        } else {
//                        BleUtil.sendData(command)
                            bluetoothSocketClientThread?.sendMessage(command)
                        }
                    }
                }
                ConstantStore.COMMAND_CONNECT_DEVICE -> {
                    if (bluetoothSocketClientThread?.isConnect() == true) {
                        Toast.makeText(this, "已建立连接，请先断开先前的连接", Toast.LENGTH_SHORT).show()
                    } else {
                        it.getStringExtra(ConstantStore.BLUETOOTH_DEVICE_MAC)?.let { address ->
                            bluetoothUtil?.getRemoteDevice(address)?.let { device ->
//                                bluetoothSocketClientThread?.disconnect()
                                bluetoothSocketClientThread = BluetoothSocketClientThread(device, bleCallback)
                                bluetoothSocketClientThread?.start()
                            }
                        }
                    }
                }
                else -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopWebSocketThread()
        unregisterReceiver(receiver)
        bluetoothSocketClientThread?.disconnect()
        notificationManager.cancel(notificationId)
    }

    /**
     * 启动webSocket服务端
     */
    private fun startWebSocketThread() {
        stopWebSocketThread()
        webSocketServerThread = WebSocketServerThread {
            println("接收到WebSocket发送过来的数据: $it")
            if (bluetoothSocketClientThread == null || bluetoothSocketClientThread?.isConnect() == false) {
                webSocketServerThread?.sendErrorMessage("未建立蓝牙连接，请先连接")
                return@WebSocketServerThread
            }
            try {
                Gson().fromJson(it, WebSocketResponsePacket::class.java)?.let { response ->
                    WebSocketRequestPacket.setInstrument(response.instrument)
                    WebSocketRequestPacket.setInstrumentInfo(it)
                    response.instrument.let { instrumentModel ->
                        ProtocolPacketParsedUtils.getAcquireCommandInstrumentModel(instrumentModel, baud = response.baud)?.let { requireDataCommand ->
                            if (requireDataCommand == "fail") {
                                webSocketServerThread?.sendErrorMessage("从so库获取采集命令失败")
                                return@WebSocketServerThread
                            }
                            WebSocketRequestPacket.setInstrumentCommandStr(requireDataCommand)
                            if (response.connectType == "0") { //表示连接方式为蓝牙
                                //符合国网报文规约的将直接发送采集指令，不发送外层数据包
                                val requestData = Gson().fromJson(requireDataCommand, RequestMessagePacketFromDeviceSo::class.java)
                                bluetoothSocketClientThread?.sendMessage(NRCodeUtil.toBytes(requestData.data.dataMsg))
                            } else {
                                // 将so返回的包含采集命令的数据包发送至底座(包含波特率、数据为、停止位、采集命令等信息)
                                bluetoothSocketClientThread?.sendMessage(requireDataCommand)
                            }
//                            BleUtil.sendData(requireDataCommand)
                        } ?: kotlin.run {
                            webSocketServerThread?.sendErrorMessage("从so库获取采集命令失败")
                        }
                    } ?: kotlin.run {
                        webSocketServerThread?.sendErrorMessage("仪器型号为空")
                    }
                }
            } catch (e: ClassCastException) {
                webSocketServerThread?.sendErrorMessage("传输数据格式出错")
                e.printStackTrace()
            } catch (e: Exception) {
                webSocketServerThread?.sendErrorMessage("内部错误")
                e.printStackTrace()
            }
        }
        webSocketServerThread?.start()
    }

    /**
     * 关闭webSocket服务端
     */
    private fun stopWebSocketThread() {
        webSocketServerThread?.disconnect()
        webSocketServerThread = null
    }

    /**
     * 启动前端通知，保持前台服务运行
     */
    private fun startCustomForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_settings)
            .setContentTitle("e基建前台服务")
            .setContentText("服务正在运行中…………")
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true) //打开程序后图标消失
            .setOngoing(true).apply {
                val i1 = Intent(this@CustomService, MainActivity::class.java)
                val p1 = PendingIntent.getActivity(
                    this@CustomService, 201, i1, PendingIntent.FLAG_IMMUTABLE)
                setContentIntent(p1)
            }.build()
        startForeground(notificationId, notification)
    }

    /**
     * 跳转到i国网
     */
    private fun launch3rdAppResult(port: Int = ConstantStore.port, seq: String) {
        val ipAddress = CustomNetworkUtil.instance.getIpAddress()
        if (ipAddress == null) {
            Toast.makeText(this, "无法获取ip地址，请检查网络连接", Toast.LENGTH_SHORT).show()
        }
        ipAddress?.let {
            println("$it:$port && seq=$seq")
            val i1 = Intent()
            i1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i1.data =
                Uri.parse("wxworklocal://jsapi/requst3rdapp_result?errcode=0&seq=$seq&data={\"port\":\"$port\"}")
            if (i1.resolveActivity(packageManager) != null) {
                startActivity(i1)
            } else {
                Toast.makeText(this, "未找到符合的目标程序", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerBluetoothReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        registerReceiver(receiver, intentFilter)
    }

    private fun randomString(size: Int): String {
        val str = "0123456789abcdef"
        val sb = StringBuilder()
        for (i in 0 until size) {
            sb.append(str[(Math.random() * 15).toInt()])
        }
        return sb.toString()
    }

    inner class CustomHandler(looper: Looper, callback: Callback): Handler(looper, callback)

    private val customHandler: CustomHandler = CustomHandler(Looper.getMainLooper()) {
        when(it.what) {
            0 -> {
                Toast.makeText(this@CustomService, "正在连接中……", Toast.LENGTH_SHORT).show()
                App.instance().eventVM.bluetoothEvent.connectState.postValue(0)
            }
            1 -> {
                Toast.makeText(this@CustomService, "连接成功", Toast.LENGTH_SHORT).show()
                App.instance().eventVM.bluetoothEvent.connectState.postValue(1)
            }
            -1 -> {
                Toast.makeText(this@CustomService, "未连接", Toast.LENGTH_SHORT).show()
                App.instance().eventVM.bluetoothEvent.connectState.postValue(-1)
            }
            2 -> {
                println("最终结果: ${it.obj}")
                WebSocketRequestPacket.setResult(it.obj.toString())
                webSocketServerThread?.sendSuccessMessage()
            }
        }
        true
    }

}