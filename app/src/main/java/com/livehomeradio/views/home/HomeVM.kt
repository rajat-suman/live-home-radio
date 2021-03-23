package com.livehomeradio.views.home

import android.app.ActionBar
import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doOnTextChanged
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.livehomeradio.R
import com.livehomeradio.datastore.DataStoreUtil
import com.livehomeradio.datastore.JWT
import com.livehomeradio.models.DashBoardModel
import com.livehomeradio.models.LoginModel
import com.livehomeradio.networkcalls.ApiKeys
import com.livehomeradio.networkcalls.ApiProcessor
import com.livehomeradio.networkcalls.Repository
import com.livehomeradio.networkcalls.RetrofitApi
import com.livehomeradio.pref.PreferenceFile
import com.livehomeradio.recycleradapter.RecyclerAdapter
import com.livehomeradio.utils.ContactsUtil
import com.livehomeradio.views.BaseActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil
) : ViewModel() {
    val model by lazy { ObservableField(DashBoardModel()) }

    companion object {
        var callToken = ""
        var nexmoCallListener: BaseActivity.NexmoCallListeners? = null
    }


    fun hitDashBoard() = viewModelScope.launch {
        try {
            dataStore.readData(JWT) {
                repository.makeCall(
                    ApiKeys.DASHBOARD,
                    loader = false,
                    saveInCache = false,
                    requestProcessor = object : ApiProcessor<Response<DashBoardModel>> {
                        override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<DashBoardModel> {
                            return retrofitApi.dashBoard("Bearer $it")
                        }

                        override fun onSuccess(res: Response<DashBoardModel>) {
                            model.set(res.body())
                        }
                    })

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hitCallerToken() = viewModelScope.launch {
        try {
            dataStore.readData(JWT) {
                repository.makeCall(
                    ApiKeys.DASHBOARD,
                    loader = false,
                    saveInCache = false,
                    requestProcessor = object : ApiProcessor<Response<LoginModel>> {
                        override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<LoginModel> {
                            return retrofitApi.getCallerToken("Bearer ${it ?: ""}")
                        }

                        override fun onSuccess(res: Response<LoginModel>) {
                            callToken = res.body()?.jwt ?: ""
                            nexmoCallListener?.tokenReceived(callToken)
                        }
                    })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clickMakeCall(view: View) {
        try {
            val dialog = Dialog(view.context, android.R.style.Theme_Translucent_NoTitleBar)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.make_call)
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
            val adapter = RecyclerAdapter<ContactsUtil.ContactsModel>(R.layout.call_item)
            dialog.findViewById<RecyclerView>(R.id.rvContacts).adapter = adapter
            dialog.findViewById<ImageView>(R.id.ivCross).setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<EditText>(R.id.etSearch)
                .doOnTextChanged { text, _, _, _ ->
                    if (text.toString().trim().isNotEmpty()) {
                        val filteredList = BaseActivity.contactsList.filter {
                            it.name.startsWith(
                                text.toString().trim(),
                                ignoreCase = true
                            )
                        }
                        adapter.addItems(filteredList)
                    } else {
                        adapter.addItems(BaseActivity.contactsList)
                    }
                }
            val etPhoneNumber = dialog.findViewById<EditText>(R.id.etPhoneNumber)
            dialog.findViewById<ImageView>(R.id.btCallDial).setOnClickListener {
                if (etPhoneNumber.text.toString().trim().isNotEmpty()) {
                    makeCall(etPhoneNumber.text.toString().trim())
                    dialog.dismiss()
                }
            }

            adapter.setOnItemClick(object : RecyclerAdapter.OnItemClick {
                override fun onClick(view: View, position: Int, type: String) {
                    makeCall(adapter.items[position].number)
                    dialog.dismiss()
                }
            })

            adapter.addItems(BaseActivity.contactsList)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun makeCall(phoneNo: String) {
        nexmoCallListener?.makeCall(phoneNo)
    }

}