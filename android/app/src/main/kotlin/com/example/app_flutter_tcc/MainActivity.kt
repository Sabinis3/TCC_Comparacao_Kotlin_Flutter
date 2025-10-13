package com.example.app_flutter_tcc

import android.net.wifi.WifiManager
import android.content.Context
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterFragmentActivity() {
    private val WIFI_CHANNEL = "com.example.app_flutter_tcc/wifi"
    private val BT_CHANNEL = "com.example.app_flutter_tcc/bluetooth"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        // Wi-Fi channel
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, WIFI_CHANNEL).setMethodCallHandler {
            call, result ->
            if (call.method == "getLinkSpeed") {
                val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val info = wifiManager.connectionInfo
                val linkSpeed = info.linkSpeed
                result.success(linkSpeed)
            } else {
                result.notImplemented()
            }
        }

        // Bluetooth channel
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, BT_CHANNEL).setMethodCallHandler {
            call, result ->
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                result.error("NO_BLUETOOTH", "Bluetooth não suportado", null)
                return@setMethodCallHandler
            }
            when (call.method) {
                "getBondedDevices" -> {
                    val devices = bluetoothAdapter.bondedDevices.map {
                        mapOf("name" to it.name, "address" to it.address)
                    }
                    result.success(devices)
                }
                "startDiscovery" -> {
                    val foundDevices = mutableListOf<Map<String, String>>()
                    val receiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {
                            val action = intent?.action
                            if (BluetoothDevice.ACTION_FOUND == action) {
                                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                                device?.let {
                                    foundDevices.add(mapOf("name" to (it.name ?: ""), "address" to it.address))
                                }
                            }
                            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                                unregisterReceiver(this)
                                result.success(foundDevices)
                            }
                        }
                    }
                    val filter = IntentFilter()
                    filter.addAction(BluetoothDevice.ACTION_FOUND)
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                    registerReceiver(receiver, filter)
                    bluetoothAdapter.startDiscovery()
                }
                else -> result.notImplemented()
            }
        }
    }
}