package com.mobivault.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.mobivault.R
import com.mobivault.core.BaseVMBindingActivity
import com.mobivault.databinding.ActivityVerifyOtpBinding
import com.mobivault.ui.viewmodel.VerifyOtpViewModel
import com.mobivault.utils.CLog
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