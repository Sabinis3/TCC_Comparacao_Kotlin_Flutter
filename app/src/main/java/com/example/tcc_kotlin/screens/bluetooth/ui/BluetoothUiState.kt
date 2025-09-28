package com.example.tcc_kotlin.screens.bluetooth.ui

import com.example.tcc_kotlin.screens.bluetooth.data.BluetoothDevice

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList()
)
