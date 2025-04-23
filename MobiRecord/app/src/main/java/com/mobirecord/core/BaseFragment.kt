package com.mobirecord.core

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mobirecord.R
import com.mobirecord.databinding.AppLoadingDialogBinding


open class BaseFragment : Fragment() {
    private var progressDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }


    open fun showLoading() {
        if (progressDialog == null) {
            progressDialog = Dialog(requireContext(), R.style.CustomDialog)
        } else {
            if (progressDialog?.isShowing == false) {
                progressDialog?.show()
            }
            return
        }

        val progressBinding = AppLoadingDialogBinding.inflate(LayoutInflater.from(requireContext()))

       /* Glide.with(requireActivity())
            .asGif()
            .load(R.drawable.loading) // Replace `your_gif_file` with the actual file name of your GIF
            .into(progressBinding.circleProgressView)*/

        progressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog?.setContentView(progressBinding.root)
        progressDialog?.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(), android.R.color.transparent
            )
        )
        progressDialog?.setCancelable(false)
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()
    }

    open fun hideLoading() {
        progressDialog?.dismiss()
    }

}
