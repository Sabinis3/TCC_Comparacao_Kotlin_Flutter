package com.example.tcc_kotlin.screens.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ThumbnailUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.platform.LocalDensity
import java.io.File

@Composable
fun PhotoBottomSheetContent(
    images: List<String>,
    videos: List<String>,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty() && videos.isEmpty()) {
        Box(
            modifier = modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Nenhuma mídia capturada")
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            contentPadding = PaddingValues(16.dp),
            modifier = modifier
        ) {

            items(
                items = images,
                span = { StaggeredGridItemSpan.FullLine }
            ) { fileName ->
                val context = LocalContext.current
                val file = remember(fileName) { File(context.filesDir, fileName) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImageThumbnail(file = file, size = 24.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = fileName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            items(
                items = videos,
                span = { StaggeredGridItemSpan.FullLine }
            ) { fileName ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Videocam,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = fileName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageThumbnail(
    file: File,
    size: Dp
) {
    val density = LocalDensity.current
    val pxSize = with(density) { size.toPx().toInt() }

    val thumbnail: Bitmap? by remember(file.path, pxSize) {
        mutableStateOf(
            BitmapFactory.decodeFile(file.absolutePath)?.let { bmp ->
                val extractedThumbnail = ThumbnailUtils.extractThumbnail(
                    bmp,
                    pxSize,
                    pxSize,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT
                )

                extractedThumbnail?.let { thumb ->
                    val matrix = Matrix().apply {
                        postRotate(90f)
                    }
                    Bitmap.createBitmap(
                        thumb,
                        0,
                        0,
                        thumb.width,
                        thumb.height,
                        matrix,
                        true
                    ).also {
                        if (it != thumb) {
                            thumb.recycle()
                        }
                    }
                }
            }
        )
    }

    if (thumbnail != null) {
        Image(
            bitmap = thumbnail!!.asImageBitmap(),
            contentDescription = "Preview",
            modifier = Modifier
                .width(size)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .width(size)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}
