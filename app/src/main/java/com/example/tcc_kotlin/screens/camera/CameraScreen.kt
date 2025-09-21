package com.example.tcc_kotlin.screens.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.io.File

@Composable
@ExperimentalMaterial3Api
fun CameraScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = navController.context
    var recording: Recording? = null;

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

    val viewModel = viewModel<MainViewModel>()
    val bitmaps by viewModel.bitmaps.collectAsState()

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
        },
    ) { paddingValues ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        this.controller = controller
                        controller.bindToLifecycle(lifecycleOwner)
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
            ){}
            IconButton(
                onClick = {
                    controller.cameraSelector =
                        if(controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                        {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else CameraSelector.DEFAULT_BACK_CAMERA
                },
                modifier = Modifier
                            .offset(16.dp, 64.dp),
                colors = IconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.30f),
                    contentColor = Color.White,
                    disabledContainerColor = Color.LightGray.copy(alpha = 0.30f),
                    disabledContentColor = Color.Gray
                )

            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Trocar câmera",
                )
            }

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    },
                    colors = IconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.30f),
                        contentColor = Color.White,
                        disabledContainerColor = Color.LightGray.copy(alpha = 0.30f),
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Abra galeria",
                    )
                }
                IconButton(
                    onClick = {
                        takePhoto(
                            controller = controller,
                            onPhotoTaken = viewModel::onTakePhoto,
                            context = context
                        )
                    },
                    colors = IconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.30f),
                        contentColor = Color.White,
                        disabledContainerColor = Color.LightGray.copy(alpha = 0.30f),
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Tire a foto",
                    )
                }
                IconButton(
                    onClick = {
                        recording = recordVideo(controller, recording, context)
                    },
                    colors = IconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.30f),
                        contentColor = Color.White,
                        disabledContainerColor = Color.LightGray.copy(alpha = 0.30f),
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Gravar vídeo",
                    )
                }
                IconButton(
                    onClick = {
                        navController.navigate("main")
                    },
                    colors = IconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.30f),
                        contentColor = Color.White,
                        disabledContainerColor = Color.LightGray.copy(alpha = 0.30f),
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Voltar",
                    )
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun recordVideo(controller: LifecycleCameraController, recording: Recording?, context: Context): Recording? {

    var newRecording: Recording? = recording;

    if(newRecording != null){
        newRecording.stop();
        newRecording = null;
        return newRecording;
    }

    val outputFile = File(context.filesDir, "recording.mp4")
    newRecording = controller.startRecording(
        FileOutputOptions.Builder(outputFile).build(),
        AudioConfig.create(true),
        ContextCompat.getMainExecutor(context),
    ){ event ->
        when(event) {
            is VideoRecordEvent.Finalize -> {
                if(event.hasError()){
                    newRecording?.close()
                    newRecording = null

                    Toast.makeText(
                        context,
                        "Falha ao gravar vídeo",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Vídeo gravado com sucesso!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    return newRecording;
}

private fun takePhoto(
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    context: Context
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback(){
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoTaken(rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Não foi possível tirar a foto: ", exception)
            }
        }
    )
}