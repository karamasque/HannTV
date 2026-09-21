package tv.own.owntv.ui.theme

import androidx.compose.ui.graphics.Color
import tv.own.owntv.core.theme.AccentColor
import tv.own.owntv.core.theme.HanTVPalette

/**
 * Material 3 tonal palette for HanTV (teal-seeded). NEUTRAL + secondary/tertiary roles are
 * theme-only; the `primary` roles are seeded per [AccentColor] (default teal == these values).
 *
 * Dark uses a near-black background (#040e0b) so the panel colours (Phase 6) pop against
 * the deep dark surface while keeping a subtle green undertone.
 *
 * **The values themselves live in core**, in [HanTVPalette], because the mobile app renders the
 * same product and a second copy of the hex codes would drift. This file only wraps them in
 * Compose's [Color], which core cannot do — it carries the Compose runtime, not `compose-ui`.
 */

// Brand mark color (the HanTV play logo) — constant.
val AccentCyan = Color(HanTVPalette.AccentCyan)

// ---------------- DARK (M3 dark over near-black #040e0b) ----------------
val DarkBackground = Color(HanTVPalette.DarkBackground) // Option A — nav + inter-panel gap surface
val DarkSurface = Color(HanTVPalette.DarkSurface)
val DarkSurfaceContainerLowest = Color(HanTVPalette.DarkSurfaceContainerLowest)
val DarkSurfaceContainerLow = Color(HanTVPalette.DarkSurfaceContainerLow)
val DarkSurfaceContainer = Color(HanTVPalette.DarkSurfaceContainer)
val DarkSurfaceContainerHigh = Color(HanTVPalette.DarkSurfaceContainerHigh)
val DarkSurfaceContainerHighest = Color(HanTVPalette.DarkSurfaceContainerHighest)
val DarkOnSurface = Color(HanTVPalette.DarkOnSurface)
val DarkOnSurfaceVariant = Color(HanTVPalette.DarkOnSurfaceVariant)
val DarkOutline = Color(HanTVPalette.DarkOutline)
val DarkOutlineVariant = Color(HanTVPalette.DarkOutlineVariant)
val DarkSecondary = Color(HanTVPalette.DarkSecondary)
val DarkOnSecondary = Color(HanTVPalette.DarkOnSecondary)
val DarkSecondaryContainer = Color(HanTVPalette.DarkSecondaryContainer)
val DarkOnSecondaryContainer = Color(HanTVPalette.DarkOnSecondaryContainer)
val DarkTertiary = Color(HanTVPalette.DarkTertiary)
val DarkOnTertiary = Color(HanTVPalette.DarkOnTertiary)
val DarkTertiaryContainer = Color(HanTVPalette.DarkTertiaryContainer)
val DarkOnTertiaryContainer = Color(HanTVPalette.DarkOnTertiaryContainer)
val DarkError = Color(HanTVPalette.DarkError)

// ---------------- LIGHT (M3 light) ----------------
val LightBackground = Color(HanTVPalette.LightBackground)
val LightSurface = Color(HanTVPalette.LightSurface)
val LightSurfaceContainerLowest = Color(HanTVPalette.LightSurfaceContainerLowest)
val LightSurfaceContainerLow = Color(HanTVPalette.LightSurfaceContainerLow)
val LightSurfaceContainer = Color(HanTVPalette.LightSurfaceContainer)
val LightSurfaceContainerHigh = Color(HanTVPalette.LightSurfaceContainerHigh)
val LightSurfaceContainerHighest = Color(HanTVPalette.LightSurfaceContainerHighest)
val LightOnSurface = Color(HanTVPalette.LightOnSurface)
val LightOnSurfaceVariant = Color(HanTVPalette.LightOnSurfaceVariant)
val LightOutline = Color(HanTVPalette.LightOutline)
val LightOutlineVariant = Color(HanTVPalette.LightOutlineVariant)
val LightSecondary = Color(HanTVPalette.LightSecondary)
val LightOnSecondary = Color(HanTVPalette.LightOnSecondary)
val LightSecondaryContainer = Color(HanTVPalette.LightSecondaryContainer)
val LightOnSecondaryContainer = Color(HanTVPalette.LightOnSecondaryContainer)
val LightTertiary = Color(HanTVPalette.LightTertiary)
val LightOnTertiary = Color(HanTVPalette.LightOnTertiary)
val LightTertiaryContainer = Color(HanTVPalette.LightTertiaryContainer)
val LightOnTertiaryContainer = Color(HanTVPalette.LightOnTertiaryContainer)
val LightError = Color(HanTVPalette.LightError)
