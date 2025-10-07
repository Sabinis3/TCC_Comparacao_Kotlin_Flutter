package com.example.tcc_kotlin.screens.camera

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CameraImagesViewModel : ViewModel() {
    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images = _images.asStateFlow()

    private val _videos = MutableStateFlow<List<String>>(emptyList())
    val videos = _videos.asStateFlow()

    fun onPhotoSaved(fileName: String) {
        _images.update { it + fileName }
    }

    fun onVideoRecorded(fileName: String) {
        _videos.update { it + fileName }
    }
}