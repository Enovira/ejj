package com.yxh.ejj.receiver

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.yxh.ejj.App
import com.yxh.ejj.global.ConstantStore
import com.yxh.ejj.service.CustomService

class BluetoothBroadcastReceiver: BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { p1 ->
            when(p1.action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    showAndPrintMessage(context, "开始搜索周边蓝牙")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    showAndPrintMessage(context, "结束搜索周边蓝牙")
                }
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    when(p1.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        BluetoothAdapter.STATE_TURNING_ON -> {
                            showAndPrintMessage(context, "手机蓝牙正在开启")
                        }
                        BluetoothAdapter.STATE_ON -> {
                            showAndPrintMessage(context, "蓝牙已开启")
                        }
                        BluetoothAdapter.STATE_OFF -> {
                            showAndPrintMessage(context, "蓝牙已关闭")
                        }
                        BluetoothAdapter.STATE_TURNING_OFF -> {
                            showAndPrintMessage(context, "手机蓝牙正在关闭")
                        }
                        else -> {}
                    }
                }
                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                    p1.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1).let {
                        if ( it == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                            showAndPrintMessage(context, "蓝牙可被搜索")
                        }
                    }
                }
                BluetoothDevice.ACTION_FOUND -> {
                    p1.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)?.let { device ->
                        device.name?.let { name ->
                            println("搜索到蓝牙设备: $name")
                            App.instance().eventVM.bluetoothEvent.foundedDevice.postValue(device)
                        }
                    }
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    when(intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)) {
                        BluetoothDevice.BOND_NONE -> {
                            showAndPrintMessage(context, "已取消与该设备的配对")
                        }
                        BluetoothDevice.BOND_BONDING -> {
                            showAndPrintMessage(context, "正在与设备配对,请等待(<10s)")
                        }
                        BluetoothDevice.BOND_BONDED -> {
                            showAndPrintMessage(context, "与设备配对成功")
                        }
                        else -> {}
                    }
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    p1.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)?.run {
                        showAndPrintMessage(context, "ACL断开连接: $name")
                        App.instance().eventVM.bluetoothEvent.connectState.postValue(-1)
                        context?.let {
                            println("发送断开连接的指令")
                            it.startService(Intent(it, CustomService::class.java).apply {
                                action = ConstantStore.COMMAND_DISCONNECT_BLE
                            })
                        }
                    }
                }
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    p1.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)?.run {
                        showAndPrintMessage(context, "ACL成功连接: $name")
                        App.instance().eventVM.bluetoothEvent.connectState.postValue(1)
                    }
                }

                else -> {
                    null
                }
            }
        }
    }

    private fun showAndPrintMessage(context: Context?, msg: String) {
        context?.let {
            Toast.makeText(it, msg, Toast.LENGTH_SHORT).show()
        }
        println(msg)
    }
}