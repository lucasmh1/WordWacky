package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.audio.StoryNarrator
import com.example.data.StoryCatalog
import com.example.model.CompletedStory
import com.example.ui.screens.LaughBookScreen
import com.example.ui.screens.PartySetupScreen
import com.example.ui.screens.StoriesShelfScreen
import com.example.ui.screens.StoryRevealScreen
import com.example.ui.screens.WordStudioScreen
import com.example.ui.theme.WordWackyTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.HomeTab
import com.example.viewmodel.WordWackyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordWackyTheme {
                WordWackyApp()
            }
        }
    }
}

@Composable
fun WordWackyApp(
    viewModel: WordWackyViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val narrator = remember(context) { StoryNarrator(context) }
    val isNarratorReady by narrator.isReady.collectAsStateWithLifecycle()
    val isSpeaking by narrator.isSpeaking.collectAsStateWithLifecycle()

    DisposableEffect(narrator) {
        onDispose { narrator.shutdown() }
    }

    // Stop speaking if user navigates away from the Story Reveal screen
    LaunchedEffect(uiState.currentScreen) {
        if (uiState.currentScreen != AppScreen.StoryReveal) {
            narrator.stop()
        }
    }

    val shareStory: (CompletedStory) -> Unit = { completed ->
        shareCompletedStory(context, completed)
    }

    when (uiState.currentScreen) {
        is AppScreen.Home -> {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val useNavRail = maxWidth >= 680.dp

                if (useNavRail) {
                    Scaffold(
                        contentWindowInsets = WindowInsets.safeDrawing,
                        containerColor = MaterialTheme.colorScheme.background
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            NavigationRail(
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                HomeTab.entries.forEach { tab ->
                                    val selected = uiState.activeTab == tab
                                    NavigationRailItem(
                                        selected = selected,
                                        onClick = { viewModel.selectTab(tab) },
                                        icon = {
                                            TabIcon(
                                                tab = tab,
                                                selected = selected,
                                                laughCount = uiState.laughBook.size
                                            )
                                        },
                                        label = { Text(tabLabel(tab)) },
                                        modifier = Modifier.testTag("nav_rail_${tab.name.lowercase()}")
                                    )
                                }
                            }
                            HomeTabContent(
                                uiState = uiState,
                                viewModel = viewModel,
                                onShareStory = shareStory,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                } else {
                    Scaffold(
                        contentWindowInsets = WindowInsets.safeDrawing,
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                HomeTab.entries.forEach { tab ->
                                    val selected = uiState.activeTab == tab
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { viewModel.selectTab(tab) },
                                        icon = {
                                            TabIcon(
                                                tab = tab,
                                                selected = selected,
                                                laughCount = uiState.laughBook.size
                                            )
                                        },
                                        label = { Text(tabLabel(tab)) },
                                        modifier = Modifier.testTag("nav_bar_${tab.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        HomeTabContent(
                            uiState = uiState,
                            viewModel = viewModel,
                            onShareStory = shareStory,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }
            }
        }

        is AppScreen.WordStudio -> {
            val session = uiState.studioSession
            if (session != null) {
                WordStudioScreen(
                    session = session,
                    currentTurnPlayerName = viewModel.getPlayerNameForBlankIndex(session.currentBlankIndex),
                    currentTurnColorIndex = viewModel.getPlayerColorForBlankIndex(session.currentBlankIndex),
                    onWordChanged = viewModel::updateCurrentWord,
                    onRollRandomWord = viewModel::fillCurrentWithRandomWord,
                    onRefreshIdeas = viewModel::refreshSparkIdeasForCurrentBlank,
                    onJumpToBlank = viewModel::jumpToBlank,
                    onPreviousBlank = viewModel::goToPreviousBlank,
                    onNextOrReveal = viewModel::advanceOrFinishStudio,
                    onAutoFillRest = viewModel::autoFillRemainingAndReveal,
                    onExitStudio = { viewModel.returnHome() }
                )
            } else {
                viewModel.returnHome()
            }
        }

        is AppScreen.StoryReveal -> {
            val story = uiState.activeStory
            if (story != null) {
                StoryRevealScreen(
                    story = story,
                    isDramaticMode = uiState.isDramaticRevealMode,
                    revealedBlankIds = uiState.revealedBlankIds,
                    confettiTriggerCount = uiState.confettiTriggerCount,
                    selectedVoiceStyle = uiState.selectedVoiceStyle,
                    isNarratorReady = isNarratorReady,
                    isSpeaking = isSpeaking,
                    onBackHome = {
                        narrator.stop()
                        viewModel.returnHome()
                    },
                    onToggleDramaticMode = viewModel::toggleDramaticRevealMode,
                    onRevealBlank = viewModel::revealSingleBlank,
                    onRevealAll = viewModel::revealAllBlanks,
                    onUpdateWord = viewModel::updateWordInActiveStory,
                    onRateStory = viewModel::rateActiveStory,
                    onSelectVoiceStyle = { style ->
                        viewModel.setVoiceStyle(style)
                        if (isSpeaking) {
                            narrator.speak(StoryCatalog.renderSpokenText(story), style)
                        }
                    },
                    onToggleReadAloud = {
                        if (isSpeaking) {
                            narrator.stop()
                        } else {
                            viewModel.revealAllBlanks()
                            narrator.speak(
                                StoryCatalog.renderSpokenText(story),
                                uiState.selectedVoiceStyle
                            )
                        }
                    },
                    onShareStory = shareStory,
                    onRemixSameStory = {
                        narrator.stop()
                        viewModel.startStory(story.template)
                    },
                    onNextRandomStory = {
                        narrator.stop()
                        viewModel.startRandomStory(excludeId = story.template.id)
                    }
                )
            } else {
                viewModel.returnHome()
            }
        }
    }
}

@Composable
private fun HomeTabContent(
    uiState: com.example.viewmodel.WordWackyUiState,
    viewModel: WordWackyViewModel,
    onShareStory: (CompletedStory) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState.activeTab) {
        HomeTab.STORIES -> StoriesShelfScreen(
            uiState = uiState,
            onCategorySelected = viewModel::selectCategory,
            onStoryClicked = { viewModel.startStory(it) },
            onSurpriseMeClicked = { viewModel.startRandomStory() },
            onOpenPartyTab = { viewModel.selectTab(HomeTab.PARTY) },
            modifier = modifier
        )

        HomeTab.PARTY -> PartySetupScreen(
            uiState = uiState,
            onTogglePartyMode = viewModel::setPartyModeEnabled,
            onUpdatePlayerName = viewModel::updatePartyPlayerName,
            onAddPlayer = viewModel::addPartyPlayer,
            onRemovePlayer = viewModel::removePartyPlayer,
            onLaunchStory = { template -> viewModel.startStory(template, forcePartyMode = true) },
            modifier = modifier
        )

        HomeTab.LAUGH_BOOK -> LaughBookScreen(
            uiState = uiState,
            onOpenStory = viewModel::openCompletedStory,
            onShareStory = onShareStory,
            onDeleteStory = viewModel::deleteCompletedStory,
            onClearAll = viewModel::clearLaughBook,
            onGoToStories = { viewModel.selectTab(HomeTab.STORIES) },
            modifier = modifier
        )
    }
}

@Composable
private fun tabLabel(tab: HomeTab): String = when (tab) {
    HomeTab.STORIES -> stringResource(R.string.nav_stories)
    HomeTab.PARTY -> stringResource(R.string.nav_party)
    HomeTab.LAUGH_BOOK -> stringResource(R.string.nav_laugh_book)
}

@Composable
private fun TabIcon(
    tab: HomeTab,
    selected: Boolean,
    laughCount: Int
) {
    val icon = when (tab) {
        HomeTab.STORIES -> if (selected) Icons.Rounded.AutoStories else Icons.Outlined.AutoStories
        HomeTab.PARTY -> if (selected) Icons.Rounded.Groups else Icons.Outlined.Groups
        HomeTab.LAUGH_BOOK -> if (selected) Icons.Rounded.MenuBook else Icons.Outlined.MenuBook
    }
    if (tab == HomeTab.LAUGH_BOOK && laughCount > 0) {
        BadgedBox(
            badge = {
                Badge { Text(text = laughCount.coerceAtMost(99).toString()) }
            }
        ) {
            Icon(imageVector = icon, contentDescription = null)
        }
    } else {
        Icon(imageVector = icon, contentDescription = null)
    }
}

private fun shareCompletedStory(context: Context, story: CompletedStory) {
    val text = StoryCatalog.renderPlainText(story)
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, story.template.title)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(sendIntent, context.getString(R.string.share_chooser_title))
    context.startActivity(chooser)
}
