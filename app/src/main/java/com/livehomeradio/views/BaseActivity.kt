package com.livehomeradio.views

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.livehomeradio.R
import com.livehomeradio.databinding.ActivityMainBinding
import com.livehomeradio.recycleradapter.AbstractModel
import com.livehomeradio.roomdb.AppDatabase
import com.livehomeradio.utils.ContactsUtil.getContactList
import com.livehomeradio.utils.showMakeCall
import com.livehomeradio.utils.showNegativeAlerter
import com.livehomeradio.views.callbottomsheet.CallBottomSheet
import com.livehomeradio.views.home.HomeVM
import com.nexmo.client.*
import com.nexmo.client.request_listener.NexmoApiError
import com.nexmo.client.request_listener.NexmoRequestListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


@AndroidEntryPoint
class BaseActivity : AppCompatActivity() {
    companion object {
        var callList = ArrayList<CallModel>()
        lateinit var contextIs: WeakReference<Context>
        lateinit var db: AppDatabase
    }

    private val notification: Uri? =
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

    private var ringtone: Ringtone? = null

    private var vibrator: Vibrator? = null

    private val mainVM: MainVM by viewModels()

    private var client: NexmoClient? = null

    private var callBottomSheet = CallBottomSheet {
        ringtone?.stop()
        if (it == null) {
            showMakeCall()
            return@CallBottomSheet
        }

        when (it.status) {
            "accept" -> {
                answerCall(it.adapterPosition)
            }
            "reject" -> {
                hangUpCall(it.adapterPosition)
            }
        }
    }

    private var onTokenReceived = object : NexmoCallListeners {
        override fun tokenReceived(token: String) {
            checkPermissions()
        }

        override fun makeCall(phoneNo: String) {
            makeNexmoCall(phoneNo)
        }

        override fun onLogout() {
            client?.logout()
            client?.removeIncomingCallListeners()
            client?.removeNewConversationListener()
            client = null
        }
    }

    override fun onResume() {
        super.onResume()
        addIncomingListener()
    }

    override fun onPause() {
        super.onPause()
        client?.removeIncomingCallListeners()
    }

    override fun onStop() {
        super.onStop()
        client?.removeIncomingCallListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        client?.removeIncomingCallListeners()
    }

    override fun onStart() {
        super.onStart()
        contextIs = WeakReference(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getInstance(this)
        ringtone = RingtoneManager.getRingtone(this, notification)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        HomeVM.nexmoCallListener = onTokenReceived
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainVM.navController = findNavController(R.id.fragmentContainer)
        binding.viewModel = mainVM
        checkPermissions()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getContactList(contentResolver)
        }
    }

    override fun onBackPressed() {
        if (mainVM.isHome.get())
            showExit()
        else
            super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            123 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && HomeVM.callToken.isNotEmpty())
                    setupNexmo()

                readContacts()

            }

            124 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getContactList(contentResolver)
            }
        }
    }

    private fun showExit() {
        val aD = AlertDialog.Builder(this)
        aD.setTitle(getString(R.string.exit_message))
        aD.setCancelable(false)
        aD.setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->
            dialogInterface.cancel()
            dialogInterface.dismiss()
            finishAffinity()
        }
        aD.setNegativeButton(getString(R.string.cancel)) { dialogInterface, i ->
            dialogInterface.cancel()
            dialogInterface.dismiss()
        }
        aD.create()
        aD.show()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                123
            )
        } else {
            if (HomeVM.callToken.isNotEmpty())
                setupNexmo()
        }
    }

    private fun readContacts() {
        try {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    124
                )
            } else {
                getContactList(contentResolver)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Nexmo

    private fun setupNexmo() {
        try {
            client = NexmoClient.Builder().useFirstIceCandidate(true).build(this)

            //Connection Listener
            client?.setConnectionListener { connectionStatus, _ ->
                runOnUiThread {
                    Toast.makeText(this, connectionStatus.name, Toast.LENGTH_LONG).show()
                }
            }
            client?.login(HomeVM.callToken)

            //Incoming Calls
            addIncomingListener()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addIncomingListener() {
        try {
            client?.addIncomingCallListener {
                addCallListener(it)
                if (callList.isEmpty()) {
                    /**
                     * Play Ringtone*/
                    ringtone?.play()
                } else {
                    /**
                     * Play Vibration*/

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(
                            VibrationEffect.createOneShot(
                                500,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    } else {
                        //deprecated in API 26
                        vibrator?.vibrate(500)
                    }
                }

                addNewCall(it, "status", false)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun answerCall(position: Int) {
        val ctx = contextIs.get()!!
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        callList[position].nexmoCall.answer(object : NexmoRequestListener<NexmoCall> {
            override fun onError(p0: NexmoApiError) {
                Log.e("onError", "onError==${p0.message}")

            }

            override fun onSuccess(p0: NexmoCall?) {
                Log.e("onSuccessAnswer", "onSuccess")
                if (ActivityCompat.checkSelfPermission(
                        ctx as Activity,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                callList[position].status = "accepted"
                callList[position].nexmoCall.mute(false)
                callList[position].nexmoCall.conversation.enableMedia()
                callBottomSheet.updateCalls(callList)
            }
        })

    }

    private fun hangUpCall(adapterPosition: Int) {
        try {
            callList[adapterPosition].nexmoCall.hangup(object : NexmoRequestListener<NexmoCall> {
                override fun onError(p0: NexmoApiError) {
                    Log.e("onErrorEnd", "onError${p0.message}")
                    if (p0.message.equals("{\"description\":\"The request failed because the current state of the conversation is INACTIVE. Only GET and DELETE actions are allowed.\",\"code\":\"conversation:error:not-allowed\"}") || p0.message.equals(
                            "{\"description\":\"Conversation does not exist, or you do not have access.\",\"code\":\"conversation:error:not-found\"}"
                        )
                    ) {
                        callList.removeAt(adapterPosition)
                        callBottomSheet.updateCalls(callList)
                        if (callList.isEmpty()) {
                            callBottomSheet.dismiss()
                        }
                    }
                }

                override fun onSuccess(p0: NexmoCall?) {
                    Log.e("onSuccessEnd", "onSuccess")
                    callList.removeAt(adapterPosition)
                    callBottomSheet.updateCalls(callList)

                    if (callList.isEmpty()) {
                        callBottomSheet.dismiss()
                    }

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
            showNegativeAlerter(e.message ?: "")
        }

    }

    private fun makeNexmoCall(phoneNo: String) {
        val callListener = object : NexmoRequestListener<NexmoCall> {
            override fun onSuccess(nexmoCall: NexmoCall?) {
                Log.d("makeNexmoCall", "Call started: ${nexmoCall?.myCallMember?.callStatus}")

                if (nexmoCall != null) {
                    addCallListener(nexmoCall)
                    addNewCall(nexmoCall, "accepted", isMakeCall = true)
                }
            }

            override fun onError(apiError: NexmoApiError) {
                Toast.makeText(
                    this@BaseActivity,
                    "Error: Unable to start a call ${apiError.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("TAG", "Error: Unable to start a call ${apiError.message}")
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this@BaseActivity,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        client?.call(phoneNo, NexmoCallHandler.SERVER, callListener)
    }

    private fun addNewCall(it: NexmoCall?, s: String, isMakeCall: Boolean) {
        try {
            if (it != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val callNumber = if (isMakeCall)
                        it.myCallMember.channel.to?.data ?: ""
                    else
                        it.myCallMember.channel.from?.data ?: ""


                    db.contactsDao().isContactExist(callNumber)
                        .catch { }.collect { bool ->
                            if (bool) {
                                val contact =
                                    db.contactsDao()
                                        .getContact(callNumber)
                                callList.add(
                                    CallModel(
                                        it,
                                        status = s,
                                        name = contact.name,
                                        number = callNumber
                                    )
                                )

                            } else {
                                callList.add(
                                    CallModel(
                                        it,
                                        status = s,
                                        name = "Unknown",
                                        number = callNumber
                                    )
                                )
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                if (!callBottomSheet.isAdded)
                                    callBottomSheet.show(supportFragmentManager, "")
                                callBottomSheet.updateCalls(callList)
                            }
                        }

                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addCallListener(it: NexmoCall?) {
        try {

            it?.addCallEventListener(object : NexmoCallEventListener {
                override fun onDTMF(p0: String?, p1: NexmoCallMember?) {

                }

                override fun onMemberStatusUpdated(
                    p0: NexmoCallMemberStatus?,
                    p1: NexmoCallMember?
                ) {

                    Log.e("dnjvfjkf", "==============$p0")
                    if (p0 == NexmoCallMemberStatus.REJECTED || p0 == NexmoCallMemberStatus.FAILED || p0 == NexmoCallMemberStatus.BUSY || p0 == NexmoCallMemberStatus.TIMEOUT || p0 == NexmoCallMemberStatus.CANCELLED || p0 == NexmoCallMemberStatus.COMPLETED) {
                        var call: CallModel? = null
                        callList.forEach { callIs ->
                            if (callIs.number == it.myCallMember.channel.from?.data ?: "" || callIs.number == it.myCallMember.channel.to?.data ?: "") {
                                call = callIs
                                return@forEach
                            }
                        }
                        if (call != null)
                            callList.remove(call)

                        callBottomSheet.updateCalls(callList)
                        if (callList.isEmpty()) {
                            callBottomSheet.dismiss()
                        }
                    }
                }

                override fun onMuteChanged(p0: NexmoMediaActionState?, p1: NexmoCallMember?) {

                }

                override fun onEarmuffChanged(
                    p0: NexmoMediaActionState?,
                    p1: NexmoCallMember?
                ) {

                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface NexmoCallListeners {
        fun tokenReceived(token: String)
        fun makeCall(phoneNo: String)
        fun onLogout()
    }

    data class CallModel(
        var nexmoCall: NexmoCall,
        var status: String = "",
        var name: String = "",
        var number: String = ""
    ) : AbstractModel()

}