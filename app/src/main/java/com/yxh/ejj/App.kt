package com.yxh.ejj

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.blankj.utilcode.util.LogUtils
import com.yxh.ejj.global.EventViewModel
import java.io.File
import java.lang.ref.WeakReference

class App : Application(), ViewModelStoreOwner {

    private lateinit var mViewModelStore: ViewModelStore
    lateinit var eventVM: EventViewModel
    private val path = Environment.getExternalStorageDirectory().path + "/demo_log" + File.separator

    companion object {
        private lateinit var instance: App
        private lateinit var context: WeakReference<Context>
        fun instance(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = WeakReference(applicationContext)
        mViewModelStore = ViewModelStore()
        eventVM = ViewModelProvider(this)[EventViewModel::class.java]
        LogUtils.getConfig().isLogSwitch = true
        LogUtils.getConfig().isLog2FileSwitch = true
        LogUtils.getConfig().dir = path
    }

    fun getContext(): Context {
        return context.get()!!
    }

    override val viewModelStore: ViewModelStore
        get() = mViewModelStore
}