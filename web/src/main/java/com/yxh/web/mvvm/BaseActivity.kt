package com.yxh.web.mvvm

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yxh.web.core.utils.AndroidBug5497Workaround
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VM: ViewModel, BD: ViewDataBinding>: AppCompatActivity() {

    private var _binding: BD? = null
    val binding: BD get() = _binding!!
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidBug5497Workaround.assistActivity(this);
        _binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.lifecycleOwner = this
        viewModel = createViewModel()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
        initView()
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun initView()

    private fun createViewModel(): VM {
        return ViewModelProvider(this)[getJvmClazz(this)]
    }

    @Suppress("UNCHECKED_CAST")
    private fun <VM> getJvmClazz(clazz: Any): VM {
        return (clazz::class.java.genericSuperclass as ParameterizedType).actualTypeArguments[0] as VM
    }

    override fun onResume() {
        hideSystemUi()
        super.onResume()
    }

    private fun hideSystemUi() {

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}