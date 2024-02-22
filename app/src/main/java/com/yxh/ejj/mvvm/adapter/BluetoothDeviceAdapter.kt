package com.yxh.ejj.mvvm.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yxh.ejj.R


class BluetoothDeviceAdapter(layoutResId: Int = R.layout.adapter_item_bluetooth_device) :
    BaseQuickAdapter<BluetoothDevice, BaseViewHolder>(layoutResId) {
    @SuppressLint("MissingPermission")
    override fun convert(holder: BaseViewHolder, item: BluetoothDevice) {
        holder.setText(R.id.name, item.name)
            .setText(R.id.address, item.address)
    }
}