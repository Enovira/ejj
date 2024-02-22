package com.yxh.ejj.mvvm.view

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import com.yxh.ejj.App
import com.yxh.ejj.R
import com.yxh.ejj.base.BaseActivity
import com.yxh.ejj.databinding.ActivityMainBinding
import com.yxh.ejj.global.ConstantStore
import com.yxh.ejj.mvvm.adapter.BluetoothDeviceAdapter
import com.yxh.ejj.mvvm.vm.MainActivityVM
import com.yxh.ejj.service.CustomService
import com.yxh.ejj.utils.BluetoothUtil

class MainActivity : BaseActivity<MainActivityVM, ActivityMainBinding>() {

    private val discoverDeviceList: MutableList<BluetoothDevice> = ArrayList()
    private val bondedDeviceList: MutableList<BluetoothDevice> = ArrayList()
    private lateinit var discoveryDevicesAdapter: BluetoothDeviceAdapter
    private lateinit var bondedDevicesAdapter: BluetoothDeviceAdapter
    private var bluetoothUtil: BluetoothUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val mSplashScreenView = installSplashScreen()
        super.onCreate(savedInstanceState)
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        binding.vm = viewModel

        bluetoothUtil = BluetoothUtil.getInstance(this)
        scaleScreenSize()
        initRecyclerView()
        initObserver()

        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }
            ?.also {
                Toast.makeText(this, "不支持Ble", Toast.LENGTH_SHORT).show()
                finish()
            }

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_SCAN,
            ), 200
        )
    }

    @SuppressLint("MissingPermission")
    private fun initRecyclerView() {
        binding.drawerLayout.isClickable = false
        binding.leftRV.adapter = BluetoothDeviceAdapter().also {
            discoveryDevicesAdapter = it
            discoveryDevicesAdapter.setNewInstance(discoverDeviceList)
            discoveryDevicesAdapter.setOnItemClickListener { adapter, view, position ->
                when(discoverDeviceList[position].bondState) {
                    BluetoothDevice.BOND_BONDED -> {
                        Toast.makeText(this@MainActivity, "已配对，无需重复配对", Toast.LENGTH_SHORT).show()
                    }
                    BluetoothDevice.BOND_NONE -> {
                        println("开始配对: ${discoverDeviceList[position].name}")
                        bluetoothUtil?.bondDevice(discoverDeviceList[position])
                    }
                }
            }
        }
        discoveryDevicesAdapter.setEmptyView(R.layout.view_empty)
        binding.rightRv.adapter = BluetoothDeviceAdapter().also {
            bondedDevicesAdapter = it
            bondedDevicesAdapter.setNewInstance(bondedDeviceList)
            bondedDevicesAdapter.setOnItemClickListener { adapter, view, position ->
//                println("开始连接设备")
//                bluetoothSocketClientThread?.disconnect()
//                bluetoothSocketClientThread = BluetoothSocketClientThread(bondedDeviceList[position]) { str ->
//                    println(str)
//                }
//                bluetoothSocketClientThread?.start()
                startService(Intent(this@MainActivity, CustomService::class.java).apply {
                    action = ConstantStore.COMMAND_CONNECT_DEVICE
                    putExtra(ConstantStore.BLUETOOTH_DEVICE_MAC, bondedDeviceList[position].address)
                })
            }
        }
        bondedDevicesAdapter.setEmptyView(R.layout.view_empty)
    }

    private fun initObserver() {
        App.instance().eventVM.bluetoothEvent.discoveryDeviceEvent.observe(this) {
            if (it) {
                discoverDeviceList.clear()
                discoveryDevicesAdapter.notifyDataSetChanged()
            }
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        App.instance().eventVM.bluetoothEvent.foundedDevice.observe(this) {
            discoverDeviceList.add(it)
            discoveryDevicesAdapter.notifyItemInserted(discoverDeviceList.size)
        }
        App.instance().eventVM.bluetoothEvent.bondedDeviceList.observe(this) {
            bondedDeviceList.clear()
            bondedDeviceList.addAll(it)
            bondedDevicesAdapter.notifyDataSetChanged()
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }
        App.instance().eventVM.bluetoothEvent.connectState.observe(this) {
            when(it) {
                1 -> {
                    binding.state.text = getString(R.string.connectState, "已连接")
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                }
                in -1 ..0 -> {
                    binding.state.text = getString(R.string.connectState, "未连接")
                }
            }
        }
    }

    private fun scaleScreenSize() {
        val dm = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(dm)
        val screenWidth = dm.widthPixels
        val screenHeight = dm.heightPixels
        println("屏幕分辨率 = $screenWidth*$screenHeight")
        println("dm.density = " + dm.density + "," + "dm.densityDpi = " + dm.densityDpi)
    }

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)

}