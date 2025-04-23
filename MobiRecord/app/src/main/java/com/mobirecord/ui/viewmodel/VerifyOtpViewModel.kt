package com.mobirecord.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.mobirecord.core.BaseViewModel
import com.mobirecord.repository.Repository
import com.mobirecord.utils.AppMethods
import com.mobirecord.utils.AppStrings
import com.mobirecord.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class VerifyOtpViewModel @Inject constructor(var app: Application, var repo: Repository, var sm: SessionManager):BaseViewModel(app) {
    var email =MutableLiveData<String>()
    var passsword =MutableLiveData<String>()
    var confirmPassword =MutableLiveData<String>()
    var otp =MutableLiveData<String>()
     var validations = MediatorLiveData<Pair<Boolean, String>>()
      init {
        initValues()
    }

    private fun initValues() {
         email.value=""
        passsword.value=""
        confirmPassword.value=""
        otp.value=""
     }
    fun validations(){
       if( !AppMethods.isValidEmail(
               email.value.toString().trim())){
           validations.value= Pair(false, AppStrings.ValidationTypes.invalidEmail)
       } else if (!AppMethods.isValidPassword(
               passsword.value.toString().trim())) {
           validations.value = Pair(false, AppStrings.ValidationTypes.invalidPassword)

       } else if (!AppMethods.isValidPassword(
               confirmPassword.value.toString().trim())) {
           validations.value = Pair(false, AppStrings.ValidationTypes.invalidConfirmPassword)

       } else if (!AppMethods.arePasswordsMatched(passsword.value.toString().trim(),
               confirmPassword.value.toString().trim())) {
           validations.value = Pair(false, AppStrings.ValidationTypes.passwordIncorrect)

       } else {
           validations.value = Pair(true, AppStrings.ValidationTypes.success)

       }

    }



}