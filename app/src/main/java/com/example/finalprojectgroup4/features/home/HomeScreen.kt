package com.example.finalprojectgroup4.features.home

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalprojectgroup4.R
import com.example.finalprojectgroup4.ui.components.CheckinCard
import com.example.finalprojectgroup4.ui.components.SpotTopBar
import com.example.finalprojectgroup4.ui.components.SyncStatusChip
import com.example.finalprojectgroup4.ui.components.colors
import com.example.finalprojectgroup4.ui.components.labelRes
import com.example.finalprojectgroup4.ui.theme.FinalProjectGroup4Theme

/**
 * Mandatory Home: nearby venues, latest check-ins and the FAB.
 *
 * This part gets the ViewModel and reads its state; HomeContent below only draws it.
 * Splitting them lets the preview show Home without a ViewModel.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun HomeScreen(
    onAddCheckin: () -> Unit,
    onVenueCheckIn: (venueId: String) -> Unit,
    onCheckinClick: (checkinId: String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    // Redraws Home whenever the ViewModel publishes a new HomeUiState
    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        uiState = uiState,
        onAddCheckin = onAddCheckin,
        onVenueCheckIn = onVenueCheckIn,
        onCheckinClick = onCheckinClick,
        onLogout = onLogout,
        modifier = modifier
    )
}

/**
 * Draws Home from a HomeUiState. No ViewModel, no data loading: just layout.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onAddCheckin: () -> Unit,
    onVenueCheckIn: (venueId: String) -> Unit,
    onCheckinClick: (checkinId: String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SpotTopBar(
                title = stringResource(R.string.title_home),
                actions = {
                    SyncStatusChip(
                        synced = uiState.isSynced,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.action_logout)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCheckin) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_new_checkin))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            // Extra bottom padding so the FAB never covers the last card
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- Nearby now ---
            item { SectionHeader(text = stringResource(R.string.home_nearby_now)) }

            if (uiState.nearbyVenues.isEmpty()) {
                item { EmptyMessage(text = stringResource(R.string.home_no_nearby)) }
            }
            items(uiState.nearbyVenues, key = { "venue-${it.id}" }) { venue ->
                NearbyVenueCard(venue = venue, onCheckIn = { onVenueCheckIn(venue.id) })
            }

            // Geoapify's terms require this attribution wherever its data is shown
            item {
                Text(
                    text = stringResource(R.string.home_powered_by_geoapify),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // --- Latest check-ins ---
            item {
                SectionHeader(
                    text = stringResource(R.string.home_latest_checkins),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (uiState.recentCheckins.isEmpty()) {
                item { EmptyMessage(text = stringResource(R.string.home_no_checkins)) }
            }
            items(uiState.recentCheckins, key = { "checkin-${it.id}" }) { checkin ->
                CheckinCard(
                    venueName = checkin.venueName,
                    category = checkin.category,
                    note = checkin.note,
                    createdAt = checkin.createdAt,
                    onClick = { onCheckinClick(checkin.id) }
                )
            }
        }
    }
}

/** Section title such as "Nearby now". */
@Composable
private fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

/** Shown in place of a section's cards when it has nothing to show. */
@Composable
private fun EmptyMessage(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/** One nearby venue: thumbnail, name, "Restaurant · 120 m" and a Check in button. */
@Composable
private fun NearbyVenueCard(
    venue: NearbyVenueItem,
    onCheckIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColors = venue.category.colors()

    OutlinedCard(
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
            // Thumbnail in the venue's category colour until venues have photos
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(categoryColors.container, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = null, // decorative
                    tint = categoryColors.content
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = venue.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        R.string.format_subtitle,
                        stringResource(venue.category.labelRes()),
                        distanceText(venue.distanceMeters)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = onCheckIn) {
                Text(stringResource(R.string.action_check_in))
            }
        }
    }
}

/** "120 m" under 1 km, otherwise "1.2 km". */
@Composable
private fun distanceText(meters: Int): String =
    if (meters < 1000) stringResource(R.string.distance_meters, meters)
    else stringResource(R.string.distance_km, meters / 1000f)

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeContentPreview() {
    FinalProjectGroup4Theme {
        HomeContent(
            uiState = HomeUiState(
                nearbyVenues = HomeSampleData.nearbyVenues,
                recentCheckins = HomeSampleData.recentCheckins,
                isSynced = true
            ),
            onAddCheckin = {},
            onVenueCheckIn = {},
            onCheckinClick = {},
            onLogout = {}
        )
    }
}

@Preview(name = "Empty", showBackground = true)
@Composable
private fun HomeContentEmptyPreview() {
    FinalProjectGroup4Theme {
        HomeContent(
            uiState = HomeUiState(),
            onAddCheckin = {},
            onVenueCheckIn = {},
            onCheckinClick = {},
            onLogout = {}
        )
    }
}
