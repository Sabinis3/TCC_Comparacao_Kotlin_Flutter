package com.example.tcc_kotlin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
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
import com.example.tcc_kotlin.ui.theme.TCC_KotlinTheme

class MainActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            TCC_KotlinTheme {
                SideEffect {
                    window.navigationBarColor = androidx.compose.ui.graphics.Color.Black.toArgb()
                    androidx.core.view.WindowInsetsControllerCompat(
                        window,
                        window.decorView
                    ).isAppearanceLightNavigationBars = false
                }

                NavHost(navController, startDestination = "main") {
                    composable("main") {
                        Surface(
                            modifier = Modifier
                                        .fillMaxSize()
                                        .windowInsetsPadding(WindowInsets.safeDrawing),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Column (
                                modifier = Modifier
                                    .fillMaxSize()
                                    .statusBarsPadding()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton (
                                        onClick = { navController.navigate("biometria") },
                                        modifier = Modifier
                                            .width(150.dp),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Biometria",
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    OutlinedButton (
                                        onClick = {
                                            if (hasRequiredPermissions()) {
                                                navController.navigate("camera")
                                            } else {
                                                ActivityCompat.requestPermissions(
                                                    this@MainActivity,
                                                    CAMERAX_PERNISSIONS,
                                                    0
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .width(150.dp),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Câmera",
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                    composable("biometria") { BiometriaScreen(navController) }
                    composable("camera") { CameraScreen(navController) }
                }

            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERNISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERNISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}