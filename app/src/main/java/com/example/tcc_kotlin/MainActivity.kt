package com.example.tcc_kotlin

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tcc_kotlin.components.GridActionButton
import com.example.tcc_kotlin.screens.audio.AudioScreen
import com.example.tcc_kotlin.screens.biometria.BiometriaScreen
import com.example.tcc_kotlin.screens.bluetooth.ui.BluetoothScreen
import com.example.tcc_kotlin.screens.camera.CameraScreen
import com.example.tcc_kotlin.screens.feedbackTatil.FeedbackTatilScreen
import com.example.tcc_kotlin.screens.flash.FlashScreen
import com.example.tcc_kotlin.screens.wifi.WifiScreen
import com.example.tcc_kotlin.ui.theme.TCC_KotlinTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }

    val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // Sem necessidade de implementação
        }

        val permissionLaucher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if (canEnableBluetooth && !isBluetoothEnabled){
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            permissionLaucher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        }

        setContent {
            TCC_KotlinTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val route = backStackEntry?.destination?.route ?: "main"
                val isMain = route == "main"

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(getTitleForRoute(route)) },
                            navigationIcon = {
                                if (!isMain) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack , contentDescription = "Voltar")
                                    }
                                } else {
                                    IconButton(onClick = { }) {
                                        Icon(Icons.Filled.Home, contentDescription = "Home")
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "main",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        composable("main") {
                            MainScreen(
                                onBiometriaClick = { navController.navigate("biometria") },
                                onCameraClick = { navigateWithCameraPermissions(navController, "camera") },
                                onFeedbackTatilClick = { navController.navigate("feedbackTatil") },
                                onFlashClick = { navigateWithCameraPermissions(navController, "flash") },
                                onBluetoothClick = { navController.navigate("bluetooth") },
                                onAudioClick = { navigateWithAudioPermissions(navController, "audio") },
                                onWifiClick = { navController.navigate("wifi") }
                            )
                        }
                        composable("biometria") { BiometriaScreen() }
                        composable("camera") { CameraScreen() }
                        composable("feedbackTatil") { FeedbackTatilScreen() }
                        composable("flash") { FlashScreen() }
                        composable("bluetooth") { BluetoothScreen() }
                        composable("audio") { AudioScreen() }
                        composable("wifi") { WifiScreen() }
                    }
                }
            }
        }
    }

    private fun getTitleForRoute(route: String?): String {
        return when (route) {
            "main" -> "Tela Principal"
            "biometria" -> "Biometria"
            "camera" -> "Câmera"
            "feedbackTatil" -> "Feedback Tátil"
            "flash" -> "Flash"
            "bluetooth" -> "Bluetooth"
            "audio" -> "Áudio"
            "wifi" -> "Wi-Fi"
            else -> "App"
        }
    }

    private fun navigateWithCameraPermissions(navController: androidx.navigation.NavController, screen: String) {
        if (hasCameraRequiredPermissions()) {
            navController.navigate(screen)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                0
            )
        }
    }

    private fun hasCameraRequiredPermissions(): Boolean {
        val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(applicationContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hasAudioRequiredPermissions(): Boolean {
        val AUDIO_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO
        )
        return AUDIO_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(applicationContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun navigateWithAudioPermissions(navController: androidx.navigation.NavController, screen: String) {
        if (hasAudioRequiredPermissions()) {
            navController.navigate(screen)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                0
            )
        }
    }
}

@Composable
private fun MainScreen(
    onBiometriaClick: () -> Unit,
    onCameraClick: () -> Unit,
    onFeedbackTatilClick: () -> Unit,
    onFlashClick: () -> Unit,
    onBluetoothClick: () -> Unit,
    onAudioClick: () -> Unit,
    onWifiClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(8.dp),
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { GridActionButton("Biometria", Icons.Rounded.Fingerprint, onBiometriaClick) }
            item { GridActionButton("Câmera", Icons.Rounded.CameraAlt, onCameraClick) }
            item { GridActionButton("Vibração", Icons.Rounded.Vibration, onFeedbackTatilClick) }
            item { GridActionButton("Flash", Icons.Rounded.FlashOn, onFlashClick) }
            item { GridActionButton("Bluetooth", Icons.Rounded.Bluetooth, onBluetoothClick) }
            item { GridActionButton("Áudio", Icons.Rounded.Mic, onAudioClick) }
            item { GridActionButton("Wi-Fi", Icons.Filled.Wifi, onWifiClick) }
        }
    }
}