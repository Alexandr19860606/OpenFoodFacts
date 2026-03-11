package com.korelin.openfoodfacts.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.korelin.openfoodfacts.ui.theme.Theme

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "❌",
            style = Theme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = Theme.typography.bodyLarge,
            color = Theme.colors.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Theme.colors.primary
            )
        ) {
            Text("Повторить")
        }
    }
}