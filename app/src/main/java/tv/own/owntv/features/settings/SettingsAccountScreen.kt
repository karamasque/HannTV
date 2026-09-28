package tv.own.owntv.features.settings

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.own.owntv.R
import tv.own.owntv.core.account.CloudAccountManager
import tv.own.owntv.core.account.CloudAuthResult
import tv.own.owntv.core.account.CloudSyncEngine
import tv.own.owntv.ui.components.HanTVButton
import tv.own.owntv.ui.components.HanTVButtonStyle
import tv.own.owntv.ui.components.HanTVIcon
import tv.own.owntv.ui.components.HanTVTextField
import tv.own.owntv.ui.components.roundedPanel
import tv.own.owntv.ui.theme.HanTVTheme

@Composable
fun SettingsAccountScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    accountManager: CloudAccountManager = org.koin.compose.koinInject(),
    syncEngine: CloudSyncEngine = org.koin.compose.koinInject(),
) {
    val colors = HanTVTheme.colors
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentUser by accountManager.currentUser.collectAsStateWithLifecycle()
    val activeDevices by accountManager.activeDevices.collectAsStateWithLifecycle()

    var isRegister by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAuthLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSyncing by remember { mutableStateOf(false) }

    val initialFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    val submitFocus = remember { FocusRequester() }
    val switchModeFocus = remember { FocusRequester() }
    val syncFocus = remember { FocusRequester() }
    val logoutFocus = remember { FocusRequester() }

    LaunchedEffect(currentUser) {
        currentUser?.let { u ->
            accountManager.fetchDevices(u.uid)
            syncEngine.syncAll()
        }
        kotlinx.coroutines.delay(60)
        runCatching { initialFocus.requestFocus() }
    }

    BackHandler { onBack() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .roundedPanel()
            .focusProperties { onEnter = { runCatching { initialFocus.requestFocus() } } }
            .focusGroup()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 40.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Header("HanTV Üyelik & Bulut Hesabı", onBack)
        Spacer(Modifier.height(4.dp))

        if (currentUser == null) {
            GroupLabel(if (isRegister) "Yeni Üyelik Oluştur" else "Bulut Hesabınıza Giriş Yapın")

            Text(
                text = "Üyeliğiniz ile en fazla 1 cihaz bağlayabilir (Premium'da 3 Cihaz), IPTV listelerinizi ve izleme sürelerinizi senkronize edebilirsiniz.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )

            Text(
                text = "🌐 Web Paneli: https://www.hantv.com.tr",
                style = MaterialTheme.typography.bodySmall,
                color = colors.primary,
                fontWeight = FontWeight.Bold,
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFEF4444),
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(Modifier.height(4.dp))

            HanTVTextField(
                value = email,
                onValueChange = { email = it },
                label = "E-posta Adresi",
                placeholder = "ör. ahmet@gmail.com",
                focusRequester = emailFocus,
                modifier = Modifier.focusRequester(initialFocus),
            )

            HanTVTextField(
                value = password,
                onValueChange = { password = it },
                label = "Şifre",
                isPassword = true,
                focusRequester = passwordFocus,
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HanTVButton(
                    label = if (isAuthLoading) "İşlem Yapılıyor..." else if (isRegister) "Üye Ol" else "Giriş Yap",
                    style = HanTVButtonStyle.PRIMARY,
                    icon = HanTVIcon.PERSON,
                    modifier = Modifier.focusRequester(submitFocus),
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Lütfen e-posta ve şifrenizi girin."
                            return@HanTVButton
                        }
                        scope.launch {
                            isAuthLoading = true
                            errorMessage = null
                            val res = if (isRegister) {
                                accountManager.register(email.trim(), password.trim())
                            } else {
                                accountManager.login(email.trim(), password.trim())
                            }
                            isAuthLoading = false
                            when (res) {
                                is CloudAuthResult.Success -> {
                                    Toast.makeText(context, "Giriş başarılı!", Toast.LENGTH_SHORT).show()
                                    syncEngine.syncAll()
                                }
                                is CloudAuthResult.Error -> {
                                    errorMessage = res.message
                                }
                            }
                        }
                    },
                )

                HanTVButton(
                    label = if (isRegister) "Zaten hesabınız var mı? Giriş Yapın" else "Hesabınız yok mu? Üye Olun",
                    style = HanTVButtonStyle.SECONDARY,
                    modifier = Modifier.focusRequester(switchModeFocus),
                    onClick = {
                        isRegister = !isRegister
                        errorMessage = null
                    },
                )
            }
        } else {
            GroupLabel("Hesap & Üyelik Durumu")

            Row2(
                icon = HanTVIcon.PERSON,
                title = if (currentUser?.isPremium == true) "⭐ Premium Üyelik" else "🆓 Ücretsiz Üyelik",
                desc = "${currentUser?.email} (ID: ${currentUser?.uid?.take(12)}...)",
                chip = "${activeDevices.size}/${if (currentUser?.isPremium == true) "3 Cihaz" else "1 Cihaz"}",
                primaryChip = true,
                onClick = {},
                modifier = Modifier.focusRequester(initialFocus),
            )

            Row2(
                icon = HanTVIcon.INFO,
                title = "HanTV Bulut Web Paneli",
                desc = "https://www.hantv.com.tr",
                chip = "Web Paneli",
                primaryChip = false,
                onClick = {},
            )

            Spacer(Modifier.height(8.dp))

            GroupLabel("Bulut Senkronizasyonu")

            HanTVButton(
                label = if (isSyncing) "Senkronize Ediliyor..." else "Şimdi Buluttan Senkronize Et",
                icon = HanTVIcon.REFRESH,
                style = HanTVButtonStyle.PRIMARY,
                modifier = Modifier.focusRequester(syncFocus),
                onClick = {
                    scope.launch {
                        isSyncing = true
                        val ok = syncEngine.syncAll()
                        isSyncing = false
                        if (ok) {
                            Toast.makeText(context, "Senkronizasyon tamamlandı!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Senkronizasyon başarısız oldu.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
            )

            Spacer(Modifier.height(8.dp))

            GroupLabel("Aktif Bağlı Cihazlarınız (${activeDevices.size}/${if (currentUser?.isPremium == true) "3 Cihaz Sınırı" else "1 Cihaz Sınırı"})")

            if (activeDevices.isEmpty()) {
                Text(
                    text = "Bağlı cihaz bulunamadı.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                )
            } else {
                activeDevices.forEach { dev ->
                    val isThisDevice = dev.id == accountManager.deviceId
                    Row2(
                        icon = HanTVIcon.INFO,
                        title = dev.name + if (isThisDevice) " (Bu Cihaz)" else "",
                        desc = "Tür: ${dev.platform.uppercase()} • Son aktif: ${dev.lastActive}",
                        chip = if (isThisDevice) "Bu Cihaz" else "Bağlı",
                        primaryChip = isThisDevice,
                        onClick = {},
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            GroupLabel("📋 Üyelik Paket Bilgileri & Kuralları")

            Text(
                text = "• Ücretsiz Üyelik: Maksimum 1 Cihaz sınırı. Sadece yerel cihazınızda kullanım imkanı sağlar.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )

            Text(
                text = "• Premium Üyelik: Maksimum 3 Cihaz bağlama hakkı. Web paneli (https://www.hantv.com.tr) üzerinden otomatik bulut IPTV ve izlemeye devam et senkronizasyonu sunar.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )

            Spacer(Modifier.height(12.dp))

            HanTVButton(
                label = "Hesaptan Çıkış Yap",
                icon = HanTVIcon.POWER,
                style = HanTVButtonStyle.SECONDARY,
                modifier = Modifier.focusRequester(logoutFocus),
                onClick = {
                    scope.launch {
                        accountManager.logout()
                        Toast.makeText(context, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
                    }
                },
            )
        }
    }
}
