package com.livehomeradio.utils


import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.livehomeradio.R
import com.livehomeradio.databinding.AlertLayoutBinding
import com.livehomeradio.databinding.ProgressLayoutBinding

//ErrorAlert

fun Context.alert(message: String) {
    try {
        val builder = AlertDialog.Builder(this)
        val layoutView = AlertLayoutBinding.inflate(LayoutInflater.from(this), null, false)
        builder.setCancelable(false)
        builder.setView(layoutView.root)
        val dialog = builder.create()

        layoutView.tvMessage.text = message
        layoutView.tvOkButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

//Loader

private var customDialog: AlertDialog? = null

fun Context.showProgress() {
    hideProgress()
    val customAlertBuilder = AlertDialog.Builder(this)
    val customAlertView =
        ProgressLayoutBinding.inflate(LayoutInflater.from(this), null, false)
    customAlertBuilder.setView(customAlertView.root)
    customAlertBuilder.setCancelable(false)
    customDialog = customAlertBuilder.create()

    customDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    customDialog!!.show()
}

fun hideProgress() {
    if (customDialog != null && customDialog?.isShowing!!) {
        customDialog?.dismiss()
    }
}

/** Welcome*/

fun Context.showWelcome(onDismiss: (Boolean) -> Unit) {
    try {

        val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.welcome)
        val window: Window? = dialog.window
        val wlp: WindowManager.LayoutParams = window?.attributes!!

        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        window.attributes = wlp
        dialog.window?.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            onDismiss(true)
            dialog.dismiss()
        }, 2000)


    } catch (e: Exception) {
        e.printStackTrace()
    }
}



