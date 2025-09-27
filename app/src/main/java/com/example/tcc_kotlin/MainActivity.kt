package com.example.tcc_kotlin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tcc_kotlin.screens.biometria.BiometriaScreen
import com.example.tcc_kotlin.screens.camera.CameraScreen
import com.example.tcc_kotlin.screens.feedbackTatil.FeedbackTatilScreen
import com.example.tcc_kotlin.screens.flash.FlashScreen
import com.example.tcc_kotlin.ui.theme.TCC_KotlinTheme

class MainActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TCC_KotlinTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            onBiometriaClick = { navController.navigate("biometria") },
                            onCameraClick = { navigateWithCameraPermissions(navController, "camera") },
                            onFeedbackTatilClick = { navController.navigate("feedbackTatil") },
                            onFlashClick = { navigateWithCameraPermissions(navController, "flash") }
                        )
                    }
                    composable("biometria") { BiometriaScreen(navController) }
                    composable("camera") { CameraScreen(navController) }
                    composable("feedbackTatil") { FeedbackTatilScreen(navController) }
                    composable("flash") { FlashScreen(navController) }
                }
            }
        }
    }

    private fun navigateWithCameraPermissions(navController: androidx.navigation.NavController, screen: String) {
        if (hasRequiredPermissions()) {
            navController.navigate(screen)
        } else {
            ActivityCompat.requestPermissions(
                this,
                CAMERAX_PERMISSIONS,
                0
            )
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(applicationContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}

@Composable
private fun MainScreen(
    onBiometriaClick: () -> Unit,
    onCameraClick: () -> Unit,
    onFeedbackTatilClick: () -> Unit,
    onFlashClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ActionButton("Biometria", onBiometriaClick)
            ActionButton("Câmera", onCameraClick)
            ActionButton("Vibração", onFeedbackTatilClick)
            ActionButton("Flash") { onFlashClick() }
        }
    }
}

@Composable
private fun ActionButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}