package com.example.tcc_kotlin.screens.wifi

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

class AndroidConnectivityObserver(
    private val context: Context
): ConnectivityObserver {
    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!
    private val wifiManager = context.applicationContext.getSystemService<WifiManager>()!!

    override val isConnected: Flow<Boolean>
        get() = callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val connected = networkCapabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )
                    trySend(connected)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    trySend(false)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(false)
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(true)
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }

    override val wifiNetworks: Flow<List<WifiNetwork>>
        @SuppressLint("MissingPermission")
        get() = flow {
            while (true) {
                val results = wifiManager.scanResults
                val currentSsid = wifiManager.connectionInfo?.ssid?.removePrefix("\"")?.removeSuffix("\"")
                val networks = results.map { result ->
                    WifiNetwork(
                        ssid = result.SSID,
                        isCurrent = result.SSID == currentSsid
                    )
                }.filter { it.ssid.isNotBlank() }
                emit(networks)
                kotlinx.coroutines.delay(5000) // Scan every 5 seconds
            }
        }
}