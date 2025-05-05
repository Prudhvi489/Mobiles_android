package com.mobivault.ui.addmobiles.model

import java.io.Serializable

data class MultiMediaModel(
    var id: String = "",
    val path: String,
    var videoThumbnail: String = "",
    var mediaType: Int = 0,
    var type:Int=0,
    var fileName:String="",
 ):Serializable
data class MultipleImagesListToBackend(var filename:String)
