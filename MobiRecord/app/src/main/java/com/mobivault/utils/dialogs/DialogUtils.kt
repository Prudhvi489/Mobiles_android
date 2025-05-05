package com.mobivault.utils.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mobivault.R
import com.mobivault.databinding.SuccessDialogBinding
import com.mobivault.databinding.UnauthorizedDialogBinding
import com.mobivault.utils.SessionManager

class DialogUtils {
    companion object{
        var unauthorizedDialogBinding: UnauthorizedDialogBinding? = null
        lateinit var successDialogBinding: SuccessDialogBinding

        fun unAuthorizedDialog(
            activity: Activity? = null, sm: SessionManager? = null, msg: String? = ""
        ) {

            val dialog = Dialog(activity!!)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            unauthorizedDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity), R.layout.unauthorized_dialog, null, false
            )


            dialog.apply {
                setContentView(unauthorizedDialogBinding!!.root)
                window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
                )
            }


            dialog.show()
            dialog.setCancelable(false)
            unauthorizedDialogBinding!!.contentTv.text = msg
            unauthorizedDialogBinding!!.okMb.setOnClickListener {
                dialog.dismiss()
                sm!!.clearSession()
//                FireChatData.clearCache()
//                AppMethods.clearAllNotifications(activity)
//                var intent = Intent(activity, OnBoardingActivity::class.java)
//                activity.startActivity(intent)
//                activity.finish()

            }

        }

        fun alertDialog(
            activity: Activity? = null,
            fragment: Fragment? = null,
            message: String,
            type: String = "",
            sm: SessionManager? = null,
            from: String? = ""
        ) {


            var context = if (fragment != null) {
                fragment.context
            } else {
                activity
            }
            val dialog = if (fragment != null) {
                fragment.context?.let { Dialog(it) }
            } else {
                activity?.let { Dialog(it) }
            }
            dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            successDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.success_dialog, null, false
            )
              dialog.apply {
                setContentView(successDialogBinding.root)
                window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
                )
            }
            successDialogBinding.contentTv.text = message

            dialog.show()
            dialog.setCancelable(false)

            dialog.setOnCancelListener { dialogInterface: DialogInterface? ->

            }
            dialog.setOnDismissListener { dialogInterface: DialogInterface? ->
                // nationalitiesSelected()

            }
            successDialogBinding.cancelIcon.setOnClickListener {

                dialog.dismiss()
            }
        }

    }
}