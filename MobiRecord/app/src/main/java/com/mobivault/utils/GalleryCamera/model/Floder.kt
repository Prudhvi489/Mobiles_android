package com.mobivault.utils.cameraGallery.model

import android.os.Parcel
import android.os.Parcelable

data class Folder(
    val folderName: String,
    val items: ArrayList<Items>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        ArrayList<Items>()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(folderName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Folder> {
        override fun createFromParcel(parcel: Parcel): Folder {
            return Folder(parcel)
        }

        override fun newArray(size: Int): Array<Folder?> {
            return arrayOfNulls(size)
        }
    }
}
