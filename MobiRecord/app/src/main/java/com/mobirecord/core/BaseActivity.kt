package com.mobirecord.core

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.provider.ContactsContract
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.*
 import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.mobirecord.R
import com.mobirecord.app.MobiRecord
import com.mobirecord.repository.Repository
import com.mobirecord.utils.CLog
import com.mobirecord.utils.SessionManager


import javax.inject.Inject


open class BaseActivity : AppCompatActivity() {
    @Inject
    lateinit var repository: Repository
     val TAG = "BaseActivity"
    private var progressDialog: Dialog? = null

     var sessionManager: SessionManager? = null
    private var isReceiverRegistered = false


    protected var lifeCyclesActivity: MobiRecord? = null


    companion object {
        var context: Context? = null
    }


    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private val intervalMillis: Long = 60000 // 1 minute

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        context = this
        lifeCyclesActivity = application as MobiRecord


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    }


    fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            showLoading()
        } else hideLoading()
    }

    fun onCreatefun() {
        try {
            lifeCyclesActivity = application as MobiRecord

        } catch (e: Exception) {

        }




    }



    private fun showLoading() {
        if (progressDialog == null) {
            progressDialog = Dialog(this, R.style.CustomDialog)
        } else {
            if (progressDialog?.isShowing == false) {
                progressDialog?.show()
            }
            return
        }
        val view = LayoutInflater.from(this).inflate(R.layout.app_loading_dialog, null, false)
        progressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog?.setContentView(view)
        progressDialog?.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this, android.R.color.transparent
            )
        )
        progressDialog?.setCancelable(false)
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
    }




    override fun onDestroy() {
        super.onDestroy()

        try {
            lifeCyclesActivity?.onActivityDestroyed(this)
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            CLog.e("BaseActivity", "Error in onActivityDestroyed")
        }


        // stopTracking()
    }






    override fun onResume() {
        super.onResume()
        try {
            lifeCyclesActivity?.onActivityResumed(this)
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            CLog.e("BaseActivity", "Error in onActivityResumed")
        }


    }

    override fun onPause() {
        super.onPause()

        try {
            lifeCyclesActivity?.onActivityPaused(this)
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            CLog.e("BaseActivity", "Error in onActivityResumed")
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            lifeCyclesActivity?.onActivityStopped(this)
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            CLog.e("BaseActivity", "Error in onActivityResumed")
        }

    }







}