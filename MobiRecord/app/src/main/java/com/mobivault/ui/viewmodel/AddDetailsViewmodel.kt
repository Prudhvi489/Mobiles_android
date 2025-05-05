package com.mobivault.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mobivault.core.BaseViewModel
import com.mobivault.network.ApiResult
import com.mobivault.network.isRequestCallSuspendSuccess
import com.mobivault.repository.Repository
import com.mobivault.ui.addmobiles.model.AwsUploadUrlResponse
import com.mobivault.ui.addmobiles.model.MultiMediaModel
import com.mobivault.ui.addmobiles.model.MultipleImagesListToBackend
import com.mobivault.ui.mobiledetails.model.Asset
import com.mobivault.ui.mobiledetails.model.GetAssetsResponse
import com.mobivault.utils.AppMethods
import com.mobivault.utils.AppStrings
import com.mobivault.utils.CLog
import com.mobivault.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddDetailsViewmodel @Inject constructor(
    var app: Application,
    var repo: Repository,
    var sm: SessionManager
) :
    BaseViewModel(app) {
    val multiImagesList: ObservableArrayList<MultiMediaModel> = ObservableArrayList()
    val multipleImagesListToBackend: ArrayList<MultipleImagesListToBackend> = ArrayList()

    val awsList: ArrayList<String> = ArrayList()
    val imageFileNameList: ArrayList<MultiMediaModel> = ArrayList()
    var deletedMediaIds = ArrayList<String>()
    var selectedImagePath = MutableLiveData<String>()
    var selectedImage: File = File("")
    var mobileModel = MutableLiveData<String>()
    var imei = MutableLiveData<String>()
    var buyerPrice = MutableLiveData<Int>()
    var sellerPrice = MutableLiveData<Int>()
    var profit = MediatorLiveData<Int>()
    var status = MutableLiveData<String>()
    var sellerAadhar = MutableLiveData<String>()
    var sellerDate = MutableLiveData<String>()
    var buyingDate = MutableLiveData<String>()
    var sellerName = MutableLiveData<String>()
    var buyerName = MutableLiveData<String>()
    var customerAddress = MutableLiveData<String>()
    var sellerPhoneNumber = MutableLiveData<String>()
    var buyerPhoneNumber = MutableLiveData<String>()
    var searchString = MutableLiveData<String>()
    var pagesize = MutableLiveData<Int>(10)
    var page = MutableLiveData<Int>(1)
    var validations = MediatorLiveData<Pair<Boolean, String>>()
    var noData = MutableLiveData<Boolean>(false)
    var filterStatusTemp= MutableLiveData<String>("all")
    var filterStatus = MutableLiveData<String>("all")

    var errorMessage = MutableLiveData<String>("")
    private val _createAssetResponse: MutableLiveData<ApiResult<Any?>?> = MutableLiveData()
    val createAssetResponse: LiveData<ApiResult<Any?>?> = _createAssetResponse

    private val _updateAssetResponse: MutableLiveData<ApiResult<Any?>?> = MutableLiveData()
    val updateAssetResponse: LiveData<ApiResult<Any?>?> = _updateAssetResponse

    private val _getAssetsResponse: MutableLiveData<ApiResult<GetAssetsResponse?>?> =
        MutableLiveData()
    val getAssetsResponse: LiveData<ApiResult<GetAssetsResponse?>?> = _getAssetsResponse

    private val _getUploadUrlsResponse: MutableLiveData<ApiResult<AwsUploadUrlResponse?>?> =
        MutableLiveData()
    val getUploadUrlsResponse: LiveData<ApiResult<AwsUploadUrlResponse?>?> = _getUploadUrlsResponse


    private val _getAssetByIdResponse: MutableLiveData<ApiResult<Asset?>?> =
        MutableLiveData()
    val getAssetByIdResponse: LiveData<ApiResult<Asset?>?> = _getAssetByIdResponse

    private val _deleteAssetResponse: MutableLiveData<ApiResult<Any?>?> = MutableLiveData()
    val deleteAssetResponse: LiveData<ApiResult<Any?>?> = _deleteAssetResponse


    init {
        initValues()
    }

    private fun initValues() {
        mobileModel.value = ""
        imei.value = ""
        status.value = ""
        sellerName.value = ""
        sellerPhoneNumber.value = ""
        buyerPhoneNumber.value = ""
        sellerAadhar.value = ""
        buyerName.value = ""
        customerAddress.value = ""
        sellerDate.value = ""
        buyingDate.value = ""
        selectedImagePath.value = ""
        searchString.value = ""
        profit.addSource(buyerPrice) { calculateProfit() }
        profit.addSource(sellerPrice) { calculateProfit() }
    }

    private fun calculateProfit() {
        val buyer = buyerPrice.value ?: 0
        val seller = sellerPrice.value ?: 0

        if (buyer > 0 && seller > 0) {
            profit.value = buyer - seller
        } else {
            profit.value = 0
        }
    }

    fun validations() {
        when {
            mobileModel.value.isNullOrEmpty() -> {
                validations.value = Pair(false, AppStrings.ValidationTypes.emptyMobileModel)
            }

            imei.value.isNullOrEmpty() -> {
                validations.value = Pair(false, AppStrings.ValidationTypes.imeiEmpty)
            }

            !imei.value.isNullOrEmpty()&& imei.value!!.length < 16 -> {
                validations.value = Pair(false, AppStrings.ValidationTypes.invalidImei)
            }
            sellerPrice.value == null -> {
                validations.value = Pair(false, AppStrings.ValidationTypes.emptySellerPrice)
            }

            status.value.isNullOrEmpty() -> {
                validations.value = Pair(false, AppStrings.ValidationTypes.emptyStatus)
            }

            sellerName.value.isNullOrEmpty() -> {
                validations.value = Pair(false, AppStrings.ValidationTypes.emptySellerName)
            }

            sellerPhoneNumber.value.isNullOrEmpty() -> {
                validations.value = Pair(false, AppStrings.ValidationTypes.emptySellerPhoneNumber)
            }

            !sellerPhoneNumber.value.isNullOrEmpty() && !AppMethods.isValidPhoneNumber(
                sellerPhoneNumber.value.toString().trim()
            ) -> {
                validations.value = Pair(false, AppStrings.ValidationTypes.invalidMobileNumber)
            }

            sellerDate.value.isNullOrBlank() -> {
                validations.value = Pair(false, AppStrings.ValidationTypes.emptySellerDate)
            }

            !buyerPhoneNumber.value.isNullOrEmpty() && !AppMethods.isValidPhoneNumber(
                buyerPhoneNumber.value.toString().trim()
            ) -> {
                validations.value = Pair(false, AppStrings.ValidationTypes.invalidMobileNumber)

            }

            multiImagesList.size == 0 -> {
                validations.value = Pair(false, AppStrings.ValidationTypes.emptyImages)
            }

            else -> {
                validations.value = Pair(true, AppStrings.ValidationTypes.success)
            }
        }
    }

    fun createAsset(jsonObject: JSONObject) {
        CLog.e(ContentValues.TAG, "loginApi() called with: jsonObject = $jsonObject")

        viewModelScope.launch {
            validateNetwork {
                setIsLoading(true)
                repo.createAsset(AppMethods.getToken(sm, false), jsonObject)
                    .isRequestCallSuspendSuccess(success = {
                        CLog.e("TAG", "login:it---->  " + it)
                        _createAssetResponse.value = it
                    }, failure = { body, errorType, message ->
                        println("failure-body-> " + body)
                        println("failure-errorType-> " + errorType)
                        errorMessage.value = message

                    })
                setIsLoading(false)
            }
        }
    }

    fun updateAsset(jsonObject: JSONObject) {
        CLog.e(ContentValues.TAG, "update_asset() called with: jsonObject = $jsonObject")

        viewModelScope.launch {
            validateNetwork {
                setIsLoading(true)
                repo.updateAsset(AppMethods.getToken(sm, false), jsonObject)
                    .isRequestCallSuspendSuccess(success = {
                        CLog.e("TAG", "login:it---->  " + it)
                        _updateAssetResponse.value = it
                    }, failure = { body, errorType, message ->
                        println("failure-body-> " + body)
                        println("failure-errorType-> " + errorType)
                        errorMessage.value = message

                    })
                setIsLoading(false)
            }
        }
    }

    fun getAssets() {
        var jsonObject = JSONObject()
        jsonObject.put(AppStrings.InputData.page, page.value)
        jsonObject.put(AppStrings.InputData.pageSize, pagesize.value)
        jsonObject.put(AppStrings.InputData.Search, searchString.value)
        jsonObject.put(AppStrings.InputData.status, filterStatus.value)

        viewModelScope.launch {
            validateNetwork {
                setIsLoading(true)
                repo.getAssets(jsonObject, AppMethods.getToken(sm, false))
                    .isRequestCallSuspendSuccess(success = {
                        CLog.e("TAG", "login:it---->  " + it)
                        _getAssetsResponse.value = it
                    }, failure = { body, errorType, message ->
                        println("failure-body-> " + body)
                        println("failure-errorType-> " + errorType)
                        errorMessage.value = message

                    })
                setIsLoading(false)
            }
        }
    }

    fun deleteAsset(id: String, jsonObject: JSONObject) {

        viewModelScope.launch {
            validateNetwork {
                setIsLoading(true)
                repo.deleteAsset(id, jsonObject, AppMethods.getToken(sm, false))
                    .isRequestCallSuspendSuccess(success = {
                        CLog.e("TAG", "deleteAsset:it---->  " + it)
                        _deleteAssetResponse.value = it
                    }, failure = { body, errorType, message ->
                        println("failure-body-> " + body)
                        println("failure-errorType-> " + errorType)
                        errorMessage.value = message

                    })
                setIsLoading(false)
            }
        }
    }

    fun getAssetById(imei: String) {
        viewModelScope.launch {
            validateNetwork {
                setIsLoading(true)
                repo.getAssetById(imei, AppMethods.getToken(sm, false))
                    .isRequestCallSuspendSuccess(success = {
                        CLog.e("TAG", "deleteAsset:it---->  " + it)
                        _getAssetByIdResponse.value = it
                    }, failure = { body, errorType, message ->
                        println("failure-body-> " + body)
                        println("failure-errorType-> " + errorType)
                        errorMessage.value = message

                    })
                setIsLoading(false)
            }
        }
    }

    fun generateUploadrls(activity: Activity) {
        Log.e("TAG", "getPutUrls:awsList ${awsList.size}")
        val jsonObject = JSONObject()
        jsonObject.put(AppStrings.InputData.fileNames, JSONArray(awsList))
        viewModelScope.launch {
            validateNetwork {
                setIsLoading(true)
                repo.generateUploadrls(AppMethods.getToken(sm, false), jsonObject)
                    .isRequestCallSuspendSuccess(
                        activity = activity,
                        success = {
                            _getUploadUrlsResponse.value = it
                        },
                        failure = { body, errorType, message ->
                            println("failure-body-> " + body)
                            println("failure-errorType-> " + errorType)
                        },
                        unAuthorised = { body, errorType, message ->  // need to call api if we get unAuthorised error
                            generateUploadrls(activity)
                            Log.e(ContentValues.TAG, "unAuthorised: ${body},${errorType}")
                            Log.e(ContentValues.TAG, "unAuthorised: message = ${message}")
                        })
                setIsLoading(false)
            }
        }
    }
    fun assignTempValues(){
        filterStatus.value=filterStatusTemp.value
    }
    fun reassignTempValues(){
        filterStatusTemp.value= filterStatus.value
    }
    fun clearTempValues(){
        filterStatusTemp.value="all"
    }



}