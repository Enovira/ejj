package com.yxh.ejj.mvvm.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.yxh.ejj.global.ConstantStore
import com.yxh.ejj.service.CustomService
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

/**
 * 通过i国网启动的页面
 */
class AuxiliaryActivity: Activity(), EasyPermissions.PermissionCallbacks {

    private var seq: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.data?.let { uri ->
            uri.queryParameterNames.forEach {
                println(uri.getQueryParameter(it))
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            EasyPermissions.requestPermissions(PermissionRequest.Builder(this, 200,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ).build())
        } else {
            EasyPermissions.requestPermissions(PermissionRequest.Builder(this, 200,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ).build())
        }

        seq = intent.data?.getQueryParameter("seq") ?: ""
        println("seq = $seq")
        startCustomServerOnForeground(seq)
        finish()
    }

    private fun startCustomServerOnForeground(seq: String?) {
        val i1 = Intent(this, CustomService::class.java)
        i1.action = ConstantStore.COMMAND_START_SERVICE
        i1.putExtra("seq", seq)
        startService(i1)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == 200) {
            println("申请权限成功回调")
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == 200) {
            println("申请权限失败回调")
        }
    }
}