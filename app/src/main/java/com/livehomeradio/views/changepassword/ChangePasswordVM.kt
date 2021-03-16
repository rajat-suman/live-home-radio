package com.livehomeradio.views.changepassword

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livehomeradio.R
import com.livehomeradio.datastore.DataStoreUtil
import com.livehomeradio.datastore.JWT
import com.livehomeradio.networkcalls.ApiKeys
import com.livehomeradio.networkcalls.ApiProcessor
import com.livehomeradio.networkcalls.Repository
import com.livehomeradio.networkcalls.RetrofitApi
import com.livehomeradio.pref.PreferenceFile
import com.livehomeradio.utils.navigateBack
import com.livehomeradio.utils.navigateWithId
import com.livehomeradio.utils.showPositiveAlerter
import com.livehomeradio.validations.Validation
import com.livehomeradio.views.BaseActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ChangePasswordVM @Inject constructor(
    private val repository: Repository,
    private val preferenceFile: PreferenceFile,
    private val dataStore: DataStoreUtil
) : ViewModel() {
    val oldPassword by lazy { ObservableField("") }
    val password by lazy { ObservableField("") }
    val confirmPassword by lazy { ObservableField("") }
    fun clickBack(view: View) {
        view.navigateBack()
    }

    fun clickSave(view: View) {
        val ctx = BaseActivity.contextIs.get()!!
        val validation = Validation.create(ctx).apply {
            isEmpty(oldPassword.get(), "Please enter old password")
            isEmpty(password.get(), "Please enter new password")
          /*  isValidPassword(
                password.get(),
                "Password must be of at least 8 characters and must contain One Upper case character, One Numeric and One Special Character."
            )*/
            isEmpty(confirmPassword.get(), "Please confirm new password.")
            areEqual(confirmPassword.get(), password.get(), "Passwords does not match.")
        }

        if (validation.isValid()) {
            hitChangePassword(view)
        }

    }

    private fun hitChangePassword(view: View) = viewModelScope.launch {
        try {
            val ctx = BaseActivity.contextIs.get()!!
            dataStore.readData(JWT) {
                repository.makeCall(
                    ApiKeys.CHANGE_PASSWORD,
                    loader = true,
                    saveInCache = false,
                    requestProcessor = object : ApiProcessor<Response<Any>> {
                        override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<Any> {
                            return retrofitApi.changePassword(
                                "Bearer $it",
                                oldPassword = oldPassword.get()?.trim(),
                                password = password.get()?.trim()
                            )
                        }

                        override fun onSuccess(res: Response<Any>) {
                            ctx.showPositiveAlerter("Password Changed Successfully!")
                            view.navigateBack()
                        }

                    })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}