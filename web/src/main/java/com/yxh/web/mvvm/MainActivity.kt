package com.yxh.web.mvvm

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.yxh.web.R
import com.yxh.web.core.utils.SpUtils
import com.yxh.web.databinding.ActivityMainBinding

class MainActivity: BaseActivity<EmptyViewModel, ActivityMainBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        binding.btn1.setOnClickListener {
            val customDialog = CustomDialog()
            customDialog.show(supportFragmentManager, "")
        }
    }

    class CustomDialog: DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.setDimAmount(0f)
            return super.onCreateDialog(savedInstanceState)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.dialog_input, container)

            view.findViewById<TextView>(R.id.title).text = "请输入链接"
            val editText = view.findViewById<EditText>(R.id.inputArea).apply {
                setText(SpUtils.getInstance().getString("url", ""))
            }
            view.findViewById<TextView>(R.id.btn1).apply {
                text = "取消"
                setOnClickListener {
                    dialog?.dismiss()
                }
            }
            view.findViewById<TextView>(R.id.btn2).apply {
                text = "确定"
                setOnClickListener {
                    dialog?.dismiss()
                    editText.text.toString().trim().let { url ->
                        SpUtils.getInstance().putString("url", url)
                        startActivity(Intent(context, WebViewActivity::class.java).apply { putExtra("url", url) })
                    }
                }
            }
            return view
        }
    }
}