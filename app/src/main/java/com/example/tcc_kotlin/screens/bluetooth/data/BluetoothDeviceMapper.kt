package com.example.tcc_kotlin.screens.bluetooth.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return com.example.tcc_kotlin.screens.bluetooth.data.BluetoothDeviceDomain(
        name = this.name,
        address = this.address
    )
}