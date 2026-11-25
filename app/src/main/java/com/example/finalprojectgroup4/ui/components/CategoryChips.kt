package com.example.finalprojectgroup4.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.finalprojectgroup4.R
import com.example.finalprojectgroup4.data.model.Category

/**
 * Filter chips: All, Restaurants, Events, Other. Exactly one is selected at a time.
 * Used on the Check-ins list, and reusable on the Map (Track D).
 *
 * @param selected the chosen category, or null for "All"
 * @param onSelect called with the tapped category, or null when "All" is tapped
 * @param contentPadding space around the chips inside the scroll area. Use this rather than
 *   a padding modifier for side margins, so overflowing chips can scroll to the screen edge.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun CategoryChips(
    selected: Category?,
    onSelect: (Category?) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    // Scrolls sideways if the chips don't fit, for example with large text
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelect(null) },
            label = { Text(stringResource(R.string.filter_all)) }
        )
        Category.entries.forEach { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(category) },
                label = { Text(stringResource(category.filterLabelRes())) }
            )
        }
    }
}

/** Plural chip labels ("Restaurants", "Events"), unlike Category.labelRes() used on cards. */
@StringRes
private fun Category.filterLabelRes(): Int = when (this) {
    Category.RESTAURANT -> R.string.filter_restaurants
    Category.EVENT -> R.string.filter_events
    Category.OTHER -> R.string.filter_other
}
