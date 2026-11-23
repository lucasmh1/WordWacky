package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.PlayerAvatarDot
import com.example.ui.components.PlayfulPillBadge
import com.example.ui.components.badgePaletteAt
import com.example.viewmodel.StudioSession

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WordStudioScreen(
    session: StudioSession,
    currentTurnPlayerName: String?,
    currentTurnColorIndex: Int,
    onWordChanged: (String) -> Unit,
    onRollRandomWord: () -> Unit,
    onRefreshIdeas: () -> Unit,
    onJumpToBlank: (Int) -> Unit,
    onPreviousBlank: () -> Unit,
    onNextOrReveal: () -> Unit,
    onAutoFillRest: () -> Unit,
    onExitStudio: () -> Unit
) {
    var showExitDialog by remember { mutableStateOf(false) }

    val handleBack = {
        if (session.hasAnyTypedWords) {
            showExitDialog = true
        } else {
            onExitStudio()
        }
    }

    BackHandler(onBack = handleBack)

    val progress by animateFloatAsState(
        targetValue = (session.currentBlankIndex + 1).toFloat() / session.totalBlanks.toFloat(),
        animationSpec = spring(stiffness = 300f),
        label = "studio_progress"
    )

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = session.template.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(
                                R.string.studio_step_format,
                                session.currentBlankIndex + 1,
                                session.totalBlanks
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = handleBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("studio_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onAutoFillRest,
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("studio_autofill_button")
                    ) {
                        Text(
                            text = stringResource(R.string.action_fill_all_random),
                            style = MaterialTheme.typography.labelLarge
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
                .padding(innerPadding)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 620.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Smooth progress bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(50)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )

                // Jumpable step dots
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(session.template.blanks) { idx, blank ->
                        val isCurrent = idx == session.currentBlankIndex
                        val isFilled = !session.enteredWords[blank.id].isNullOrBlank()
                        val dotBg = when {
                            isCurrent -> MaterialTheme.colorScheme.primary
                            isFilled -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        val dotText = when {
                            isCurrent -> MaterialTheme.colorScheme.onPrimary
                            isFilled -> MaterialTheme.colorScheme.onSecondaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(dotBg)
                                .then(
                                    if (isCurrent) {
                                        Modifier.border(2.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { onJumpToBlank(idx) }
                                .testTag("studio_step_dot_$idx"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${idx + 1}",
                                style = MaterialTheme.typography.labelLarge,
                                color = dotText
                            )
                        }
                    }
                }

                // Party Turn Banner (when Pass & Play is active)
                if (currentTurnPlayerName != null) {
                    val turnPalette = badgePaletteAt(currentTurnColorIndex)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = turnPalette.container,
                        border = BorderStroke(1.5.dp, turnPalette.border)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PlayerAvatarDot(
                                name = currentTurnPlayerName,
                                colorIndex = currentTurnColorIndex,
                                sizeDp = 36
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.studio_turn_banner, currentTurnPlayerName),
                                style = MaterialTheme.typography.titleSmall,
                                color = turnPalette.content,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Animated Word Card
                AnimatedContent(
                    targetState = session.currentBlankIndex,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { it / 3 } + fadeIn()) togetherWith
                                (slideOutHorizontally { -it / 3 } + fadeOut())
                        } else {
                            (slideInHorizontally { -it / 3 } + fadeIn()) togetherWith
                                (slideOutHorizontally { it / 3 } + fadeOut())
                        }
                    },
                    label = "blank_card_transition"
                ) { blankIndex ->
                    val blank = session.template.blanks[blankIndex]
                    val pos = blank.partOfSpeech
                    val posPalette = badgePaletteAt(pos.paletteIndex)
                    val currentText = session.enteredWords[blank.id].orEmpty()
                    val ideas = session.sparkIdeas[blank.id] ?: pos.suggestions.take(3)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.5.dp, posPalette.border.copy(alpha = 0.6f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            PlayfulPillBadge(
                                text = pos.label.uppercase(),
                                palette = posPalette,
                                icon = Icons.Rounded.AutoAwesome
                            )

                            Column {
                                Text(
                                    text = blank.customPrompt ?: "Give me a ${pos.label}!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = pos.hint,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = currentText,
                                    onValueChange = { onWordChanged(it.take(36)) },
                                    singleLine = true,
                                    placeholder = { Text(stringResource(R.string.studio_input_placeholder)) },
                                    shape = RoundedCornerShape(18.dp),
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.None,
                                        imeAction = if (session.isLastBlank) ImeAction.Done else ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { onNextOrReveal() },
                                        onDone = { onNextOrReveal() }
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("studio_word_input")
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                FilledTonalIconButton(
                                    onClick = onRollRandomWord,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .testTag("studio_dice_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Casino,
                                        contentDescription = stringResource(R.string.cd_dice),
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            // Spark Idea Chips
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = stringResource(R.string.studio_ideas_label),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    IconButton(
                                        onClick = onRefreshIdeas,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Refresh,
                                            contentDescription = stringResource(R.string.action_random_word),
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    ideas.forEach { idea ->
                                        SuggestionChip(
                                            onClick = { onWordChanged(idea) },
                                            label = {
                                                Text(
                                                    text = idea,
                                                    style = MaterialTheme.typography.labelLarge
                                                )
                                            },
                                            shape = RoundedCornerShape(50),
                                            modifier = Modifier.heightIn(min = 42.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (session.currentBlankIndex > 0) {
                        OutlinedButton(
                            onClick = onPreviousBlank,
                            modifier = Modifier
                                .weight(0.42f)
                                .heightIn(min = 54.dp)
                                .testTag("studio_prev_button"),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.action_previous))
                        }
                    }

                    Button(
                        onClick = onNextOrReveal,
                        modifier = Modifier
                            .weight(if (session.currentBlankIndex > 0) 0.58f else 1f)
                            .heightIn(min = 54.dp)
                            .testTag("studio_next_button"),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (session.isLastBlank) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    ) {
                        Text(
                            text = if (session.isLastBlank) {
                                stringResource(R.string.action_reveal_story)
                            } else {
                                stringResource(R.string.action_next_word)
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (session.isLastBlank) {
                                Icons.Rounded.AutoAwesome
                            } else {
                                Icons.AutoMirrored.Rounded.ArrowForward
                            },
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.dialog_exit_title)) },
            text = { Text(stringResource(R.string.dialog_exit_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onExitStudio()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.dialog_exit_leave),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                Button(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.dialog_exit_stay))
                }
            }
        )
    }
}
