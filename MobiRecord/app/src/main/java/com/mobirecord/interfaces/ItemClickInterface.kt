package com.mobirecord.interfaces

import android.app.Dialog
import com.mobirecord.ui.addmobiles.model.ListDataModel

interface ItemClickInterface {
    fun onClickType(from: String, id: String, name: String,specification:String? = null, dialog: Dialog)
}

interface MultiSelectionInterface {
    fun onClickDone(from: String, dialog: Dialog, list: ArrayList<ListDataModel>)
}