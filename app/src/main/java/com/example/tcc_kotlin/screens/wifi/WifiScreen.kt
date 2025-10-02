// kotlin
package com.example.tcc_kotlin.screens.wifi

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun WifiScreen() {
    val context = LocalContext.current
    val wifiManager = remember {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    val scanResults = remember { mutableStateListOf<ScanResult>() }
    var wifiInfo by remember { mutableStateOf<WifiInfo?>(null) }

    val requiredPermissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun hasAllPermissions(ctx: Context): Boolean =
        requiredPermissions.all {
            ContextCompat.checkSelfPermission(ctx, it) == PackageManager.PERMISSION_GRANTED
        }

    var hasPermissions by remember { mutableStateOf(hasAllPermissions(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasPermissions = result.values.all { it }
        if (hasPermissions) {
            updateCurrentConnection(wifiManager) { wifiInfo = it }
            startWifiScanSafely(wifiManager)
        }
    }

    DisposableEffect(hasPermissions) {
        if (!hasPermissions) {
            permissionLauncher.launch(requiredPermissions)
            onDispose { }
        } else {
            val receiver = object : BroadcastReceiver() {
                @SuppressLint("MissingPermission")
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
                    if (success) {
                        val results = wifiManager.scanResults
                        scanResults.clear()
                        scanResults.addAll(results)
                        updateCurrentConnection(wifiManager) { wifiInfo = it }
                    }
                }
            }
            val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                @Suppress("DEPRECATION")
                context.registerReceiver(receiver, filter)
            }

            updateCurrentConnection(wifiManager) { wifiInfo = it }
            startWifiScanSafely(wifiManager)

            onDispose {
                try {
                    context.unregisterReceiver(receiver)
                } catch (_: Exception) {
                }
            }
        }
    }

    val currentSsid = remember(wifiInfo) {
        wifiInfo?.ssid?.replace("\"", "")?.takeIf { it.isNotBlank() && it != "<unknown ssid>" } ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Wi‑Fi", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.padding(6.dp))

        if (!hasPermissions) {
            Text("Permissões necessárias não concedidas.")
            Spacer(Modifier.padding(6.dp))
            Button(onClick = { permissionLauncher.launch(requiredPermissions) }) {
                Text("Conceder permissões")
            }
            return@Column
        }

        // Current connection
        val info = wifiInfo
        if (info != null && info.networkId != -1 && currentSsid.isNotBlank()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Rede conectada atualmente", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.padding(4.dp))
                    Text("SSID: $currentSsid")
                    Text("BSSID: ${info.bssid ?: "-"}")
                    Text("Sinal: ${info.rssi} dBm")
                    Text("Velocidade: ${info.linkSpeed} Mbps")
                    Text("IP: ${formatIpAddress(info.ipAddress)}")
                }
            }
        } else {
            Text("Nenhuma rede Wi‑Fi conectada no momento.")
        }

        Spacer(Modifier.padding(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Redes disponíveis", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { startWifiScanSafely(wifiManager) }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Atualizar")
            }
        }

        if (scanResults.isEmpty()) {
            Text("Nenhuma rede encontrada.")
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(
                    items = scanResults,
                    key = { it.BSSID ?: "${it.SSID}-${it.frequency}" }
                ) { res ->
                    val isConnected = res.SSID == currentSsid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isConnected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                else Color.Transparent
                            )
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (res.SSID.isNullOrBlank()) "(Sem SSID)" else res.SSID,
                                fontWeight = if (isConnected) FontWeight.SemiBold else FontWeight.Normal
                            )
                            Text(
                                text = "Sinal: ${res.level} dBm • ${res.frequency} MHz",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        if (isConnected) {
                            Text("✓ Conectada", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Divider()
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun updateCurrentConnection(
    wifiManager: WifiManager,
    setInfo: (WifiInfo) -> Unit
) {
    setInfo(wifiManager.connectionInfo)
}

@SuppressLint("MissingPermission")
private fun startWifiScanSafely(wifiManager: WifiManager) {
    try {
        wifiManager.startScan()
    } catch (_: Exception) {
    }
}

private fun formatIpAddress(ip: Int): String {
    return String.format(
        "%d.%d.%d.%d",
        ip and 0xff,
        ip shr 8 and 0xff,
        ip shr 16 and 0xff,
        ip shr 24 and 0xff
    )
}