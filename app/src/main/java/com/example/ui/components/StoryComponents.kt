package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.StoryCategory
import com.example.ui.theme.BadgePalette
import com.example.ui.theme.PlayfulBadgePalettes

fun categoryIcon(category: StoryCategory): ImageVector = when (category) {
    StoryCategory.SCIFI -> Icons.Rounded.RocketLaunch
    StoryCategory.FANTASY -> Icons.Rounded.AutoAwesome
    StoryCategory.EVERYDAY -> Icons.Rounded.LocalFireDepartment
    StoryCategory.MYSTERY -> Icons.Rounded.Search
}

fun badgePaletteAt(index: Int): BadgePalette {
    val safeIndex = ((index % PlayfulBadgePalettes.size) + PlayfulBadgePalettes.size) % PlayfulBadgePalettes.size
    return PlayfulBadgePalettes[safeIndex]
}

@Composable
fun PlayfulPillBadge(
    text: String,
    palette: BadgePalette,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = palette.container,
        border = BorderStroke(1.2.dp, palette.border.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = palette.content,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = palette.content,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlayerAvatarDot(
    name: String,
    colorIndex: Int,
    modifier: Modifier = Modifier,
    sizeDp: Int = 36
) {
    val palette = badgePaletteAt(colorIndex)
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(palette.container)
            .border(2.dp, palette.border, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleMedium,
            color = palette.content,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
