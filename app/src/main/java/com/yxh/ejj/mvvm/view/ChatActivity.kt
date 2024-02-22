package com.yxh.ejj.mvvm.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.yxh.ejj.App
import com.yxh.ejj.R
import com.yxh.ejj.base.BaseActivity
import com.yxh.ejj.databinding.ActivityChatBinding
import com.yxh.ejj.service.CustomService
import com.yxh.ejj.utils.PreventFastClickListener
import com.yxh.ejj.utils.SoftKeyboardManager
import com.yxh.ejj.utils.setPreventFastClickListener
import com.yxh.ejj.mvvm.vm.EmptyViewModel

class ChatActivity : BaseActivity<EmptyViewModel, ActivityChatBinding>() {

    private lateinit var seq: String

    override fun getLayoutId(): Int {
        return R.layout.activity_chat
    }

    override fun initView() {
        seq = intent.data?.getQueryParameter("seq") ?: "null"
        binding.receivedMessageDisplay.setText("seq: $seq")

        initListener()
        initObserver()

        checkPermission()
    }

    private fun initObserver() {
        App.instance().eventVM.socketConfig.observe(this) {
            binding.socketIp.setText(it.ip)
            binding.socketPort.setText(it.port.toString())
        }
    }

    private fun initListener() {
        binding.startSocket.setPreventFastClickListener(object :
            PreventFastClickListener() {
            override fun onPreventFastClick(v: View?) {
                startService(1, seq)
            }
        })

        binding.cleanReceived.setPreventFastClickListener(object :
            PreventFastClickListener() {
            override fun onPreventFastClick(v: View?) {
                binding.receivedMessageDisplay.setText("")
            }
        })

        binding.cleanSendMessage.setPreventFastClickListener(object :
            PreventFastClickListener() {
            override fun onPreventFastClick(v: View?) {
                binding.sendMessageDisplay.setText("")
            }
        })

        binding.sendMessage.setPreventFastClickListener(object : PreventFastClickListener() {
            override fun onPreventFastClick(v: View?) {
                val message = binding.sendMessageDisplay.text.trim().toString()
                if (message.isEmpty()) {
                    Toast.makeText(this@ChatActivity, "传输内容不能为空", Toast.LENGTH_SHORT).show()
                } else {
                    startService(2, message)
                }
            }
        })

        binding.root.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                SoftKeyboardManager.instance.hideSoftKeyboard(this@ChatActivity, v)
            }
        }
    }

    /**
     * @param 1: 启动前台通知  2: 通过socket传输信息
     */
    private fun startService(command: Int, data: String) {
        val i1 = Intent(this, CustomService::class.java)
        when(command) {
            1 -> {
                i1.action = "com.yxh.ejj.startService"
                i1.putExtra("message", data)
            }
            2 -> {
                i1.action = "com.yxh.ejj.sendMessage"
                i1.putExtra("message", data)
            }
        }
        startService(i1)
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.QUERY_ALL_PACKAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.QUERY_ALL_PACKAGES), 201
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, CustomService::class.java))
    }
}