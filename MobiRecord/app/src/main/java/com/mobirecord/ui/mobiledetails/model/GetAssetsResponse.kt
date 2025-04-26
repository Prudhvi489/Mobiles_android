package com.mobirecord.ui.mobiledetails.model

data class GetAssetsResponse(
    val assets: ArrayList<Asset>,
    val totalRecords: Int
)
data class Asset(
    val buyer_name: String?="",
    val buyer_phone: String?="",
    val buyer_price: Int?=0,
    val buying_date: String?="",
    val created_at: String?="",
    val deletedAt: Any?="",
    val imei: String?="",
    val mobile_model: String?="",
    val profit: Int?=0,
    val seller_aadhar: String?="",
    val seller_date: String?="",
    val seller_name: String?="",
    val seller_phone: String?="",
    val seller_price: Int?=0,
    val status: String?="",
    val updated_at: String?="",
    val image_keys:ArrayList<MobileModel>?= arrayListOf(),
    val imageUrls:ArrayList<String>?= arrayListOf(),
    val userId: Int?=0
)
data class MobileModel(val key:String?="",val url:String?="")