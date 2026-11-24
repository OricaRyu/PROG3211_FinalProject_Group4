package com.example.finalprojectgroup4.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.finalprojectgroup4.data.model.Category

/**
 * A background colour and the matching text/icon colour for one category.
 *
 * Kind: data class. Owner: Track A (Sébastien).
 */
data class CategoryColors(
    val container: Color,
    val content: Color
)

/**
 * Gives each category its own colour pair from our theme, so restaurants, events and
 * other places are easy to tell apart and switch correctly between light and dark.
 *
 * Kind: @Composable extension function on Category. Owner: Track A (Sébastien).
 */
@Composable
fun Category.colors(): CategoryColors {
    val scheme = MaterialTheme.colorScheme
    return when (this) {
        Category.RESTAURANT -> CategoryColors(scheme.primaryContainer, scheme.onPrimaryContainer)
        Category.EVENT -> CategoryColors(scheme.tertiaryContainer, scheme.onTertiaryContainer)
        Category.OTHER -> CategoryColors(scheme.secondaryContainer, scheme.onSecondaryContainer)
    }
}
