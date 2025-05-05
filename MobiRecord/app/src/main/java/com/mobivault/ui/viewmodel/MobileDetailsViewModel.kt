package com.mobivault.ui.viewmodel

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.mobivault.core.BaseViewModel
import com.mobivault.repository.Repository
import com.mobivault.ui.addmobiles.model.MultiMediaModel
import com.mobivault.utils.AppMethods
import com.mobivault.utils.AppStrings
import com.mobivault.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class MobileDetailsViewModel @Inject constructor(var app: Application, var repo: Repository, var sm: SessionManager):
    BaseViewModel(app) {
    val multiImagesList = ArrayList<MultiMediaModel>()
     var selectedImagePath = MutableLiveData<String>()
     var mobileBuyDate = MutableLiveData<String>()
    var mobileSoldDate = MutableLiveData<String>()
    var customerName = MutableLiveData<String>()
    var mobileName = MutableLiveData<String>()
    var customerAddress = MutableLiveData<String>()
    var customerPhoneNumber = MutableLiveData<String>()
    var searchString = MutableLiveData<String>()
    var validations = MediatorLiveData<Pair<Boolean, String>>()


    init{
        initValues()
    }

    private fun initValues() {
        customerName.value=""
        customerPhoneNumber.value=""
        mobileName.value=""
        customerAddress.value=""
        mobileBuyDate.value=""
        mobileSoldDate.value=""
        selectedImagePath.value=""
        searchString.value=""
    }
    fun validations(){
        if(!AppMethods.isValidPhoneNumber(customerPhoneNumber.value.toString().trim())){
            validations.value= Pair(false, AppStrings.ValidationTypes.invalidMobileNumber)
        }else{
            validations.value=Pair(true, AppStrings.ValidationTypes.success)
        }
    }

}