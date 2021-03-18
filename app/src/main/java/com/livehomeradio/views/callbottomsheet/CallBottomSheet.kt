package com.livehomeradio.views.callbottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.Nullable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.livehomeradio.R
import com.livehomeradio.databinding.CallsSheetBinding
import com.livehomeradio.recycleradapter.RecyclerAdapter
import com.livehomeradio.views.BaseActivity

class CallBottomSheet(val valueIs: (BaseActivity.CallModel) -> Unit) :
    BottomSheetDialogFragment() {
    private var binding: CallsSheetBinding? = null
    private val adapter by lazy { RecyclerAdapter<BaseActivity.CallModel>(R.layout.call_adapter) }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        this.isCancelable = false

        binding =
            CallsSheetBinding.inflate(LayoutInflater.from(context))

        isCancelable = false
        binding?.rvFeatures?.adapter = adapter

        dialog.setContentView(binding?.root!!)
        (binding?.root?.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    fun updateCalls(callList: ArrayList<BaseActivity.CallModel>) {
        adapter.addItems(callList)
        adapter.setOnItemClick(object : RecyclerAdapter.OnItemClick {
            override fun onClick(view: View, position: Int, type: String) {
                Log.e("ssdhvbhdvhv", "====$type")
                val item = adapter.items[position].apply { status = type }
                valueIs(item)
            }
        })

    }
}
