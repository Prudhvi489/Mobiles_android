package com.mobivault.ui.addmobiles.model

typealias AwsUploadUrlResponse  = Map<String, AwsItemResponseModel>

data class AwsItemResponseModel(
    val key: String,
    val uploadUrl: String,
    val fileName:String
)