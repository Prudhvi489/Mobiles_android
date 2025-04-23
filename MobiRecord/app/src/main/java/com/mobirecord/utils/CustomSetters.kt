package com.mobirecord.utils

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.MutableLiveData
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.mobirecord.R
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

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







