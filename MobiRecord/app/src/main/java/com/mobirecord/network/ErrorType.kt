package com.mobirecord.network

import com.google.gson.annotations.SerializedName

class Error() {
    companion object {
        const val AUTHENTICATION_ERROR="402"
        const val UN_AUTHORISED_ERROR= 401
        const val NETWORK_ERROR = "1001"
        const val NETWORK_MESSAGE = "It seems your internet is not available,please check it and try again later"
        const val SESSION_EXPIRE = "Session Expired"
        const val TIMEOUT  = "Unable to connect to server."
    }

    @SerializedName("message")
    var message = ""
    @SerializedName("errorCode")
    var errorCode = ""
}

enum class ErrorType {
    NETWORK,
    TIMEOUT,
    SESSION_EXPIRED,
    UNKNOWN,
    SERVER_ERROR,
    CLASS_CAST_EXCEPTION
}