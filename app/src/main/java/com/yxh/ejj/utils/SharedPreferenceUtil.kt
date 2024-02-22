package com.yxh.ejj.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.yxh.ejj.App

class SharedPreferenceUtil() {

    private val name = "ejj"
    private val mode = Context.MODE_PRIVATE
    private var editor: Editor? = null
    private var sharedPreferences: SharedPreferences? = null

    init {
        sharedPreferences = App.instance().getContext().getSharedPreferences(name, mode)
        editor = sharedPreferences?.edit()
    }

    companion object {
        private var _instance: SharedPreferenceUtil? = null
        fun getInstance(): SharedPreferenceUtil {
            if (_instance == null) {
                synchronized(this) {
                    if (_instance == null) {
                        _instance = SharedPreferenceUtil()
                    }
                }
            }
            return _instance!!
        }
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences?.getInt(key, defaultValue) ?: defaultValue
    }

    fun putInt(key: String, value: Int): Boolean {
        editor?.putInt(key, value)
        return editor?.commit() ?: false
    }
}