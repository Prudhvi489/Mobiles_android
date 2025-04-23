package com.mobirecord.interfaces

import android.app.Dialog
import com.mobirecord.ui.addmobiles.model.MultiMediaModel
import com.mobirecord.ui.mobiledetails.model.Asset

interface BottomSheet {
    /* fun alertNoClick(type: String?, dialog: Dialog?, from: String?)
     fun alertYesClick(type: String?, dialog: Dialog?, fr
     om: String?)*/


      fun editTvClick(dialog: Dialog?,model:Asset)
    fun deleteTvClick(dialog: Dialog?,model: Asset)

}