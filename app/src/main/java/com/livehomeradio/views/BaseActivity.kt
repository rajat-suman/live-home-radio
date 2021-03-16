package com.livehomeradio.views

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.livehomeradio.R
import com.livehomeradio.databinding.ActivityMainBinding
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
class BaseActivity : AppCompatActivity() {
    private val mainVM: MainVM by viewModels()
    companion object{
       lateinit var contextIs:WeakReference<Context>
    }

    override fun onStart() {
        super.onStart()
        contextIs= WeakReference(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainVM.navController = findNavController(R.id.fragmentContainer)
        binding.viewModel = mainVM
        checkUpdate()
    }


    private fun checkUpdate() {
        val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.UPDATE_AVAILABLE
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        1234
                    )
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1234 -> {
                val TAG = "null"
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Log.d(TAG, "" + "Result Ok")
                        //  handle user's approval }
                    }
                    Activity.RESULT_CANCELED -> {
                        Log.d(TAG, "" + "Result Cancelled")
                        //  handle user's rejection  }
                    }
                    ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                        //if you want to request the update again just call checkUpdate()
                        Log.d(TAG, "" + "Update Failure")
                        //  handle update failure
                    }
                }
            }
        }
    }


    override fun onBackPressed() {
        if (mainVM.isHome.get())
            showExit()
        else
            super.onBackPressed()
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

}