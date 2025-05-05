package com.mobivault.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding
import com.mobivault.databinding.FragmentMainBinding


abstract class BaseBindingFragment<T: ViewBinding> : BaseFragment() {

    lateinit var binding: T
    lateinit var mainBinding: FragmentMainBinding
    var hasInitializedRootView = false

    abstract fun getPersistentView(inflater: LayoutInflater, container: ViewGroup?): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::binding.isInitialized) { // Inflate the layout for this fragment
            mainBinding = FragmentMainBinding.inflate(inflater, container, false)
            this.binding = this.getPersistentView(inflater, container)
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            mainBinding.fragmentLL.addView(this.binding.root.rootView, params)
        } else { // Do not inflate the layout again.
            // The returned View of onCreateView will be added into the fragment.
            // However it is not allowed to be added twice even if the parent is same.
            // So we must remove rootView from the existing parent view group
            // (it will be added back).
            (mainBinding.root.parent as? ViewGroup)?.removeView(binding.root)
        }

        return mainBinding.root
    }



}
