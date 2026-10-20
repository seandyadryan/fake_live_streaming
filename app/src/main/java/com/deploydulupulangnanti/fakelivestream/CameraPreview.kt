package com.deploydulupulangnanti.fakelivestream

import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
@SuppressLint("MissingPermission") // The calling screen checks runtime permission before creating this preview.
fun CameraPreview(front: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val view = remember { PreviewView(context).apply { implementationMode = PreviewView.ImplementationMode.COMPATIBLE } }
    var error by remember { mutableStateOf<String?>(null) }
    DisposableEffect(front, owner) {
        var disposed = false
        var provider: ProcessCameraProvider? = null
        var preview: Preview? = null
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            if (!disposed) {
                runCatching {
                    val cameraProvider = future.get()
                    provider = cameraProvider
                    val selector = if (front) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
                    check(cameraProvider.hasCamera(selector)) { "This camera is unavailable. Try switching cameras." }
                    val useCase = Preview.Builder().build().also { it.surfaceProvider = view.surfaceProvider }
                    preview = useCase
                    cameraProvider.bindToLifecycle(owner, selector, useCase)
                    error = null
                }.onFailure { error = "Camera unavailable. Switch cameras or use a studio background." }
            }
        }, ContextCompat.getMainExecutor(context))
        onDispose { disposed = true; preview?.let { provider?.unbind(it) } }
    }
    Box(modifier) {
        AndroidView(factory = { view }, modifier = Modifier.matchParentSize())
        error?.let { Text(it, Modifier.align(Alignment.Center).background(Color(0xCC0C0D16)).padding(24.dp), color = Color.White) }
    }
}
