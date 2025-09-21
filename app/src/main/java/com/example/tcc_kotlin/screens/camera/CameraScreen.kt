package com.example.tcc_kotlin.screens.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navController: NavController) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val viewModel = viewModel<CameraImagesViewModel>()
    val bitmaps by viewModel.bitmaps.collectAsState()
    val controller = rememberCameraController(context, lifecycleOwner)

    var recording by rememberSaveable { mutableStateOf<Recording?>(null) }
    var isRecording by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            recording?.close()
            recording = null
        }
    }

    val buttonColors = IconButtonDefaults.iconButtonColors(
        containerColor = Color.Black.copy(alpha = 0.30f),
        contentColor = Color.White,
        disabledContainerColor = Color.LightGray.copy(alpha = 0.30f),
        disabledContentColor = Color.Gray
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            PhotoBottomSheetContent(
                bitmaps = bitmaps,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CameraPreview(controller)
            CameraTopBar(
                onSwitchCamera = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        else CameraSelector.DEFAULT_BACK_CAMERA
                },
                buttonColors = buttonColors
            )
            CameraActionBar(
                onOpenGallery = {
                    scope.launch { scaffoldState.bottomSheetState.expand() }
                },
                onTakePhoto = {
                    capturePhoto(
                        controller = controller,
                        context = context,
                        onPhotoTaken = viewModel::onTakePhoto
                    )
                },
                onToggleVideo = {
                    val (newRecording, nowRecording) =
                        toggleRecording(controller, recording, context)
                    recording = newRecording
                    isRecording = nowRecording
                },
                onBack = { navController.navigate("main") },
                isRecording = isRecording,
                buttonColors = buttonColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun rememberCameraController(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner
): LifecycleCameraController {
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
    }
    LaunchedEffect(controller, lifecycleOwner) {
        controller.bindToLifecycle(lifecycleOwner)
    }
    return controller
}

@Composable
private fun CameraPreview(controller: LifecycleCameraController) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            PreviewView(ctx).apply {
                this.controller = controller
            }
        }
    )
}

@Composable
private fun CameraTopBar(
    onSwitchCamera: () -> Unit,
    buttonColors: IconButtonColors,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onSwitchCamera,
        modifier = modifier
            .offset(16.dp, 64.dp),
        colors = buttonColors
    ) {
        Icon(
            imageVector = Icons.Default.Cameraswitch,
            contentDescription = "Switch camera"
        )
    }
}

@Composable
private fun CameraActionBar(
    onOpenGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onToggleVideo: () -> Unit,
    onBack: () -> Unit,
    isRecording: Boolean,
    buttonColors: IconButtonColors,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = onOpenGallery, colors = buttonColors) {
            Icon(Icons.Default.Photo, contentDescription = "Open gallery")
        }
        IconButton(onClick = onTakePhoto, colors = buttonColors) {
            Icon(Icons.Default.PhotoCamera, contentDescription = "Take photo")
        }
        IconButton(onClick = onToggleVideo, colors = buttonColors) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = if (isRecording) "Stop recording" else "Record video",
                tint = if (isRecording) Color.Red else Color.White
            )
        }
        IconButton(onClick = onBack, colors = buttonColors) {
            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
        }
    }
}

private fun capturePhoto(
    controller: LifecycleCameraController,
    context: Context,
    onPhotoTaken: (Bitmap) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                try {
                    val rotated = rotateBitmap(image)
                    onPhotoTaken(rotated)
                } catch (e: Exception) {
                    Log.e("Camera", "Capture processing failed", e)
                } finally {
                    image.close()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Photo capture failed", exception)
            }
        }
    )
}

private fun rotateBitmap(image: ImageProxy): Bitmap {
    val rotation = image.imageInfo.rotationDegrees.toFloat()
    val matrix = Matrix().apply { postRotate(rotation) }
    return Bitmap.createBitmap(
        image.toBitmap(),
        0,
        0,
        image.width,
        image.height,
        matrix,
        true
    )
}

@SuppressLint("MissingPermission")
private fun toggleRecording(
    controller: LifecycleCameraController,
    current: Recording?,
    context: Context
): Pair<Recording?, Boolean> {
    var active = current
    if (active != null) {
        active.stop()
        active.close()
        return null to false
    }
    val outputFile = File(context.filesDir, "recording.mp4")
    active = controller.startRecording(
        FileOutputOptions.Builder(outputFile).build(),
        AudioConfig.create(true),
        ContextCompat.getMainExecutor(context)
    ) { event ->
        if (event is VideoRecordEvent.Finalize) {
            if (event.hasError()) {
                Toast.makeText(context, "Video recording failed", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Video saved", Toast.LENGTH_LONG).show()
            }
        }
    }
    return active to true
}