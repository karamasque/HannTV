package tv.own.owntv.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tv.own.owntv.ui.theme.HanTVTheme

/**
 * Generic focusable content card (poster, channel tile, etc.) built on [FocusableSurface].
 * Scales up a touch on focus for the classic "10-foot" pop. Real poster/channel cards in phases
 * 7–9 wrap this and add their own inner layout.
 */
@Composable
fun HanTVCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    content: @Composable BoxScope.(focused: Boolean) -> Unit,
) {
    FocusableSurface(
        onClick = onClick,
        modifier = modifier,
        selected = selected,
        shape = RoundedCornerShape(14.dp),
        focusedScale = 1.03f,
        glowElevation = 8,
        focusedContainerColor = HanTVTheme.colors.card,
        unfocusedContainerColor = HanTVTheme.colors.card,
        selectedContainerColor = HanTVTheme.colors.card,
        contentAlignment = Alignment.Center,
    ) { focused ->
        Box(modifier = Modifier) {
            content(focused)
        }
    }
}
