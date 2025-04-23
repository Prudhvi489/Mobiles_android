package com.mobirecord.core

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mobirecord.utils.CLog

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    val networkLiveData = MutableSharedFlow<Boolean>()
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    private val _networkAlerts = MutableSharedFlow<NetworkState>()
    val networkAlerts = _networkAlerts.asSharedFlow()

    var cancelNetworkRequest = false
    private var networkCallJob: Job? = null


    fun setIsLoading(isLoading: Boolean?) {
        CLog.e("TAG", "setIsLoading: value ->${isLoading} ")
        this._isLoading.postValue(isLoading)
    }


    init {
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun validateNetwork(onNetworkConnected: suspend () -> Unit) {
        networkCallJob = viewModelScope.launch {
            testNetwork()
            if (isActive) {
                onNetworkConnected()
            }
        }

    }




/*
    private suspend fun testNetwork() {
        try {
            val connectivityManager =
                getApplication<Application>().getSystemService(Application.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network =
                connectivityManager.activeNetwork ?: throw UnknownHostException("No active network")
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                ?: throw UnknownHostException("Network capabilities not available")

            val isConnected =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

            // Perform a simple HTTP request to check if the network provides internet access
            val hasInternetAccess = try {
                withContext(Dispatchers.IO) {
                    val url = URL("https://www.google.com") // Using Google as a test URL
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connectTimeout = 3000 // Set a short timeout for the test
                    connection.readTimeout = 3000
                    connection.requestMethod = "HEAD"
                    val responseCode = connection.responseCode
                    responseCode == HttpURLConnection.HTTP_OK
                }
            } catch (e: Exception) {
                false
            }

            val networkState = when {
                !isConnected -> NetworkState.NoConnection
                !hasInternetAccess -> NetworkState.SlowConnection
                else -> NetworkState.GoodConnection
            }
            _networkAlerts.emit(networkState)
            if (networkState == NetworkState.SlowConnection && cancelNetworkRequest.not()) {
                delay(2000)
                testNetwork()
            } else if (cancelNetworkRequest) {
                networkCallJob?.cancel()
                cancelNetworkRequest = false
            }


        } catch (e: UnknownHostException) {
            Log.e("NetworkCheck", "UnknownHostException: ${e.message}")
            _networkAlerts.emit(NetworkState.NoConnection) // Emit as no connection
            delay(2000)
            testNetwork()
        } catch (e: Exception) {
            Log.e("NetworkCheck", "Exception: ${e.message}")
            _networkAlerts.emit(NetworkState.NoConnection) // Emit as no connection
            delay(2000)
            testNetwork()
        }
    }
*/

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun testNetwork() {
        val connectivityManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }

        val networkState = when {
            networkCapabilities == null -> {
                NetworkState.NoConnection
            }
            !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> {
                NetworkState.NoConnection
            }
            !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> {
                // Additional check for internet validation capability
                NetworkState.NoConnection
            }
            else -> {
                val downSpeed = networkCapabilities.linkDownstreamBandwidthKbps
                val upSpeed = networkCapabilities.linkUpstreamBandwidthKbps

                when {
                    downSpeed < 1000 || upSpeed < 500 -> {
                        NetworkState.SlowConnection
                    }
                    else -> {
                        NetworkState.GoodConnection
                    }
                }
            }
        }

        _networkAlerts.emit(networkState)

        if ((networkState == NetworkState.NoConnection || networkState == NetworkState.SlowConnection) && !cancelNetworkRequest) {
            delay(2000)
            testNetwork()
        } else if (cancelNetworkRequest) {
            networkCallJob?.cancel()
            cancelNetworkRequest = false
        }
    }

}

sealed class NetworkState {
    object NoConnection : NetworkState()
    object SlowConnection : NetworkState()
    object GoodConnection : NetworkState()
}
