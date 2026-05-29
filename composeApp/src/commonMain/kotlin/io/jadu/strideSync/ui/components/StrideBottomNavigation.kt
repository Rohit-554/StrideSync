package io.jadu.strideSync.ui.components

import io.jadu.strideSync.ui.theme.Spacing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.strideSync.ui.theme.Surface
import io.jadu.strideSync.ui.theme.TextSecondary

@Composable
fun StrideBottomNavigation(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface)
            .navigationBarsPadding()
            .height(Spacing.d64)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavTab.Home.render(selectedTab, onTabSelected)
        NavTab.Explore.render(selectedTab, onTabSelected)
        NavTab.Record.render(selectedTab, onTabSelected)
        NavTab.Profile.render(selectedTab, onTabSelected)
    }
}

private enum class NavTab(
    val label: String,
    val icon: ImageVector,
    val tabId: String
) {
    Home("Home", Icons.Default.Home, "home"),
    Explore("Explore", Icons.Default.Search, "explore"),
    Record("Record", Icons.Default.RadioButtonChecked, "record"),
    Profile("Profile", Icons.Default.Person, "profile");

    @Composable
    fun render(selectedTab: String, onTabSelected: (String) -> Unit) {
        BottomNavItem(
            label = label,
            icon = icon,
            isSelected = selectedTab == tabId,
            onClick = { onTabSelected(tabId) }
        )
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = TextSecondary
    val contentColor = if (isSelected) activeColor else inactiveColor

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(vertical = Spacing.xs)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(Spacing.xxl)
        )
        Text(
            text = label,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Box(
                modifier = Modifier
                    .size(Spacing.xs)
                    .background(activeColor, shape = CircleShape)
            )
        }
    }
}
