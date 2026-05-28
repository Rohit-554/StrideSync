package io.jadu.strideSync.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.jadu.strideSync.domain.model.AthleteSummary
import io.jadu.strideSync.ui.components.StrideBottomNavigation
import io.jadu.strideSync.ui.viewmodel.ExploreViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExploreScreen(
    onNavigateToFeed: () -> Unit = {},
    onNavigateToRecord: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val viewModel: ExploreViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadSuggestions()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            StrideBottomNavigation(
                selectedTab = "explore",
                onTabSelected = { tab ->
                    when (tab) {
                        "home" -> onNavigateToFeed()
                        "record" -> onNavigateToRecord()
                        "profile" -> onNavigateToProfile()
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Explore",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = viewModel::onQueryChange,
                    placeholder = {
                        Text(
                            text = "Search athletes",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFF252830),
                        unfocusedContainerColor = Color(0xFF252830),
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            val title = if (uiState.showingSearchResults) "Search Results" else "Suggested Athletes"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (!uiState.showingSearchResults) {
                        Text(
                            text = "Refresh",
                            color = MaterialTheme.colorScheme.primaryContainer,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                                .clickable(onClick = viewModel::loadSuggestions)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            val athletes = if (uiState.showingSearchResults) uiState.searchResults else uiState.suggestedAthletes

            if (uiState.isLoading || uiState.isSearching) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else if (athletes.isEmpty()) {
                item {
                    EmptyExploreState(
                        message = if (uiState.showingSearchResults) {
                            "No athletes match \"${uiState.query.trim()}\"."
                        } else {
                            "No athlete suggestions available yet."
                        },
                        detail = uiState.errorMessage ?: "Try another search or refresh again."
                    )
                }
            } else {
                items(athletes, key = { it.id }) { athlete ->
                    AthleteRow(
                        athlete = athlete,
                        onToggleFollow = { viewModel.toggleFollow(athlete.id) }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AthleteRow(
    athlete: AthleteSummary,
    onToggleFollow: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AthleteAvatar(
                avatarUrl = athlete.avatarUrl,
                initials = athlete.displayName.take(2).uppercase()
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = athlete.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${athlete.activityCount} activities • ${athlete.followerCount} followers",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Button(
            onClick = onToggleFollow,
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (athlete.isFollowing) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color(0xFF252830)
                },
                contentColor = if (athlete.isFollowing) Color.White else MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text(if (athlete.isFollowing) "Following" else "Follow")
        }
    }
}

@Composable
private fun AthleteAvatar(
    avatarUrl: String?,
    initials: String
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl.isNullOrBlank()) {
            Text(
                text = initials,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        } else {
            AsyncImage(
                model = avatarUrl,
                contentDescription = initials,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun EmptyExploreState(
    message: String,
    detail: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = detail,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
