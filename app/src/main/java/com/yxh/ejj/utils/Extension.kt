package com.yxh.ejj.utils

import android.view.View
import java.io.InputStream

fun View.setPreventFastClickListener(listener: PreventFastClickListener) {
    setOnClickListener(listener)
}

fun InputStream.tryRead(byteArray: ByteArray, maxLength: Int = byteArray.size, timeout: Long = 100): Int {
    val time = System.currentTimeMillis()
    while (available() < maxLength && System.currentTimeMillis() - time < timeout) {
        Thread.sleep(1)
    }
    val length = if (available() >= maxLength) maxLength else available()
    return read(byteArray, 0, length)
}