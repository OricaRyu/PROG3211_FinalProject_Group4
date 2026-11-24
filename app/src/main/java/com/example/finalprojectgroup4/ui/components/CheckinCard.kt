package com.example.finalprojectgroup4.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.finalprojectgroup4.R
import com.example.finalprojectgroup4.data.model.Category
import java.util.Date
import java.util.Locale

/**
 * One check-in row: venue, category, note preview and date. Used on Home and the Check-ins list.
 * Takes plain values instead of a model class, so any screen can use it.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun CheckinCard(
    venueName: String,
    category: Category,
    note: String,
    createdAt: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColors = category.colors()
    val categoryLabel = stringResource(category.labelRes())
    // "Restaurant · Great pasta", or just "Restaurant" when there is no note
    val subtitle = if (note.isBlank()) categoryLabel
    else stringResource(R.string.format_subtitle, categoryLabel, note)

    // Short date such as "Nov 8", in the order the phone's language expects
    val dateText = remember(createdAt) {
        val pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMd")
        DateFormat.format(pattern, Date(createdAt)).toString()
    }

    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar: the venue's first letter on its category colour
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(categoryColors.container, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = venueName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = categoryColors.content
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = venueName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = dateText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
