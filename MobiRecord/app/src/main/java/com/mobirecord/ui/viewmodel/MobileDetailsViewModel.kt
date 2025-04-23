package com.mobirecord.ui.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.mobirecord.core.BaseViewModel
import com.mobirecord.repository.Repository
import com.mobirecord.ui.addmobiles.model.MultiMediaModel
import com.mobirecord.utils.AppMethods
import com.mobirecord.utils.AppStrings
import com.mobirecord.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
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