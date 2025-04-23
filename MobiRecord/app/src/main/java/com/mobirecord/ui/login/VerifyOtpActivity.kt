package com.mobirecord.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobirecord.R
import com.mobirecord.core.BaseVMBindingActivity
import com.mobirecord.databinding.ActivityVerifyOtpBinding
import com.mobirecord.ui.mobiledetails.MobilesListActivity
import com.mobirecord.ui.viewmodel.VerifyOtpViewModel
import com.mobirecord.utils.AppMethods
import com.mobirecord.utils.AppStrings
import com.mobirecord.utils.CLog
import com.mobirecord.utils.setOnSafeClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyOtpActivity :
    BaseVMBindingActivity<ActivityVerifyOtpBinding, VerifyOtpViewModel>(VerifyOtpViewModel::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel=viewModel
        binding.lifecycleOwner= this
        initUI()
        onClicks()
        setObservers()

    }

    private fun setObservers() {


    }

    private fun onClicks() {
        binding.commonHeader.backIconIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.verifyBTN.setOnClickListener {
            //api call  and redreciton based on cases from forgot password or from login
            // if login then home else reset password
            CLog.e(TAG, "onClicks:api calllll ", )
            hideKeyboard()
            startActivity(Intent(this, ResetPasswordActivity::class.java))
            finishAffinity()
        }


    }


    private fun initUI() {
        binding.commonHeader.titleTV.text = getString(R.string.verify_otp)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)  // Get current focus or create a new view
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    override fun getPersistentView(): ActivityVerifyOtpBinding {
        return ActivityVerifyOtpBinding.inflate(layoutInflater)
    }
}