package com.yxh.ejj.global

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yxh.ejj.bean.SocketConfig

class EventViewModel: ViewModel() {
    var socketMessage = MutableLiveData<ByteArray>()
    var socketConfig = MutableLiveData<SocketConfig>()
    val bluetoothEvent = BluetoothEvent()

    class BluetoothEvent: ViewModel() {
        val foundedDevice by lazy { MutableLiveData<BluetoothDevice>() }
        val bondedDeviceList by lazy { MutableLiveData<Set<BluetoothDevice>>() }
        val connectState by lazy { MutableLiveData<Int>() }
        val discoveryDeviceEvent by lazy { MutableLiveData<Boolean>() }
    }
}