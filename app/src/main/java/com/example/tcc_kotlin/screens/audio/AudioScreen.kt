package com.example.tcc_kotlin.screens.audio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Square
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
import com.example.tcc_kotlin.screens.audio.player.AndroidAudioPlayer
import com.example.tcc_kotlin.screens.audio.recorder.AndroidAudioRecorder
import java.io.File

@Composable
fun AudioScreen() {
    val context = LocalContext.current

    val recorder = remember { AndroidAudioRecorder(context) }
    val player = remember { AndroidAudioPlayer(context) }

    var audioFile by remember { mutableStateOf<File?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.wrapContentSize(),
                contentPadding = PaddingValues(0.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                item {
                    GridActionButton(
                        onClick = {
                            if (!isRecording && !isPlaying) {
                                File(context.cacheDir, "audio_record.3gp").also {
                                    recorder.start(it)
                                    audioFile = it
                                    isRecording = true
                                }
                            }
                        },
                        text = "Gravar áudio",
                        icon = Icons.Filled.Mic,
                        aspectRatio = 2.5f,
                        enabled = !isRecording && !isPlaying
                    )
                }
                item {
                    GridActionButton(
                        onClick = {
                            if (isRecording) {
                                recorder.stop()
                                isRecording = false
                            }
                        },
                        text = "Parar gravação",
                        icon = Icons.Filled.Square,
                        aspectRatio = 2.5f,
                        enabled = isRecording
                    )
                }
                item {
                    GridActionButton(
                        onClick = {
                            val file = audioFile
                            if (!isPlaying && !isRecording && file != null) {
                                isPlaying = true
                                player.playFile(file) {
                                    isPlaying = false
                                }
                            }
                        },
                        text = "Tocar áudio",
                        icon = Icons.Filled.PlayArrow,
                        aspectRatio = 2.5f,
                        enabled = !isRecording && !isPlaying && audioFile != null
                    )
                }
                item {
                    GridActionButton(
                        onClick = {
                            if (isPlaying) {
                                player.stop()
                                isPlaying = false
                            }
                        },
                        text = "Parar áudio",
                        icon = Icons.Filled.Square,
                        aspectRatio = 2.5f,
                        enabled = isPlaying
                    )
                }
            }
        }
    }
}
