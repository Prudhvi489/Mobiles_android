package com.mobivault.repository

import android.util.Log
import com.mobireccord.network.Resource
import com.mobivault.network.ApiResult
import com.mobivault.network.safeApiCall
import com.mobivault.ui.addmobiles.model.AwsUploadUrlResponse
import com.mobivault.ui.login.model.LoginResponse
import com.mobivault.ui.mobiledetails.model.Asset
import com.mobivault.ui.mobiledetails.model.GetAssetsResponse
import com.mobivault.utils.CLog
import com.tuneconnect.network.RestApi

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class Repository @Inject constructor(val api: RestApi) {
    private val TAG = "Repository"
    suspend fun getCountries(
        headerMap: HashMap<String, String>,
    ): Resource<ApiResult<Any>?> {
        CLog.e(TAG, "getCountries() called with: headerMap = $headerMap, ")
        return safeApiCall {
            api.getCountries(headerMap)
        }

    }
    suspend fun login(
        jsonObject: JSONObject
    ): Resource<ApiResult<LoginResponse?>?> {
        CLog.e(TAG, "login() called with: jsonObject = $jsonObject")
         return safeApiCall {
            api.login(jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull()))
        }

    }
    suspend fun createAsset( headerMap: HashMap<String, String>,
        jsonObject: JSONObject
    ): Resource<ApiResult<Any?>?> {
        CLog.e(TAG, "createAsset() called with: headerMap = $headerMap, jsonObject = $jsonObject")
          return safeApiCall {
            api.createAsset(headerMap,jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull()))
        }

    }
    suspend fun updateAsset( headerMap: HashMap<String, String>,
        jsonObject: JSONObject
    ): Resource<ApiResult<Any?>?> {
        CLog.e(TAG, "updateAsset() called with: headerMap = $headerMap, jsonObject = $jsonObject")
          return safeApiCall {
            api.updateAsset(headerMap,jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull()))
        }

    }
    suspend fun getAssets(jsonObject:JSONObject, headerMap: HashMap<String, String>
     ): Resource<ApiResult<GetAssetsResponse?>?> {
        CLog.e(TAG, "getAssets() called with: jsonObject = $jsonObject, headerMap = $headerMap")
           return safeApiCall {
            api.getAssets(jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull()),headerMap)
        }

    }
    suspend fun deleteAsset( imei:String,jsonObject:JSONObject,headerMap: HashMap<String, String>
     ): Resource<ApiResult<Any?>?> {
        Log.e(
            TAG,
            "deleteAsset() called with: imei = $imei, jsonObject = $jsonObject, headerMap = $headerMap"
        )
             return safeApiCall {
                 val url = "http://3.110.196.14/api/asset/:${imei}"

//                 api.deleteAsset(url,headerMap)
            api.deleteAsset(imei,headerMap)
        }

    }
    suspend fun getAssetById( imei:String,headerMap: HashMap<String, String>
     ): Resource<ApiResult<Asset?>?> {
        Log.e(
            TAG,
            "getAssetById() called with: imei = $imei, headerMap = $headerMap"
        )
             return safeApiCall {
                 val url = "http://3.110.196.14/api/asset/:${imei}"

//                 api.deleteAsset(url,headerMap)
            api.getAssetById(imei,headerMap)
        }

    }

    suspend fun updateCelebrityDetails(
        headerMap: HashMap<String, String>,
        documentType: Int,
        categoryId: Int,
        countryId: Int,
        description: String,
        categoryOther: String,
        doc: MultipartBody.Part,
        jsonObj: JSONArray
    ): Resource<ApiResult<Any>?> {
        CLog.e(
            TAG,
            "updateCelebrityDetails() called with: headerMap = $headerMap, documentType = $documentType, categoryId = $categoryId, countryId = $countryId, description = $description, doc = $doc, jsonObj = $jsonObj"
        )
        return safeApiCall {
            api.updateCelebrityDetails(
                headerMap,
                documentType.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                countryId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                description.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                categoryOther.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                RequestBody.create("application/json".toMediaTypeOrNull(), jsonObj.toString()),
                doc
            )
        }

    }
    suspend fun generateUploadrls(
        token: HashMap<String, String>, jsonObject: JSONObject
    ): Resource<ApiResult<AwsUploadUrlResponse?>?> {
        Log.e(TAG, "getPutUrls() called with: token = $token, jsonObject = $jsonObject")
        return safeApiCall {
            api.generateUploadrls(
                token, jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull()),
            )
        }

    }


}