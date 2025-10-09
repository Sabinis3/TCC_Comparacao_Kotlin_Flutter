package com.example.tcc_kotlin.screens.audio.player

import java.io.File

interface AudioPlayer {
    fun playFile(file: File, onCompletion: () -> Unit = {})
    fun stop()
}
