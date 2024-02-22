package com.yxh.ejj.mvvm.vm

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.yxh.ejj.R
import com.yxh.ejj.global.ConstantStore
import com.yxh.ejj.mvvm.view.AuxiliaryActivity
import com.yxh.ejj.service.CustomService

class MainActivityVM(private val application: Application) : AndroidViewModel(application), View.OnClickListener {

    val booleanMutableLiveData = MutableLiveData<Boolean>()

    override fun onClick(v: View?) {
        v?.let {
            when(it.id) {
                R.id.toChatActivity -> {
//                    val p0 = Intent(application, ChatActivity::class.java)
//                    p0.data = (Uri.parse("ejj://com.yxh.ejj?seq=null"))
//                    application.startActivity(p0)
                }
                R.id.toSerialPortActivity -> {
                    val intent = Intent(application, AuxiliaryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    application.startActivity(intent)
                }
                R.id.startService -> {
                    startServiceWithCommand()
                }
                R.id.discoveryBle -> {
                    startServiceWithCommand(ConstantStore.COMMAND_START_SCAN_BLE)
                }
                R.id.stopScan -> {
//                    BleUtil.stopScan(scanCallback)
                    startServiceWithCommand(ConstantStore.COMMAND_STOP_SCAN_BLE)
                }
                R.id.sendMessage -> {
//                    BleUtil.sendData(randomString(200))
                    startServiceWithCommand(ConstantStore.COMMAND_SEND_MESSAGE)
                }
                R.id.disconnect -> {
//                    viewModelScope.launch {
//                        BleUtil.sendStopSignal(1)
//                        BleUtil.disconnect()
//                    }
                    startServiceWithCommand(ConstantStore.COMMAND_DISCONNECT_BLE)
                }
                R.id.stopService -> {
//                    val intent = Intent(application, CustomService::class.java)
//                    application.stopService(intent)
                    startServiceWithCommand(ConstantStore.COMMAND_STOP_SERVICE)
                }
                R.id.sendAcquireCommand -> {
                    startServiceWithCommand(ConstantStore.COMMAND_SEND_ACQUIRE_COMMAND)
                }
                R.id.bonded -> {
                    startServiceWithCommand(ConstantStore.COMMAND_BONDED_DEVICE_LIST)
                }

                else -> {}
            }
        }
    }

    private fun randomString(size: Int): String {
        val str = "0123456789abcdef"
        val sb = StringBuilder()
        for (i in 0 until size) {
            sb.append(str[(Math.random() * 15).toInt()])
        }
        return sb.toString()
    }

    private fun startServiceWithCommand(command: String? = null) {
        application.startService(Intent(application, CustomService::class.java).apply {
            action = command
        })
    }
}