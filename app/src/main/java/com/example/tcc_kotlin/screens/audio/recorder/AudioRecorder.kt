package com.example.tcc_kotlin.screens.audio.recorder

import java.io.File

interface AudioRecorder {
    fun start(output: File)
    fun stop()
}