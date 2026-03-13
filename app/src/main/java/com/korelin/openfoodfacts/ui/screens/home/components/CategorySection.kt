package com.korelin.openfoodfacts.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.korelin.openfoodfacts.ui.theme.Theme

@Composable
fun CategorySection(
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = remember {
        listOf("Напитки", "Снеки", "Соусы", "Десерты", "Замороженное", "Молочные", "Хлеб")
    }

    Column(modifier = modifier) {
        Text(
            text = "Категории",
            style = Theme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = false,
                    onClick = { onCategoryClick(category) },
                    label = { Text(category) },
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }
    }
}