package tv.own.owntv.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import tv.own.owntv.R
import tv.own.owntv.core.settings.SettingsRepository
import tv.own.owntv.core.theme.GlassSurface
import tv.own.owntv.core.theme.HanTVThemePresets
import tv.own.owntv.ui.theme.HanTVTheme

/**
 * UI Theme preset chooser dialog — allows choosing an interface theme preset (wallpaper + glass accent)
 * directly from Settings or setup flow.
 */
@Composable
fun ThemePresetChooserDialog(
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settingsRepo: SettingsRepository = koinInject()
    val bgImagePath by settingsRepo.bgImagePath.collectAsStateWithLifecycle("")
    val activePreset = remember(bgImagePath) {
        HanTVThemePresets.ALL.firstOrNull { bgImagePath.contains(it.id.name.lowercase()) } ?: HanTVThemePresets.ALL.first()
    }
    val firstFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { firstFocus.requestFocus() } }
    BackHandler { onDismiss() }

    Box(
        Modifier.fillMaxSize()
            .modalScrim()
            .trapAllFocusExit()
            .focusGroup(),
        contentAlignment = Alignment.Center,
    ) {
        Column(Modifier.dialogPanel(width = 680.dp, padding = 24.dp)) {
            Text(stringResource(R.string.theme_setup_title), style = MaterialTheme.typography.titleLarge, color = HanTVTheme.colors.onSurface)
            Spacer(Modifier.height(6.dp))
            Text(
                stringResource(R.string.theme_setup_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = HanTVTheme.colors.onSurfaceVariant,
            )
            Spacer(Modifier.height(18.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth().height(165.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(HanTVThemePresets.ALL, key = { it.id }) { preset ->
                    val isSelected = preset.id == activePreset.id
                    val imageRequest = remember(preset.wallpaperResId) {
                        ImageRequest.Builder(context)
                            .data(preset.wallpaperResId)
                            .size(240, 160)
                            .crossfade(true)
                            .build()
                    }

                    FocusableSurface(
                        onClick = {
                            scope.launch(Dispatchers.IO) { preset.applyTheme(context, settingsRepo) }
                        },
                        selected = isSelected,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.width(170.dp).height(150.dp).then(if (preset.id == activePreset.id) Modifier.focusRequester(firstFocus) else Modifier),
                        surface = GlassSurface.CARDS,
                    ) { _ ->
                        Box(Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = imageRequest,
                                contentDescription = stringResource(preset.titleRes),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                        ),
                                    ),
                            )
                            Column(
                                modifier = Modifier.align(Alignment.BottomStart).padding(10.dp),
                            ) {
                                Text(
                                    stringResource(preset.titleRes),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                                Text(
                                    stringResource(preset.subtitleRes),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(HanTVTheme.colors.primary),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    HanTVIcon(HanTVIcon.SPARKLE, tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                HanTVButton(stringResource(R.string.settings_close), onClick = onDismiss)
            }
        }
    }
}
