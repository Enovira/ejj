package com.yxh.web.core

import android.app.Application
import com.yxh.web.core.utils.SpUtils

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        SpUtils.getInstance().initSp(this)
    }
}