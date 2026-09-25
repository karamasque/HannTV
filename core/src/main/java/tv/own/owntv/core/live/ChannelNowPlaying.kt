package tv.own.owntv.core.live

import androidx.compose.runtime.Immutable

/**
 * Now-playing EPG information for a channel row in lists and preview displays.
 */
@Immutable
data class ChannelNowPlaying(
    val title: String = "",
    val startMs: Long = 0L,
    val stopMs: Long = 0L,
) {
    val progressFraction: Float
        get() {
            if (startMs <= 0 || stopMs <= startMs) return 0f
            val now = System.currentTimeMillis()
            val totalMs = (stopMs - startMs).toFloat()
            if (now in startMs..stopMs) {
                return ((now - startMs).toFloat() / totalMs).coerceIn(0f, 1f)
            }
            return 0f
        }
}
