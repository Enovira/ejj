package com.yxh.ejj.global

import com.google.gson.Gson

private val gson: Gson by lazy { Gson() }

/**
 * 快速转Json字符串
 */
fun Any.toJson(): String {
    return try {
        gson.toJson(this)
    } catch (e: Exception) {
        e.printStackTrace()
        "this object cannot cast to json string"
    }
}

/**
 * 输出日志(快速定位)
 */
fun log(msg: String) {
    try {
        //通过JDK自带的StackTraceElement类获取调用信息栈，用于最后输出
        //此处Thread.currentThread().getStackTrace()获取的是StackTraceElement数组，里面保存的信息是各个调用信息
        val stackTraceElements = Thread.currentThread().stackTrace
        val index = 3
        if (stackTraceElements.isNotEmpty() && stackTraceElements.size >= index) {
            stackTraceElements[index].let {
                println(
                    StringBuilder().append("(").append(it.fileName)
                        .append(":").append(it.lineNumber)
                        .append(")#").append(it.methodName)
                        .append(": ").append(msg)
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}