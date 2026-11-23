package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.StoryCatalog
import com.example.model.CompletedStory
import com.example.model.StorySegment
import com.example.model.VoiceStyle
import com.example.ui.components.ConfettiBurstOverlay
import com.example.ui.components.PlayfulPillBadge
import com.example.ui.components.badgePaletteAt
import com.example.ui.components.categoryIcon
import com.example.ui.theme.MarigoldTertiary
import com.example.ui.theme.NewsreaderFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StoryRevealScreen(
    story: CompletedStory,
    isDramaticMode: Boolean,
    revealedBlankIds: Set<String>,
    confettiTriggerCount: Int,
    selectedVoiceStyle: VoiceStyle,
    isNarratorReady: Boolean,
    isSpeaking: Boolean,
    onBackHome: () -> Unit,
    onToggleDramaticMode: (Boolean) -> Unit,
    onRevealBlank: (String) -> Unit,
    onRevealAll: () -> Unit,
    onUpdateWord: (String, String) -> Unit,
    onRateStory: (Int) -> Unit,
    onSelectVoiceStyle: (VoiceStyle) -> Unit,
    onToggleReadAloud: () -> Unit,
    onShareStory: (CompletedStory) -> Unit,
    onRemixSameStory: () -> Unit,
    onNextRandomStory: () -> Unit
) {
    BackHandler(onBack = onBackHome)

    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val copiedMsg = stringResource(R.string.snackbar_copied)

    var editingBlankId by remember { mutableStateOf<String?>(null) }
    var editDraft by remember { mutableStateOf("") }

    val blankLookup = remember(story.template.id) {
        story.template.blanks.associateBy { it.id }
    }

    val storyAnnotated = buildAnnotatedString {
        for (segment in story.template.segments) {
            when (segment) {
                is StorySegment.Text -> {
                    append(segment.content)
                }
                is StorySegment.BlankRef -> {
                    val blank = blankLookup[segment.blankId]
                    val filled = story.filledBlanks[segment.blankId]
                    val isRevealed = !isDramaticMode || segment.blankId in revealedBlankIds
                    val palette = badgePaletteAt(blank?.partOfSpeech?.paletteIndex ?: 0)

                    pushStringAnnotation(tag = "BLANK", annotation = segment.blankId)
                    if (isRevealed) {
                        withStyle(
                            SpanStyle(
                                color = palette.content,
                                background = palette.container,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append(" ${filled?.word ?: "???"} ")
                        }
                    } else {
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.onPrimary,
                                background = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(" [TAP: ${blank?.partOfSpeech?.label ?: "WORD"}] ")
                        }
                    }
                    pop()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.reveal_header_badge),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackHome,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("reveal_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.cd_back)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { onShareStory(story) },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("reveal_top_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = stringResource(R.string.action_share_story),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 660.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Reveal Mode Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = !isDramaticMode,
                            onClick = { onToggleDramaticMode(false) },
                            label = { Text(stringResource(R.string.reveal_instant_mode)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("reveal_mode_instant"),
                            shape = RoundedCornerShape(50)
                        )
                        FilterChip(
                            selected = isDramaticMode,
                            onClick = { onToggleDramaticMode(true) },
                            label = { Text(stringResource(R.string.reveal_dramatic_mode)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.TouchApp,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("reveal_mode_dramatic"),
                            shape = RoundedCornerShape(50)
                        )
                    }

                    // Editorial Storybook Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.65f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PlayfulPillBadge(
                                    text = story.template.category.badgeLabel,
                                    palette = badgePaletteAt(story.template.category.paletteIndex),
                                    icon = categoryIcon(story.template.category)
                                )
                                PlayfulPillBadge(
                                    text = story.template.laughTag,
                                    palette = badgePaletteAt(2),
                                    icon = Icons.Rounded.AutoAwesome
                                )
                            }

                            Text(
                                text = story.template.title,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            @Suppress("DEPRECATION")
                            ClickableText(
                                text = storyAnnotated,
                                style = TextStyle(
                                    fontFamily = NewsreaderFontFamily,
                                    fontSize = 20.sp,
                                    lineHeight = 33.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.testTag("revealed_story_text"),
                                onClick = { offset ->
                                    storyAnnotated
                                        .getStringAnnotations(tag = "BLANK", start = offset, end = offset)
                                        .firstOrNull()
                                        ?.let { annotation ->
                                            val blankId = annotation.item
                                            if (isDramaticMode && blankId !in revealedBlankIds) {
                                                onRevealBlank(blankId)
                                            } else {
                                                editingBlankId = blankId
                                                editDraft = story.filledBlanks[blankId]?.word.orEmpty()
                                            }
                                        }
                                }
                            )

                            Text(
                                text = stringResource(R.string.reveal_tap_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (isDramaticMode && revealedBlankIds.size < story.filledBlanks.size) {
                                TextButton(
                                    onClick = onRevealAll,
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Reveal Remaining (${story.filledBlanks.size - revealedBlankIds.size})")
                                }
                            }
                        }
                    }

                    // Funny Voice Narrator Card (Real Android TTS)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
                        ),
                        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.GraphicEq,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.reveal_narrator_title),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }

                                Button(
                                    onClick = onToggleReadAloud,
                                    enabled = isNarratorReady,
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("read_aloud_button"),
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSpeaking) {
                                            MaterialTheme.colorScheme.error
                                        } else {
                                            MaterialTheme.colorScheme.secondary
                                        }
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isSpeaking) Icons.Rounded.Stop else Icons.Rounded.VolumeUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isSpeaking) {
                                            stringResource(R.string.action_stop_reading)
                                        } else {
                                            stringResource(R.string.action_read_aloud)
                                        }
                                    )
                                }
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                VoiceStyle.entries.forEach { style ->
                                    FilterChip(
                                        selected = selectedVoiceStyle == style,
                                        onClick = { onSelectVoiceStyle(style) },
                                        label = { Text(style.label) },
                                        shape = RoundedCornerShape(50),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                                        ),
                                        modifier = Modifier
                                            .heightIn(min = 42.dp)
                                            .testTag("voice_chip_${style.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    }

                    // Laugh-O-Meter Rating Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.reveal_rate_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                (1..5).forEach { star ->
                                    val filled = star <= story.laughRating
                                    IconButton(
                                        onClick = { onRateStory(star) },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .testTag("rate_star_$star")
                                    ) {
                                        Icon(
                                            imageVector = if (filled) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                                            contentDescription = stringResource(R.string.cd_star_rating, star),
                                            tint = if (filled) MarigoldTertiary else MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.size(34.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Action Buttons Row 1: Share + Copy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val text = StoryCatalog.renderPlainText(story)
                                clipboardManager.setText(AnnotatedString(text))
                                scope.launch {
                                    snackbarHostState.showSnackbar(copiedMsg)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 50.dp)
                                .testTag("copy_story_button"),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.action_copy_story))
                        }

                        Button(
                            onClick = { onShareStory(story) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 50.dp)
                                .testTag("share_story_button"),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.action_share_story))
                        }
                    }

                    // Action Buttons Row 2: Remix + Next Tale
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRemixSameStory,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp)
                                .testTag("remix_story_button"),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Replay,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.action_play_again))
                        }

                        Button(
                            onClick = onNextRandomStory,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp)
                                .testTag("next_random_story_button"),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shuffle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.action_next_random))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Confetti Celebration Overlay
        ConfettiBurstOverlay(triggerKey = confettiTriggerCount)
    }

    // Inline Tap-to-Remix Word Dialog
    val activeEditId = editingBlankId
    if (activeEditId != null) {
        val blankInfo = blankLookup[activeEditId]
        val contributor = story.filledBlanks[activeEditId]?.enteredByPlayer
        AlertDialog(
            onDismissRequest = { editingBlankId = null },
            title = { Text(stringResource(R.string.dialog_edit_word_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (blankInfo != null) {
                        PlayfulPillBadge(
                            text = blankInfo.partOfSpeech.label,
                            palette = badgePaletteAt(blankInfo.partOfSpeech.paletteIndex)
                        )
                    }
                    if (!contributor.isNullOrBlank()) {
                        Text(
                            text = stringResource(R.string.dialog_edit_word_by, contributor),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedTextField(
                        value = editDraft,
                        onValueChange = { editDraft = it.take(36) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateWord(activeEditId, editDraft)
                        editingBlankId = null
                    },
                    enabled = editDraft.isNotBlank()
                ) {
                    Text(stringResource(R.string.dialog_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { editingBlankId = null }) {
                    Text(stringResource(R.string.dialog_cancel))
                }
            }
        )
    }
}
