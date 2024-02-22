package com.yxh.ejj.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

@SuppressLint("MissingPermission")
class BluetoothUtil {

    companion object {
        private var instance: BluetoothUtil? = null
        private var adapter: BluetoothAdapter? = null

        fun getInstance(context: Context? = null): BluetoothUtil {
            return if (instance == null) {
                context?.let {
                    it.getSystemService(BluetoothManager::class.java).let { bm ->
                        adapter = bm.adapter
                    }
                } ?: kotlin.run {
                    throw IllegalArgumentException("context cannot be null")
                }
                BluetoothUtil().also { instance = it }
            } else {
                instance!!
            }
        }

        private fun checkPermission(activity: Activity): Boolean {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ActivityCompat.requestPermissions(activity,
                        arrayOf(
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION), 0)
                } else {
                    ActivityCompat.requestPermissions(activity,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION), 0)
                }
                return false
            }
            return true
        }
    }

    fun bondDevice(bluetoothDevice: BluetoothDevice) {
        bluetoothDevice.createBond()
    }

    fun startDiscovery() {
        adapter?.startDiscovery()
    }

    fun isDiscovering(): Boolean {
        return adapter?.isDiscovering == true
    }

    fun stopDiscovery() {
        adapter?.cancelDiscovery()
    }

    fun getBondedDeviceList(): Set<BluetoothDevice>? {
        adapter?.enable()
        return adapter?.bondedDevices
    }

    fun getRemoteDevice(address: String): BluetoothDevice? {
        return adapter?.getRemoteDevice(address)
    }
}