package com.mobivault.network

import android.app.Activity
import com.mobireccord.network.Resource
import com.mobivault.utils.AppMethods
import com.mobivault.utils.AppMethods.Companion.refreshAccessToken
import com.mobivault.utils.CLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException



/**
 * Function that executes the given function on Dispatchers.IO context and switch to Dispatchers.Main context when an error occurs
 * @param callFunction is the function that is returning the wanted object. It must be a suspend function. Eg:
 * override suspend fun loginUser(body: LoginUserBody, emitter: RemoteErrorEmitter): LoginUserResponse? = safeApiCall( { authApi.loginUser(body)} , emitter)
 * @param emitter is the interface that handles the error messages. The error messages must be displayed on the MainThread, or else they would throw an Exception.
 */


@Suppress("UNCHECKED_CAST")
suspend inline fun <T> safeApiCall(
    crossinline callFunction: suspend () -> Response<T>
): Resource<T?> {
    try {
        val myObject = withContext(Dispatchers.IO) {
            callFunction.invoke()
        }
        CLog.e("TAG", "safeMultiApiCalls: body " + myObject.isSuccessful())
        CLog.e("TAG", "safeMultiApiCalls: url " + myObject.raw().request.url)
        CLog.e("TAG", "safeMultiApiCalls: Response " + myObject)
        // CLog.e("TAG", "safeMultiApiCalls: error " + myObject.errorBody()?.string())
        var message =
            AppMethods.handleResponse(myObject.errorBody()?.string() ?: "Some Exception Occurred")
        CLog.e("TAG", "safeApiCall: message ->${message}")
        return if (myObject.isSuccessful) {
            Resource.Success(myObject.body() as T)
        } else if (myObject.code() == Error.UN_AUTHORISED_ERROR) {
            Resource.UnAuthorised(myObject.body(), ErrorType.SESSION_EXPIRED, myObject.message())
        } else {

            Resource.Error(
                myObject.body(), ErrorType.UNKNOWN, message ?: "Some Exception Occurred"
            )
        }

    } catch (e: Exception) {
        e.printStackTrace()
        when (e) {
            is HttpException -> {
                return if (e.code() == Error.UN_AUTHORISED_ERROR) {
                    val error = Error().apply {
                        message = Error.SESSION_EXPIRE
                    }
                    Resource.UnAuthorised(null, ErrorType.SESSION_EXPIRED, "")

                } else {
                    val body = e.response()?.errorBody()?.string() ?: ""
                    Resource.Error(null, ErrorType.UNKNOWN, body)
                }
            }

            is SocketTimeoutException -> {
                return Resource.Error(null, ErrorType.TIMEOUT, "")
            }

            is IOException -> {
                return Resource.Error(null, ErrorType.NETWORK, "")
            }

            is ConnectException -> {
                return Resource.Error(null, ErrorType.SERVER_ERROR, "")
            }

            is ClassCastException -> {
                return Resource.Error(null, ErrorType.CLASS_CAST_EXCEPTION, "")
            }

            else -> {
                return Resource.Error(null, ErrorType.UNKNOWN, e.toString())
            }
        }
    }
}


suspend fun <T> Resource<T>?.isRequestCallSuspendSuccess(
    activity: Activity? = null,
    success: suspend (T) -> Unit = {},
    failure: suspend (T?, ErrorType, String) -> Unit = { _, _, _ -> },
    unAuthorised: suspend (T?, ErrorType, String?) -> Unit = { _, _, _ -> }
) {
    val resData = this

    if (this is Resource.Success<*>) {
        if (isRequestSuccess(this.data as ApiResult<*>)) {
            success(resData?.data as T)
            CLog.e("TAG", "isRequestCallSuspendSuccess: api success ")
        } else {
            failure(
                resData?.data,
                resData?.errorType ?: ErrorType.UNKNOWN,
                resData?.message.toString()
            )
        }
    } else if (this is Resource.Error<*>) {
        failure(resData?.data, resData?.errorType!!, resData?.message.toString() ?: "")
    } else if (this is Resource.UnAuthorised<*>) {

        // Call the function to refresh the access token
        if (activity != null) {
            refreshAccessToken(activity) { refreshed ->
                    GlobalScope.launch{
                    if (refreshed) {

                        unAuthorised(resData?.data, resData?.errorType!!, resData?.message)
                    } else {
                        // Handle failure to refresh token
                        failure(resData?.data, resData?.errorType!!, resData?.message ?: "")
                    }
                }

            }

        } else {
            unAuthorised(resData?.data, resData?.errorType!!, resData?.message)
        }
        /*   unAuthorised(resData?.data, resData?.errorType!!,resData.message)
             AppMethods.refreshAccessToken(activity)
         failure(resData?.data, resData?.errorType!!)*/

    } else {
        failure(resData?.data, resData?.errorType!!, resData?.message.toString() ?: "")
    }
}

private fun isRequestSuccess(receivedData: ApiResult<*>?): Boolean {
    CLog.e("TAG", "isRequestSuccess() called with: receivedData = $receivedData")
    if (receivedData == null) {
        return false
    }
//    return receivedData.status == 1
    return true
}