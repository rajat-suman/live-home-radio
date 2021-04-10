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
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.livehomeradio.R
import com.livehomeradio.databinding.AlertLayoutBinding
import com.livehomeradio.databinding.ProgressLayoutBinding
import com.livehomeradio.roomdb.Contacts
import com.livehomeradio.views.BaseActivity
import com.livehomeradio.views.home.HomeVM
import com.livehomeradio.views.makecall.ContactsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
var contactList: List<Contacts>? = null;

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


/**Make Call*/
 fun Context.showMakeCall() {
    try {

        val adapter = ContactsAdapter()
        getContacts(adapter)
        val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.make_call)
        val window: Window? = dialog.window
        val wlp: WindowManager.LayoutParams = window?.attributes!!
        val rvContacts: RecyclerView = dialog.findViewById(R.id.rvContacts)
        val ivCross: AppCompatImageView = dialog.findViewById(R.id.ivCross)
        val etSearch: EditText = dialog.findViewById(R.id.etSearch)
        val etPhoneNumber: EditText = dialog.findViewById(R.id.etPhoneNumber)
        val btCallDial: AppCompatImageView = dialog.findViewById(R.id.btCallDial)

        ivCross.setOnClickListener {
            dialog.dismiss()
        }

        rvContacts.adapter = adapter
        etSearch.doOnTextChanged { text, _, _, _ ->
            if (text.toString().trim().isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    BaseActivity.db.contactsDao().searchContacts("%${text.toString().trim()}%")
                        .catch { }.collect {
                            CoroutineScope(Dispatchers.Main).launch {
                                adapter.addItems(it)
                            }
                        }
                }
            } else {
                getContacts(adapter)
            }
        }
        btCallDial.setOnClickListener {
            if (etPhoneNumber.text.toString().trim().isNotEmpty()) {
                HomeVM.nexmoCallListener?.makeCall(etPhoneNumber.text.toString().trim())
                dialog.dismiss()
            }
        }
        adapter.initClick { position, view ->
            val number = adapter.getAllItems()[position].number
            HomeVM.nexmoCallListener?.makeCall(number.trim())
            dialog.dismiss()
        }

        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        window.attributes = wlp
        dialog.window?.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.show()


    } catch (e: Exception) {
        e.printStackTrace()
    }


}

private fun getContacts(adapter: ContactsAdapter) {

    if (contactList != null){
        adapter.addItems(contactList!!)
        return
    }

    CoroutineScope(Dispatchers.IO).launch {
        BaseActivity.db.contactsDao().getContacts().catch {
        }.collect {
            CoroutineScope(Dispatchers.Main).launch {
                contactList = it
                adapter.addItems(it)
            }
        }
    }
}





