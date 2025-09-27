package com.example.tcc_kotlin.screens.bluetooth

typealias BluetoothDeviceDomain = BluetoothDevice

data class BluetoothDevice (
    val name: String?,
    val address: String
)