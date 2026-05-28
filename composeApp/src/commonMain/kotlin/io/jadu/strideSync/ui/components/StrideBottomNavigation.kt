package io.jadu.strideSync.ui.components

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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Search
import io.jadu.strideSync.ui.theme.Surface
import io.jadu.strideSync.ui.theme.TextSecondary

@Composable
fun StrideBottomNavigation(
    selectedTab: String, // "home", "explore", "record", "profile"
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface)
            .navigationBarsPadding()
            .height(64.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(label = "Home", icon = Icons.Default.Home, isSelected = selectedTab == "home", onClick = { onTabSelected("home") })
        BottomNavItem(label = "Explore", icon = Icons.Default.Search, isSelected = selectedTab == "explore", onClick = { onTabSelected("explore") })
        BottomNavItem(label = "Record", icon = Icons.Default.RadioButtonChecked, isSelected = selectedTab == "record", onClick = { onTabSelected("record") })
        BottomNavItem(label = "Profile", icon = Icons.Default.Person, isSelected = selectedTab == "profile", onClick = { onTabSelected("profile") })
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
            .padding(vertical = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = contentColor, modifier = Modifier.size(24.dp))
        Text(text = label, color = contentColor, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        if (isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(modifier = Modifier.size(4.dp).background(activeColor, shape = CircleShape))
        }
    }
}
