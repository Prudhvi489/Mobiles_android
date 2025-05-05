package com.mobivault.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobivault.R
import com.mobivault.core.BaseVMBindingActivity
import com.mobivault.databinding.ActivityLoginBinding
import com.mobivault.databinding.ForgotPasswordBinding
import com.mobivault.ui.mobiledetails.MobilesListActivity
import com.mobivault.ui.viewmodel.LoginViewModel
import com.mobivault.utils.AppMethods
import com.mobivault.utils.AppStrings
import com.mobivault.utils.SessionManager
import com.mobivault.utils.dialogs.DialogUtils
import com.mobivault.utils.goneView
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity :
    BaseVMBindingActivity<ActivityLoginBinding, LoginViewModel>(LoginViewModel::class.java) {
    @Inject
    lateinit var sm: SessionManager
    var hidePassword: Boolean = false
    private lateinit var forgotPasswordBottomSheetDialog: BottomSheetDialog
    private lateinit var forgotBottomSheetBinding: ForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        intiUI()
        onClicks()
        setObservers()

    }

    private fun onClicks() {
        binding.commonHeader.backIconIV.setOnClickListener {
            finish()
        }
        binding.forgotPasswordTV.setOnClickListener {
            forgotPasswordBottomSheetDialog.show()
        }
    }

    private fun setObservers() {
        viewModel.validations.observe(this) {
            if (it.first) {
                loginApiCall()

            } else {
                AppMethods.showToast(
                    this,
                    AppMethods.returnMessage(it.second, this),
                    AppStrings.SnackbarStatus.error
                )

            }
        }
        viewModel.loginResponse.observe(this){
            if(it?.status==AppStrings.Constants.success){
                AppMethods.showToast(this,it.message,AppStrings.SnackbarStatus.success)
                it.data.let {
                    sm.saveData(AppStrings.SessionValues.accessToken,it?.token)
                }
                sm.createUserLoginSession()
                navToActivity()
                finishAffinity()
            }
        }
        viewModel.forgotPasswordValidations.observe(this) {
            if (it.first) {
                navToVerifyOtpActivity()
             } else {
//                AppMethods.showToast(this,AppMethods.returnMessage(it.second,this),AppStrings.SnackbarStatus.error)
                //need to show the alert here because the bottomsheet wil overlay the snackbar
                DialogUtils.alertDialog(this, null, AppMethods.returnMessage(it.second, this))


            }
        }
    }

    private fun loginApiCall() {
        var jsonObject=JSONObject()
        jsonObject.put(AppStrings.InputData.email,viewModel.email.value?.trim())
        jsonObject.put(AppStrings.InputData.password,viewModel.passsword.value?.trim())
         viewModel.loginApi(jsonObject)
    }

    private fun intiUI() {
        binding.commonHeader.backIconIV.goneView()
        binding.commonHeader.titleTV.text = getString(R.string.login)
        showHidePassword()
        initialiseForgotPasswordBottomSheet()
    }

    private fun initialiseForgotPasswordBottomSheet() {
        forgotPasswordBottomSheetDialog = BottomSheetDialog(this)
        forgotBottomSheetBinding = ForgotPasswordBinding.inflate(LayoutInflater.from(this))
        forgotBottomSheetBinding.viewModel = viewModel
        forgotBottomSheetBinding.lifecycleOwner = this
        forgotPasswordBottomSheetDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        forgotPasswordBottomSheetDialog?.setContentView(forgotBottomSheetBinding.root)
        forgotPasswordBottomSheetDialog?.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        setValuesToForgotPasswordBottomSheet()
        forgotPasswordBottomSheetClicks()
        forgotPasswordBottomSheetDialog?.setCancelable(false)
        forgotPasswordBottomSheetDialog?.setCanceledOnTouchOutside(false)

    }

    private fun forgotPasswordBottomSheetClicks() {
        forgotBottomSheetBinding.commonHeader.backIconIV.setOnClickListener {
            forgotPasswordBottomSheetDialog.dismiss()
        }
    }

    private fun setValuesToForgotPasswordBottomSheet() {
        forgotBottomSheetBinding.commonHeader.titleTV.text = getString(R.string.forgot_password)
    }

  /*  @SuppressLint("ClickableViewAccessibility")
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
    }*/
  @SuppressLint("ClickableViewAccessibility")
  private fun showHidePassword() {
      binding.passwordET.setOnTouchListener { v, event ->
          if (event.action == MotionEvent.ACTION_UP) {
              val drawableEnd = binding.passwordET.compoundDrawablesRelative[2] // End drawable
              if (drawableEnd != null) {
                  val drawableWidth = drawableEnd.bounds.width()
                  val touchableAreaStart = binding.passwordET.width - binding.passwordET.paddingEnd - drawableWidth

                  if (event.x >= touchableAreaStart) {
                      // Toggle password visibility
                      hidePassword = !hidePassword
                      AppMethods.showhidePwd(this, binding.passwordET, hidePassword)
                      return@setOnTouchListener true
                  }
              }
          }
          false
      }
  }


    fun navToActivity() {
        startActivity(Intent(this, MobilesListActivity::class.java))

    }

    fun navToVerifyOtpActivity() {
        startActivity(Intent(this, VerifyOtpActivity::class.java))

    }

    override fun getPersistentView(): ActivityLoginBinding {

        return ActivityLoginBinding.inflate(layoutInflater)
    }
}