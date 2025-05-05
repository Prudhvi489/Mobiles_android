package com.mobivault.utils

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.mobivault.R

@BindingAdapter("bind:background")
fun background(view: ConstraintLayout, colorCode: String?) {
    view.setBackgroundColor(Color.parseColor(colorCode))
}

@BindingAdapter("bind:loadGlideImage")
fun loadGlideImage(view: ShapeableImageView, filePath: String?) {
    CLog.e("TAG", "loadGlideImage:${filePath} ")
    try {
        if (filePath.toString().isNotEmpty()) {
            AppMethods.glideCoverImage(view.context, filePath.toString(), view)
        }
    } catch (e: Exception) {

    }

}

@BindingAdapter("bind:loadcircleCropImage")
fun loadcircleCropImage(view: ShapeableImageView, filePath: String?) {
    CLog.e("TAG", "loadcircleCropImage:${filePath} ")
    try {
        if (filePath.toString().isNotEmpty()) {
            AppMethods.loadcircleCropImage(view, filePath.toString())
        }
    } catch (e: Exception) {

    }

}



@BindingAdapter("setBackground")
fun setBackgroundResource(button: MaterialTextView, status: Boolean) {
    if (status) {
        button.isEnabled = true
        button.setBackgroundResource(R.drawable.violet_button_bg)
    } else {
        button.isEnabled = false
        button.setBackgroundResource(R.drawable.gray_button_bg)
    }
}

@BindingAdapter("setIntText")
fun setIntText(view: TextInputEditText, value: Int?) {
    val newValue = value?.toString() ?: ""
    if (view.text?.toString() != newValue) {
        view.setText(newValue)
    }
}

@InverseBindingAdapter(attribute = "setIntText", event = "setIntTextAttrChanged")
fun getIntText(view: TextInputEditText): Int {
    return view.text?.toString()?.toIntOrNull() ?: 0
}

@BindingAdapter("setIntTextAttrChanged")
fun setIntTextListener(view: TextInputEditText, listener: InverseBindingListener?) {
    if (listener == null) return
    view.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            listener.onChange()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}







