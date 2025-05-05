package com.mobivault.core

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.mobivault.R


abstract class BaseVMBindingActivity<T : ViewBinding, VM : BaseViewModel>(private var viewModelClass: Class<VM>) :
    BaseBindingActivity<T>() {

    lateinit var viewModel: VM

    lateinit var noNetworkDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(viewModelClass)
        //  noNetworkDialog = networkAlertDialog()
        viewModel.isLoading.observe(this) {
            showLoading(it)
        }
//        instalizeSession()
        onCreatefun()
        lifecycleScope.launchWhenStarted {
            viewModel.networkLiveData.collect {
                onNetworkChange(it)
            }
        }




        lifecycleScope.launchWhenStarted {
            viewModel.networkAlerts.collect { state ->
                if (state == NetworkState.NoConnection || state == NetworkState.SlowConnection) {
                    // Create and show the dialog if it's not already showing
                    if (!::noNetworkDialog.isInitialized || !noNetworkDialog.isShowing) {
                        noNetworkDialog = networkAlertDialog(state)
                        noNetworkDialog.show()
                    }
                } else {
                    // Dismiss the dialog if it is showing and the network state is good
                    if (::noNetworkDialog.isInitialized && noNetworkDialog.isShowing) {
                        noNetworkDialog.dismiss()
                    }
                }
            }
        }


    }


    open fun onNetworkChange(isConnected: Boolean) {
//        Timber.tag("Activity Connected:").e(if (isConnected) "On Connect" else "On Disconnect")
    }


    private fun networkAlertDialog(state: NetworkState): AlertDialog {
        val (title, message) = when (state) {
            is NetworkState.NoConnection -> {
                getString(R.string.network_disconnected) to getString(R.string.please_check_the_network_and_retry)
            }

            is NetworkState.SlowConnection -> {
                getString(R.string.network_slow) to getString(R.string.slow_connection_message)
            }

            is NetworkState.GoodConnection -> {
                // If you want to handle good connection differently, you can add that logic here.
                return noNetworkDialog // Return the default dialog or create a no-op dialog.
            }
        }

        return AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(getString(R.string.retry)) { dialog, _ ->
                // Handle retry action
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                viewModel.cancelNetworkRequest = true
            }
        }.create()
    }

}