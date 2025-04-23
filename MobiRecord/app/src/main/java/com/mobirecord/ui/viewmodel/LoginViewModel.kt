package com.mobirecord.ui.viewmodel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mobirecord.core.BaseViewModel
import com.mobirecord.network.ApiResult
import com.mobirecord.network.isRequestCallSuspendSuccess
import com.mobirecord.repository.Repository
import com.mobirecord.ui.login.model.LoginResponse
import com.mobirecord.utils.AppMethods
import com.mobirecord.utils.AppStrings
import com.mobirecord.utils.CLog
import com.mobirecord.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.HashMap
import javax.inject.Inject
@HiltViewModel
class LoginViewModel @Inject constructor(var app: Application, var repo: Repository, var sm: SessionManager):BaseViewModel(app) {
    var email =MutableLiveData<String>()
    var passsword =MutableLiveData<String>()
    var forgotPasswordEmail= MutableLiveData<String>()
    var validations = MediatorLiveData<Pair<Boolean, String>>()
    var forgotPasswordValidations = MediatorLiveData<Pair<Boolean, String>>()
    var errorMessage = MutableLiveData<String>("")
    private val _loginResponse: MutableLiveData<ApiResult<LoginResponse?>?> = MutableLiveData()
    val loginResponse: LiveData<ApiResult<LoginResponse?>?> = _loginResponse

    init {
        initValues()
    }

    private fun initValues() {
         email.value=""
        passsword.value=""
        forgotPasswordEmail.value=""
    }
    fun validations(){
       if( !AppMethods.isValidEmail(
               email.value.toString().trim())){
           validations.value= Pair(false, AppStrings.ValidationTypes.invalidEmail)
       }/* else if (!AppMethods.isValidPassword(
               passsword.value.toString().trim())) {
           validations.value = Pair(false, AppStrings.ValidationTypes.invalidPassword)

       } */else {
           validations.value = Pair(true, AppStrings.ValidationTypes.success)

       }

    }
    fun forgotPasswordValidations(){
        if( !AppMethods.isValidEmail(
                forgotPasswordEmail.value.toString().trim())){
            forgotPasswordValidations.value= Pair(false, AppStrings.ValidationTypes.invalidEmail)
        } else {
            forgotPasswordValidations.value = Pair(true, AppStrings.ValidationTypes.success)

        }

    }
    fun loginApi(jsonObject: JSONObject) {
        CLog.e(ContentValues.TAG, "loginApi() called with: jsonObject = $jsonObject")

        viewModelScope.launch {
            validateNetwork {
                setIsLoading(true)
                repo.login( jsonObject).isRequestCallSuspendSuccess(success = {
                    CLog.e("TAG", "login:it---->  " + it)
                    _loginResponse.value = it
                }, failure = { body, errorType, message ->
                    println("failure-body-> " + body)
                    println("failure-errorType-> " + errorType)
                    errorMessage.value=message

                })
                setIsLoading(false)
            }
        }
    }
}