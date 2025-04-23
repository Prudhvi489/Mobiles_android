package com.mobirecord.utils.cameraGallery.multipleSelectionGallery.model

import android.net.Uri
import java.io.Serializable
import java.util.Date

/*
data class ImageVideoModel(
    var path: String? = null,
    var caption: String? = null,
    var isImage: Boolean = false,
    var select: Boolean = false,
    var createdDate: Date? = null,
    var count:Int?=1
):Serializable
*/

data class ImageVideoModel(
    var path: String? = null,
    var caption: String? = null,
    var isImage: Boolean = false,
    var select: Boolean = false,
    var createdDate: Date? = null,
    var count: Int? = 1,
    var contentUri: Uri?=null
): Serializable {


    // No need for a secondary constructor that calls the primary constructor
}