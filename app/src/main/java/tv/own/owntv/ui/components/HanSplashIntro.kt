package tv.own.owntv.ui.components

import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tv.own.owntv.R

/**
 * High-performance 60fps cinematic startup splash intro for HanTV.
 * Optimized specifically for Android TV hardware (zero GPU blur overhead, graphicsLayer rendering).
 */
@Composable
fun HanSplashIntro(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    // GPU-accelerated animatables
    val logoScale = remember { Animatable(0.75f) }
    val logoAlpha = remember { Animatable(0f) }
    val shineOffset = remember { Animatable(-1.2f) }
    val glowExpansion = remember { Animatable(0.6f) }
    val glowAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val screenAlpha = remember { Animatable(1f) }

    // Asynchronous background audio playback (never blocks the UI thread)
    DisposableEffect(Unit) {
        var player: MediaPlayer? = null
        val job = scope.launch(Dispatchers.IO) {
            try {
                val mp = MediaPlayer.create(context, R.raw.han_intro)
                if (mp != null) {
                    player = mp
                    mp.setOnCompletionListener {
                        runCatching { it.release() }
                    }
                    mp.start()
                }
            } catch (_: Throwable) {
            }
        }

        onDispose {
            job.cancel()
            try {
                if (player?.isPlaying == true) {
                    player?.stop()
                }
                player?.release()
            } catch (_: Throwable) {
            }
        }
    }

    // Intercept back / remote clicks to fast-skip
    BackHandler {
        onSplashFinished()
    }

    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }

        // Phase 1: Logo & Glow Entrance
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            )
        }
        launch {
            glowAlpha.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            )
        }
        launch {
            logoScale.animateTo(
                targetValue = 1.04f,
                animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
            )
            logoScale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 350, easing = LinearEasing),
            )
        }
        launch {
            glowExpansion.animateTo(
                targetValue = 1.4f,
                animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            )
        }

        // Phase 2: Mirror Specular Light Sweep (Ayna Parlama Efekti)
        delay(250)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            )
        }
        launch {
            shineOffset.animateTo(
                targetValue = 2.0f,
                animationSpec = tween(durationMillis = 950, easing = FastOutSlowInEasing),
            )
        }

        // Phase 3: Hold & Smooth Fadeout
        delay(1200)
        screenAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        )

        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = screenAlpha.value
            }
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF162032), // Deep Luminous Slate
                        Color(0xFF0F1522), // Midnight
                        Color(0xFF070B10), // Base Obsidian
                    ),
                    radius = 1400f,
                ),
            )
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent {
                when (it.key) {
                    Key.DirectionCenter, Key.Enter, Key.NumPadEnter, Key.Back, Key.Escape -> {
                        onSplashFinished()
                        true
                    }
                    else -> false
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        // High-performance hardware shader glow (No expensive CPU/GPU blur pass)
        Box(
            modifier = Modifier
                .size(380.dp)
                .graphicsLayer {
                    scaleX = glowExpansion.value
                    scaleY = glowExpansion.value
                    alpha = glowAlpha.value
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x8000D2FF), // Neon Cyan core
                            Color(0x330077B6), // Deep Blue falloff
                            Color(0x000077B6), // Transparent boundary
                        ),
                    ),
                    shape = CircleShape,
                ),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            val squircleShape = RoundedCornerShape(36.dp)

            // Main Pure Logo with Hardware Mirror Shine Effect and Modern Squircle Contours
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .graphicsLayer {
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                        alpha = logoAlpha.value
                    }
                    .clip(squircleShape)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0x9900D2FF),
                                Color(0x330077B6),
                                Color(0x8000D2FF),
                            ),
                        ),
                        shape = squircleShape,
                    )
                    .drawWithContent {
                        drawContent()

                        // Specular Light Sweep / Mirror reflection
                        val progress = shineOffset.value
                        val width = size.width
                        val height = size.height
                        val startX = (progress - 0.45f) * width
                        val endX = (progress + 0.45f) * width

                        val shineBrush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x3300D2FF),
                                Color(0xE6FFFFFF), // Pure white mirror reflection
                                Color(0x6638BDF8), // Electric Cyan aura
                                Color.Transparent,
                            ),
                            start = Offset(startX, 0f),
                            end = Offset(endX, height),
                        )

                        drawRect(
                            brush = shineBrush,
                            size = Size(width, height),
                            blendMode = BlendMode.Screen,
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hantv_splash_logo),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

