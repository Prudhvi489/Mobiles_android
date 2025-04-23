package com.mobirecord.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobirecord.R
import com.mobirecord.core.BaseVMBindingActivity
import com.mobirecord.databinding.ActivityResetPasswordBinding
import com.mobirecord.ui.viewmodel.VerifyOtpViewModel
import com.mobirecord.utils.AppMethods
import com.mobirecord.utils.AppStrings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordActivity :
    BaseVMBindingActivity<ActivityResetPasswordBinding, VerifyOtpViewModel>(VerifyOtpViewModel::class.java) {
    var hidePassword: Boolean = false
    var hideConfirmPassword: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initUI()
        onClicks()
        setObservers()

    }

    private fun setObservers() {
        viewModel.validations.observe(this) {
            AppMethods.hideKeyboard(this, binding.passwordET)
            if (it.first) {
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            } else {
                AppMethods.showToast(
                    this,
                    AppMethods.returnMessage(it.second, this),
                    AppStrings.SnackbarStatus.error
                )
            }
        }
    }

    private fun onClicks() {
        binding.commonHeader.backIconIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initUI() {
        binding.commonHeader.titleTV.text = getString(R.string.reset_password)
        showHidePassword()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showHidePassword() {

        binding.passwordET.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd =
                    binding.passwordET.compoundDrawablesRelative[2] // Index 2 corresponds to the drawableEnd

                if (drawableEnd != null && event.rawX >= (binding.passwordET.right - drawableEnd.bounds.width())) {
                    // The drawableEnd was clicked
                    // Handle your click event here
                    hidePassword = !hidePassword
                    AppMethods.showhidePwd(this, binding.passwordET, hidePassword)
                    return@setOnTouchListener true
                }
            }
            false
        }
        binding.confirmPasswordET.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd =
                    binding.confirmPasswordET.compoundDrawablesRelative[2] // Index 2 corresponds to the drawableEnd

                if (drawableEnd != null && event.rawX >= (binding.confirmPasswordET.right - drawableEnd.bounds.width())) {
                    // The drawableEnd was clicked
                    // Handle your click event here
                    hideConfirmPassword = !hideConfirmPassword
                    AppMethods.showhidePwd(this, binding.confirmPasswordET, hideConfirmPassword)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    override fun getPersistentView(): ActivityResetPasswordBinding {
        return ActivityResetPasswordBinding.inflate(layoutInflater)
    }
}