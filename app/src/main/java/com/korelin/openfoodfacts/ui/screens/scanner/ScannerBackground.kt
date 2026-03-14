package com.korelin.openfoodfacts.ui.screens.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.korelin.openfoodfacts.ui.theme.Backgrounds

@Composable
fun ScannerBackground(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Backgrounds.scannerGradient)
    ) {
        // Паттерн сканера (линии)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = List(10) {
                            Color.White.copy(alpha = 0.05f)
                        }
                    )
                )
        )
        content()
    }
}