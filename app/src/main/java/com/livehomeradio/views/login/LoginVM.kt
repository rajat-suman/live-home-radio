package com.livehomeradio.views.login

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livehomeradio.R
import com.livehomeradio.datastore.DataStoreUtil
import com.livehomeradio.datastore.JWT
import com.livehomeradio.datastore.REMEMBER
import com.livehomeradio.models.LoginModel
import com.livehomeradio.networkcalls.ApiKeys
import com.livehomeradio.networkcalls.ApiProcessor
import com.livehomeradio.networkcalls.Repository
import com.livehomeradio.networkcalls.RetrofitApi
import com.livehomeradio.pref.PreferenceFile
import com.livehomeradio.utils.getRequestBody
import com.livehomeradio.utils.navigateWithId
import com.livehomeradio.validations.Validation
import com.livehomeradio.views.BaseActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil
) : ViewModel() {
    val email by lazy { ObservableField("") }
    val password by lazy { ObservableField("") }


    fun clickLogin(view: View) {
        val ctx = BaseActivity.contextIs.get()!!
        val validation = Validation.create(ctx).apply {
            isEmpty(email.get(), "Please enter email address.")
//            isEmailValid(email.get(), "Please enter valid email address.")
            isEmpty(password.get(), "Please enter password.")
        }

        if (validation.isValid()) {
            hitLogin(view)
        }

    }

    private fun hitLogin(view: View) = viewModelScope.launch {
        try {
            repository.makeCall(
                ApiKeys.LOGIN,
                loader = true,
                saveInCache = false,
                requestProcessor = object : ApiProcessor<Response<LoginModel>> {
                    override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<LoginModel> {
                        val json = JSONObject().apply {
                            put("username", email.get()?.trim())
                            put("password", password.get()?.trim())
                        }.toString()

                        return retrofitApi.login(getRequestBody(json))

                    }

                    override fun onSuccess(res: Response<LoginModel>) {
                        dataStore.saveData(JWT, res.body()?.jwt ?: "")
                        dataStore.saveData(REMEMBER, true)
                        view.navigateWithId(R.id.action_login_to_home2, null)
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clickForgot(view: View) {
        view.navigateWithId(R.id.action_login_to_forgotPassword, null)
    }

}