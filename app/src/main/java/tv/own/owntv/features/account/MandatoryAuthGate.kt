package tv.own.owntv.features.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import tv.own.owntv.core.account.CloudAccountManager
import tv.own.owntv.core.account.CloudAuthResult
import tv.own.owntv.ui.components.BrandLockup
import tv.own.owntv.ui.components.HanTVButton
import tv.own.owntv.ui.components.HanTVTextField
import tv.own.owntv.ui.theme.HanTVTheme

@Composable
fun MandatoryAuthGate(
    modifier: Modifier = Modifier,
    accountManager: CloudAccountManager = koinInject(),
) {
    val colors = HanTVTheme.colors
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isRegister by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAuthLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val initialFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        runCatching { initialFocus.requestFocus() }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF020617),
                    )
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .width(520.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, colors.primary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .focusProperties { onEnter = { runCatching { initialFocus.requestFocus() } } }
                .focusGroup(),
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                BrandLockup(markSize = 44, textSize = 28)

                Text(
                    text = if (isRegister) "HanTV Üyeliği Oluşturun" else "HanTV Hesabınıza Giriş Yapın",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Uygulamayı kullanabilmek için lütfen giriş yapın veya ücretsiz üyelik oluşturun.\nÜyeliğiniz ile en fazla 3 cihaz bağlayabilirsiniz.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                }

                HanTVTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    label = "E-posta Adresi",
                    placeholder = "ör. ahmet@gmail.com",
                    focusRequester = emailFocus,
                    modifier = Modifier.focusRequester(initialFocus),
                )

                HanTVTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = "Şifre",
                    isPassword = true,
                    focusRequester = passwordFocus,
                )

                Spacer(Modifier.height(4.dp))

                HanTVButton(
                    label = if (isAuthLoading) "İşlem Yapılıyor..." else if (isRegister) "Kayıt Ol" else "Giriş Yap",
                    enabled = !isAuthLoading && email.isNotBlank() && password.isNotBlank(),
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Lütfen e-posta ve şifrenizi girin."
                            return@HanTVButton
                        }
                        isAuthLoading = true
                        errorMessage = null
                        scope.launch {
                            val res = if (isRegister) accountManager.register(email, password) else accountManager.login(email, password)
                            isAuthLoading = false
                            when (res) {
                                is CloudAuthResult.Success -> {
                                    Toast.makeText(context, "Hoş geldiniz!", Toast.LENGTH_SHORT).show()
                                }
                                is CloudAuthResult.Error -> {
                                    errorMessage = res.message
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                HanTVButton(
                    label = if (isRegister) "Zaten hesabınız var mı? Giriş Yapın" else "Hesabınız yok mu? Üye Olun",
                    onClick = {
                        isRegister = !isRegister
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
