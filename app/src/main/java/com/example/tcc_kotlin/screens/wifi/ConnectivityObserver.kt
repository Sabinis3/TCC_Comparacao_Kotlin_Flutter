package com.example.tcc_kotlin.screens.wifi

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isConnected: Flow<Boolean>
    val wifiNetworks: Flow<List<WifiNetwork>>
}