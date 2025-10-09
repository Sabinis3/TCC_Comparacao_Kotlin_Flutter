package com.example.tcc_kotlin.screens.flash

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tcc_kotlin.components.GridActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashScreen() {
    val context = LocalContext.current
    var isFlashOn by remember { mutableStateOf(false) }

        Surface(
            modifier = Modifier
                        .fillMaxSize(),
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
                GridActionButton(
                    onClick = {
                        isFlashOn = !isFlashOn
                        setFlash(context, isFlashOn)
                    },
                    text = if (isFlashOn) "Desligar Flash" else "Ligar Flash",
                    icon = Icons.Filled.FlashOn,
                    aspectRatio = 8f,
                    enabled = true
                )
            }
        }
}

private fun setFlash(context: Context, state: Boolean) {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraId = cameraManager.cameraIdList[0]
    cameraManager.setTorchMode(cameraId, state)
}