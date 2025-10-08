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

    val recorder by lazy {
        AndroidAudioRecorder(context)
    }

    val player by lazy {
        AndroidAudioPlayer(context)
    }

    var audioFile: File? = null

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
                            File(context.cacheDir, "audio_record.3gp").also {
                                recorder.start(it)
                                audioFile = it
                            }
                        },
                        text = "Gravar áudio",
                        icon = Icons.Filled.Mic,
                        aspectRatio = (2.5f)
                    )
                }
                item {
                    GridActionButton(
                        onClick = {
                            recorder.stop()
                        },
                        text = "Parar gravação",
                        icon = Icons.Filled.Square,
                        aspectRatio = (2.5f)
                    )
                }
                item {
                    GridActionButton(
                        onClick = {
                            player.playFile(audioFile ?: return@GridActionButton)
                        },
                        text = "Tocar áudio",
                        icon = Icons.Filled.PlayArrow,
                        aspectRatio = (2.5f)
                    )
                }
                item {
                    GridActionButton(
                        onClick = {
                            player.stop()
                        },
                        text = "Parar áudio",
                        icon = Icons.Filled.Square,
                        aspectRatio = (2.5f)
                    )
                }
            }
        }
    }
}
