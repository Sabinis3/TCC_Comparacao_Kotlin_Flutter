package com.example.tcc_kotlin.screens.feedbackTatil

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tcc_kotlin.components.GridActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackTatilScreen() {
    val context = LocalContext.current
    val composeHaptic = LocalHapticFeedback.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun vibrate(durationMs: Long = 40L) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    durationMs,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }

    fun vibrateWaveform() {
        // on-off-on (wait, vibrate, wait, vibrate...) first value is delay
        val timings = longArrayOf(0, 400, 600, 800, 1200, 400) // ms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(timings, -1) // -1 means do not repeat
            )
        } else {
            @Suppress("DEPRECATION") vibrator.vibrate(timings, -1)
        }
    }

    fun vibrateWaveformWithAmplitude() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 300, 400, 500, 600) // ms
            val amplitudes = intArrayOf(0, 80, 0, 200, 0) // 0 = off, higher = stronger
            vibrator.vibrate(
                VibrationEffect.createWaveform(timings, amplitudes, -1)
            )
        }
    }
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ){
                Column (
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Text(
                        text = "Feedback Tátil - VibrationEffect",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        GridActionButton(
                            onClick = { vibrate(80) },
                            text = "Pulso único",
                            icon = Icons.Filled.Vibration,
                            aspectRatio = 8f,
                            enabled = true
                        )
                    }
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        GridActionButton(
                            onClick = { vibrateWaveform() },
                            text = "Forma de onda",
                            icon = Icons.Filled.Vibration,
                            aspectRatio = 8f,
                            enabled = true
                        )
                    }
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        GridActionButton(
                            onClick = { vibrateWaveformWithAmplitude() },
                            text = "Forma de onda com amplitude",
                            icon = Icons.Filled.Vibration,
                            aspectRatio = 8f,
                            enabled = true
                        )
                    }
                    Text(
                        text = "Feedback Tátil - HapticFeedbackType",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp, top = 24.dp)
                    )
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        GridActionButton(
                            onClick = {
                                composeHaptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                            },
                            text = "Vibração suave com Haptic",
                            icon = Icons.Filled.Vibration,
                            aspectRatio = 8f,
                            enabled = true
                        )
                    }
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        GridActionButton(
                            onClick = {
                                composeHaptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                            },
                            text = "Vibração longa com Haptic",
                            icon = Icons.Filled.Vibration,
                            aspectRatio = 8f,
                            enabled = true
                        )
                    }
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        GridActionButton(
                            onClick = {
                                composeHaptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                            },
                            text = "Vibração para teclado virtual com Haptic",
                            icon = Icons.Filled.Vibration,
                            aspectRatio = 8f,
                            enabled = true
                        )
                    }
                }
            }

        }
}
