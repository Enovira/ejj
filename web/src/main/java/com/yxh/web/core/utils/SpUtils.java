package com.yxh.web.core.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {
    private static final String SP_NAME = "Config";

    // 声明SharedPreferences对象
    private SharedPreferences sp;
    // 声明SharedPreferences编辑器，用于用于修改SharedReferences对象中值的接口
    private SharedPreferences.Editor editor;
    // 声明单例
    private volatile static SpUtils mInstance = null;

    /**
     * 构造方法
     */
    private SpUtils() { }

    /**
     * 实现单例，使用双重锁
     * @return
     */
    public static SpUtils getInstance() {
        if (mInstance == null) {
            synchronized (SpUtils.class) {
                if (mInstance == null) {
                    mInstance = new SpUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化SharedPreferences
     * MODE_PRIVATE：只限于本应用读写
     * MODE_WORLD_READABLE:支持其他应用读，但是不能写
     * MODE_WORLD_WRITEABLE:其他应用可以写
     */
    @SuppressLint("CommitPrefEdits")
    public void initSp(Context mContext) {
        sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 写入int值
     * @param key
     * @param values
     */
    public void putInt(String key, int values) {
        editor.putInt(key, values);
        editor.commit();
    }

    /**
     * 获取int值
     * @param key
     * @param defValues
     * @return
     */
    public int getInt(String key, int defValues) {
        return sp.getInt(key, defValues);
    }

    /**
     * 写入String值
     * @param key
     * @param values
     */
    public void putString(String key, String values) {
        editor.putString(key, values);
        editor.commit();
    }

    /**
     * 获取String值
     * @param key
     * @param defValues
     * @return
     */
    public String getString(String key, String defValues) {
        return sp.getString(key, defValues);
    }

    /**
     * 写入Boolean值
     * @param key
     * @param values
     */
    public void putBoolean(String key, boolean values) {
        editor.putBoolean(key, values);
        editor.commit();
    }

    /**
     * 获取boolean值
     * @param key
     * @param defValues
     * @return
     */
    public boolean getBoolean(String key, boolean defValues) {
        return sp.getBoolean(key, defValues);
    }

    /**
     * 删除指定Key
     * @param key
     */
    public void deleteKey(String key) {
        editor.remove(key);
        editor.commit();
    }

}
