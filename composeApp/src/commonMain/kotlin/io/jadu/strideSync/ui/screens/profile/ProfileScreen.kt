package io.jadu.strideSync.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.jadu.strideSync.domain.model.Activity
import io.jadu.strideSync.ui.components.SportTypeIcon
import io.jadu.strideSync.ui.components.StrideBottomNavigation
import io.jadu.strideSync.ui.components.StrideToast
import io.jadu.strideSync.ui.theme.Spacing
import io.jadu.strideSync.ui.theme.StrideColors
import io.jadu.strideSync.ui.theme.TextSecondary
import io.jadu.strideSync.ui.viewmodel.ProfileViewModel
import io.jadu.strideSync.utils.Formatters
import org.koin.compose.viewmodel.koinViewModel

private const val PROFILE_TAB = "profile"

@Composable
fun ProfileScreen(
    onNavigateToFeed: () -> Unit = {},
    onNavigateToExplore: () -> Unit = {},
    onNavigateToRecord: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onViewActivity: (String) -> Unit = {}
) {
    val viewModel: ProfileViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreenContent(
        uiState = uiState,
        onNavigateToFeed = onNavigateToFeed,
        onNavigateToExplore = onNavigateToExplore,
        onNavigateToRecord = onNavigateToRecord,
        onLogout = {
            viewModel.logout()
            onNavigateToLogin()
        },
        onViewActivity = onViewActivity
    )
}

@Composable
private fun ProfileScreenContent(
    uiState: ProfileViewModel.ProfileUiState,
    onNavigateToFeed: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToRecord: () -> Unit,
    onLogout: () -> Unit,
    onViewActivity: (String) -> Unit
) {
    var errorToast by remember { mutableStateOf("") }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) errorToast = uiState.errorMessage
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = { ProfileTopBar() },
            bottomBar = {
                StrideBottomNavigation(
                    selectedTab = PROFILE_TAB,
                    onTabSelected = { tab ->
                        when (tab) {
                            "home" -> onNavigateToFeed()
                            "explore" -> onNavigateToExplore()
                            "record" -> onNavigateToRecord()
                        }
                    }
                )
            },
            containerColor = StrideColors.Background
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = Spacing.xxl),
                verticalArrangement = Arrangement.spacedBy(Spacing.d0)
            ) {
                item {
                    AthleteHeroSection(
                        displayName = uiState.user?.displayName ?: "Athlete",
                        email = uiState.user?.email.orEmpty(),
                        activityCount = uiState.activityCount.toString(),
                        followerCount = uiState.followerCount.toString(),
                        followingCount = uiState.followingCount.toString()
                    )
                }

            item {
                HorizontalDivider(
                    color = StrideColors.SurfaceAlt,
                    modifier = Modifier.padding(vertical = Spacing.sm)
                )
            }

            item {
                LogoutButton(onLogout = onLogout)
            }

            item {
                HorizontalDivider(
                    color = StrideColors.SurfaceAlt,
                    modifier = Modifier.padding(vertical = Spacing.sm)
                )
            }

            item {
                RecentActivitiesHeader(count = uiState.recentActivities.size)
            }

            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Spacing.d200),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else if (uiState.recentActivities.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.xxxl),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No activities yet.\nGo record your first run!",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                items(uiState.recentActivities) { activity ->
                    RecentActivityItem(
                        activity = activity,
                        onClick = { onViewActivity(activity.id) }
                    )
                    HorizontalDivider(
                        color = StrideColors.BackgroundElevated,
                        modifier = Modifier.padding(horizontal = Spacing.lg)
                    )
                }
            }
            }
        }

        if (errorToast.isNotEmpty()) {
            StrideToast(
                message = errorToast,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = Spacing.lg),
                onDismiss = { errorToast = "" }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Profile",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
private fun AthleteHeroSection(
    displayName: String,
    email: String,
    activityCount: String,
    followerCount: String,
    followingCount: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xxl, vertical = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        AthleteAvatar(initials = displayName.take(2).uppercase())
        AthleteIdentity(
            name = displayName,
            subtitle = email
        )
        SocialStatsRow(
            activities = activityCount,
            following = followingCount,
            followers = followerCount
        )
    }
}

@Composable
private fun AthleteAvatar(initials: String) {
    Box(
        modifier = Modifier
            .size(Spacing.d88)
            .border(Spacing.xxs, StrideColors.BrandPrimaryStrong, CircleShape)
            .padding(Spacing.d3)
            .background(StrideColors.SurfaceAlt, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = StrideColors.TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AthleteIdentity(name: String, subtitle: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Text(
            text = name,
            color = StrideColors.TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                color = StrideColors.TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun SocialStatsRow(activities: String, following: String, followers: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SocialStatCell(value = activities, label = "Activities")
        SocialStatDivider()
        SocialStatCell(value = following, label = "Following")
        SocialStatDivider()
        SocialStatCell(value = followers, label = "Followers")
    }
}

@Composable
private fun SocialStatCell(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = StrideColors.TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            color = StrideColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun SocialStatDivider() {
    Box(
        modifier = Modifier
            .height(Spacing.xxxl)
            .width(Spacing.d1)
            .background(StrideColors.SurfaceAlt)
    )
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xxl),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onLogout) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(Spacing.d18)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = "Logout",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun RecentActivitiesHeader(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xxl, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recent Activities",
            color = StrideColors.TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$count total",
            color = StrideColors.TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun RecentActivityItem(
    activity: Activity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.xxl, vertical = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Spacing.d44)
                .background(StrideColors.SurfaceAlt, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            SportTypeIcon(
                sportType = activity.sportType,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            Text(
                text = activity.title,
                color = StrideColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = activity.startedAt.let { Formatters.timeAgo(it) },
                color = StrideColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
        ) {
            Text(
                text = activity.distanceM.let { Formatters.metersToKmString(it) },
                color = StrideColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "KM",
                color = StrideColors.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
