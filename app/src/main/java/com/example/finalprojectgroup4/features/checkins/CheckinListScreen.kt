package com.example.finalprojectgroup4.features.checkins

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalprojectgroup4.R
import com.example.finalprojectgroup4.data.model.Category
import com.example.finalprojectgroup4.ui.preview.previewCheckins
import com.example.finalprojectgroup4.ui.components.CategoryChips
import com.example.finalprojectgroup4.ui.components.CheckinCard
import com.example.finalprojectgroup4.ui.components.SpotTopBar
import com.example.finalprojectgroup4.ui.theme.FinalProjectGroup4Theme

/**
 * Mandatory List: the user's check-ins in a LazyColumn, with category filter chips and the FAB.
 *
 * This part gets the ViewModel and reads its state; CheckinListContent below only draws it.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun CheckinListScreen(
    onAddCheckin: () -> Unit,
    onCheckinClick: (checkinId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CheckinListViewModel = viewModel()
) {
    // Stops collecting while the app is in the background
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CheckinListContent(
        uiState = uiState,
        onSelectCategory = viewModel::selectCategory,
        onAddCheckin = onAddCheckin,
        onCheckinClick = onCheckinClick,
        modifier = modifier
    )
}

/**
 * Draws the list from its state. No ViewModel, no data loading: just layout.
 *
 * Kind: @Composable function. Owner: Track A (Sébastien).
 */
@Composable
fun CheckinListContent(
    uiState: CheckinListUiState,
    onSelectCategory: (Category?) -> Unit,
    onAddCheckin: () -> Unit,
    onCheckinClick: (checkinId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Hoisted so the list can jump back to the top when the filter changes
    val listState = rememberLazyListState()
    // The filter the list was last shown with. Saved so that coming back from Detail
    // (same filter) keeps the scroll position; only a real chip change resets it.
    var shownCategory by rememberSaveable { mutableStateOf(uiState.selectedCategory) }
    LaunchedEffect(uiState.selectedCategory) {
        if (uiState.selectedCategory != shownCategory) {
            shownCategory = uiState.selectedCategory
            listState.scrollToItem(0)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { SpotTopBar(title = stringResource(R.string.title_checkins)) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCheckin) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_new_checkin))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CategoryChips(
                selected = uiState.selectedCategory,
                onSelect = onSelectCategory,
                modifier = Modifier.padding(vertical = 8.dp),
                // Inside the scroll area, so overflowing chips scroll right to the screen edge
                contentPadding = PaddingValues(horizontal = 16.dp)
            )

            // Spinner and empty message share the same centred spot, so nothing jumps
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.checkins.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyListMessage(hasAnyCheckins = uiState.hasAnyCheckins)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    // Extra bottom padding so the FAB never covers the last card
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // key = id lets Compose keep each card's state when the list changes
                    items(uiState.checkins, key = { it.id }) { checkin ->
                        CheckinCard(
                            venueName = checkin.venueName,
                            category = checkin.category,
                            note = checkin.note.orEmpty(), // note is optional in the model
                            createdAt = checkin.createdAt,
                            onClick = { onCheckinClick(checkin.id) }
                        )
                    }
                }
            }
        }
    }
}

/** "No check-ins yet" when the user has none, or "None in this category" when a filter hides them all. */
@Composable
private fun EmptyListMessage(hasAnyCheckins: Boolean) {
    Text(
        text = stringResource(
            if (hasAnyCheckins) R.string.list_empty_filtered else R.string.home_no_checkins
        ),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    )
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CheckinListPreview() {
    FinalProjectGroup4Theme {
        CheckinListContent(
            uiState = CheckinListUiState(
                checkins = previewCheckins,
                selectedCategory = null,
                hasAnyCheckins = true
            ),
            onSelectCategory = {},
            onAddCheckin = {},
            onCheckinClick = {}
        )
    }
}

@Preview(name = "Filtered: Events", showBackground = true)
@Composable
private fun CheckinListFilteredPreview() {
    FinalProjectGroup4Theme {
        CheckinListContent(
            uiState = CheckinListUiState(
                checkins = previewCheckins.filter { it.category == Category.EVENT },
                selectedCategory = Category.EVENT,
                hasAnyCheckins = true
            ),
            onSelectCategory = {},
            onAddCheckin = {},
            onCheckinClick = {}
        )
    }
}

@Preview(name = "Empty", showBackground = true)
@Composable
private fun CheckinListEmptyPreview() {
    FinalProjectGroup4Theme {
        CheckinListContent(
            uiState = CheckinListUiState(),
            onSelectCategory = {},
            onAddCheckin = {},
            onCheckinClick = {}
        )
    }
}
