package com.example.finalprojectgroup4.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.finalprojectgroup4.R
import com.example.finalprojectgroup4.validation.Validators.MAX_RATING


/**
 * Shows a 1 to 5 star rating: filled stars in the primary colour, empty ones in outline variant.
 * Screen readers hear one sentence ("Rated 4 out of 5") instead of five separate stars.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun RatingStars(
    rating: Int,
    modifier: Modifier = Modifier,
    starSize: Dp = 20.dp
) {
    val description = stringResource(R.string.cd_rating, rating, MAX_RATING)

    Row(
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(MAX_RATING) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < rating) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(starSize)
            )
        }
    }
}
