package com.mobirecord.core

import android.os.Bundle
import androidx.viewbinding.ViewBinding

abstract class BaseBindingActivity<T: ViewBinding>: BaseActivity() {
    lateinit var binding :T

    abstract fun getPersistentView(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getPersistentView()
        setContentView(binding.root)
    }

}