/**
 * HanTV Authentication & Dashboard Logic
 * Supports real Firebase Auth/Firestore and Fallback Local Demo Mode.
 */

// Toast Message Utility
function showToast(message, type = "success") {
  let container = document.getElementById("toastContainer");
  if (!container) {
    container = document.createElement("div");
    container.id = "toastContainer";
    container.className = "toast-container";
    document.body.appendChild(container);
  }

  const toast = document.createElement("div");
  toast.className = `toast-item toast-${type}`;
  toast.innerHTML = `
    <span class="toast-icon">${type === "success" ? "✓" : type === "error" ? "✕" : "ℹ"}</span>
    <span class="toast-msg">${message}</span>
  `;

  container.appendChild(toast);
  setTimeout(() => {
    toast.classList.add("show");
  }, 10);

  setTimeout(() => {
    toast.classList.remove("show");
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

// Current App State
let currentUser = null;
let userPlaylists = [];
let userDevices = [];

// Local Storage Keys
const LOCAL_USER_KEY = "hantv_local_user";
const LOCAL_PLAYLISTS_KEY = "hantv_local_playlists";
const LOCAL_DEVICES_KEY = "hantv_local_devices";

// Init Auth Listener
function initAuth() {
  if (isFirebaseConfigured && firebaseAuth) {
    firebaseAuth.onAuthStateChanged((user) => {
      if (user) {
        currentUser = {
          uid: user.uid,
          email: user.email,
          displayName: user.displayName || user.email.split("@")[0]
        };
        onUserLoggedIn();
        loadFirestoreData(user.uid);
      } else {
        currentUser = null;
        onUserLoggedOut();
      }
    });
  } else {
    // Local / Demo Mode Check
    const saved = localStorage.getItem(LOCAL_USER_KEY);
    if (saved) {
      try {
        currentUser = JSON.parse(saved);
        onUserLoggedIn();
        loadLocalData();
      } catch (e) {
        currentUser = null;
        onUserLoggedOut();
      }
    } else {
      onUserLoggedOut();
    }
  }
}

// UI State Updates
function onUserLoggedIn() {
  const authBtns = document.getElementById("headerAuthBtns");
  const userMenu = document.getElementById("headerUserMenu");
  const userNameEl = document.getElementById("headerUserName");

  if (authBtns && userMenu) {
    authBtns.style.display = "none";
    userMenu.style.display = "flex";
    if (userNameEl) {
      userNameEl.textContent = currentUser.displayName || currentUser.email;
    }
  }

  // Update modal user headers
  const dashEmailEl = document.getElementById("dashUserEmail");
  if (dashEmailEl) {
    dashEmailEl.textContent = currentUser.email;
  }
}

function onUserLoggedOut() {
  const authBtns = document.getElementById("headerAuthBtns");
  const userMenu = document.getElementById("headerUserMenu");
  if (authBtns && userMenu) {
    authBtns.style.display = "flex";
    userMenu.style.display = "none";
  }
  closeDashboard();
}

// Open / Close Modals
function openAuthModal(tab = "login") {
  const modal = document.getElementById("authModal");
  if (modal) {
    modal.classList.add("active");
    document.body.style.overflow = "hidden";
    switchAuthTab(tab);
  }
}

function closeAuthModal() {
  const modal = document.getElementById("authModal");
  if (modal) {
    modal.classList.remove("active");
    document.body.style.overflow = "";
  }
}

function switchAuthTab(tab) {
  const loginTab = document.getElementById("tabLogin");
  const signupTab = document.getElementById("tabSignup");
  const loginForm = document.getElementById("formLogin");
  const signupForm = document.getElementById("formSignup");

  if (tab === "login") {
    loginTab.classList.add("active");
    signupTab.classList.remove("active");
    loginForm.style.display = "block";
    signupForm.style.display = "none";
  } else {
    signupTab.classList.add("active");
    loginTab.classList.remove("active");
    signupForm.style.display = "block";
    loginForm.style.display = "none";
  }
}

function openDashboard(section = "playlists") {
  if (!currentUser) {
    openAuthModal("login");
    return;
  }
  const modal = document.getElementById("dashboardModal");
  if (modal) {
    modal.classList.add("active");
    document.body.style.overflow = "hidden";
    switchDashSection(section);
    renderPlaylists();
    renderDevices();
  }
}

function closeDashboard() {
  const modal = document.getElementById("dashboardModal");
  if (modal) {
    modal.classList.remove("active");
    document.body.style.overflow = "";
  }
}

function switchDashSection(section) {
  document.querySelectorAll(".dash-nav-btn").forEach(btn => {
    btn.classList.toggle("active", btn.dataset.section === section);
  });
  document.querySelectorAll(".dash-pane").forEach(pane => {
    pane.classList.toggle("active", pane.id === `dashPane_${section}`);
  });
}

// Auth Actions
async function handleLogin(e) {
  e.preventDefault();
  const email = document.getElementById("loginEmail").value.trim();
  const pass = document.getElementById("loginPassword").value;

  if (!email || !pass) {
    showToast("Lütfen e-posta ve şifrenizi girin.", "error");
    return;
  }

  if (isFirebaseConfigured && firebaseAuth) {
    try {
      await firebaseAuth.signInWithEmailAndPassword(email, pass);
      showToast("Giriş başarılı! Hoş geldiniz.");
      closeAuthModal();
      openDashboard();
    } catch (err) {
      showToast(getFirebaseErrorMessage(err.code), "error");
    }
  } else {
    // Local Demo Login
    currentUser = {
      uid: "demo_" + btoa(email).substring(0, 10),
      email: email,
      displayName: email.split("@")[0]
    };
    localStorage.setItem(LOCAL_USER_KEY, JSON.stringify(currentUser));
    onUserLoggedIn();
    loadLocalData();
    showToast("Giriş başarılı (Demo Modu). Hoş geldiniz!");
    closeAuthModal();
    openDashboard();
  }
}

async function handleSignup(e) {
  e.preventDefault();
  const name = document.getElementById("signupName").value.trim();
  const email = document.getElementById("signupEmail").value.trim();
  const pass = document.getElementById("signupPassword").value;
  const pass2 = document.getElementById("signupPasswordConfirm").value;

  if (pass !== pass2) {
    showToast("Girdiğiniz şifreler birbiriyle eşleşmiyor!", "error");
    return;
  }

  if (pass.length < 6) {
    showToast("Şifreniz en az 6 karakter olmalıdır.", "error");
    return;
  }

  if (isFirebaseConfigured && firebaseAuth) {
    try {
      const res = await firebaseAuth.createUserWithEmailAndPassword(email, pass);
      if (name && res.user) {
        await res.user.updateProfile({ displayName: name });
      }
      showToast("Hesabınız başarıyla oluşturuldu!");
      closeAuthModal();
      openDashboard();
    } catch (err) {
      showToast(getFirebaseErrorMessage(err.code), "error");
    }
  } else {
    // Local Demo Signup
    currentUser = {
      uid: "demo_" + Date.now(),
      email: email,
      displayName: name || email.split("@")[0]
    };
    localStorage.setItem(LOCAL_USER_KEY, JSON.stringify(currentUser));
    onUserLoggedIn();
    loadLocalData();
    showToast("Hesap oluşturuldu (Demo Modu). Hoş geldiniz!");
    closeAuthModal();
    openDashboard();
  }
}

async function handleLogout() {
  if (isFirebaseConfigured && firebaseAuth) {
    await firebaseAuth.signOut();
  } else {
    localStorage.removeItem(LOCAL_USER_KEY);
    currentUser = null;
    onUserLoggedOut();
  }
  showToast("Oturum kapatıldı.");
}

// Data Handling (Playlists & Devices)
function loadLocalData() {
  try {
    userPlaylists = JSON.parse(localStorage.getItem(LOCAL_PLAYLISTS_KEY)) || [
      {
        id: "demo_p1",
        title: "Ev Eğlencesi (Örnek Liste)",
        type: "xtream",
        server: "http://example-iptv.com:8080",
        username: "user_demo",
        createdAt: new Date().toLocaleDateString("tr-TR")
      }
    ];
    userDevices = JSON.parse(localStorage.getItem(LOCAL_DEVICES_KEY)) || [
      {
        id: "demo_d1",
        name: "Oturma Odası Sony Android TV",
        pairedAt: new Date().toLocaleDateString("tr-TR"),
        lastActive: "Bugün"
      }
    ];
  } catch (e) {
    userPlaylists = [];
    userDevices = [];
  }
  renderPlaylists();
  renderDevices();
}

async function loadFirestoreData(uid) {
  if (!firestoreDb) return;
  try {
    // Load Playlists
    const pSnap = await firestoreDb.collection("users").doc(uid).collection("playlists").get();
    userPlaylists = [];
    pSnap.forEach(doc => {
      userPlaylists.push({ id: doc.id, ...doc.data() });
    });
    renderPlaylists();

    // Load Devices
    const dSnap = await firestoreDb.collection("users").doc(uid).collection("devices").get();
    userDevices = [];
    dSnap.forEach(doc => {
      userDevices.push({ id: doc.id, ...doc.data() });
    });
    renderDevices();
  } catch (err) {
    console.error("Firestore veri yükleme hatası:", err);
  }
}

// Smart Source Analyzer for Web Client
function analyzeSmartText(input) {
  if (!input || !input.trim()) return null;
  const text = input.trim();

  // 1. Query Params (get.php?username=...&password=... or player_api.php)
  const getPhpMatch = text.match(/https?:\/\/[^\s"'<>\n\r]+/i);
  if (getPhpMatch) {
    const urlStr = getPhpMatch[0];
    try {
      const urlObj = new URL(urlStr);
      const params = urlObj.searchParams;
      const user = params.get("username") || params.get("user") || params.get("usr") || params.get("u");
      const pass = params.get("password") || params.get("pass") || params.get("pwd") || params.get("p");
      if (user && pass) {
        const server = `${urlObj.protocol}//${urlObj.host}`;
        return {
          type: "xtream",
          server: server,
          username: user,
          password: pass,
          url: urlStr,
          suggestedTitle: (urlObj.hostname.split(".")[0] || user).toUpperCase() + " Xtream"
        };
      }
    } catch (e) {}

    // Path based (/live/user/pass or /movie/user/pass or /series/user/pass)
    const pathMatch = urlStr.match(/^(https?:\/\/[^\/]+)\/(?:live|movie|series|vod|playlist|timeshift|hls)\/([^\/]+)\/([^\/]+)/i);
    if (pathMatch && !pathMatch[2].includes("=") && !pathMatch[3].includes("=")) {
      return {
        type: "xtream",
        server: pathMatch[1],
        username: pathMatch[2],
        password: pathMatch[3],
        url: urlStr,
        suggestedTitle: pathMatch[2] + " Xtream"
      };
    }
  }

  // 2. Stalker (MAC + Portal)
  const macMatch = text.match(/([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})/);
  const portalMatch = text.match(/https?:\/\/[^\s"'<>\n\r]+/i);
  if (macMatch && portalMatch) {
    return {
      type: "stalker",
      portalUrl: portalMatch[0],
      mac: macMatch[0].toUpperCase(),
      suggestedTitle: "Stalker Portal"
    };
  }

  // 3. Multiline labeled text (Server: ... User: ... Pass: ...)
  const lines = text.split(/[\r\n]+/);
  let server = "", user = "", pass = "", portalUrl = "", mac = "";
  for (const line of lines) {
    const parts = line.split(/[:=]/);
    if (parts.length >= 2) {
      const key = parts[0].trim().toLowerCase();
      const val = line.substring(line.indexOf(parts[0]) + parts[0].length + 1).trim();
      if ((key.includes("sunucu") || key.includes("server") || key.includes("url") || key.includes("host") || key.includes("link")) && val.startsWith("http")) {
        server = val.replace(/\/+$/, "");
      } else if (key.includes("kullanici") || key.includes("user") || key.includes("username") || key.includes("hesap")) {
        user = val;
      } else if (key.includes("sifre") || key.includes("şifre") || key.includes("pass") || key.includes("password") || key.includes("parola")) {
        pass = val;
      } else if (key.includes("mac")) {
        mac = val;
      } else if (key.includes("portal")) {
        portalUrl = val;
      }
    }
  }

  if (server && user && pass) {
    return {
      type: "xtream",
      server: server,
      username: user,
      password: pass,
      suggestedTitle: user + " Xtream"
    };
  }

  if (mac && portalUrl) {
    return {
      type: "stalker",
      portalUrl: portalUrl,
      mac: mac,
      suggestedTitle: "Stalker Portal"
    };
  }

  // 4. Pure M3U URL
  if (text.startsWith("http://") || text.startsWith("https://")) {
    return {
      type: "m3u",
      url: text,
      suggestedTitle: "M3U Playlist"
    };
  }

  return null;
}

// Method selection
function selectPlaylistMethod(method) {
  document.getElementById("selectedPlMethod").value = method;
  const methods = ["m3u_url", "m3u_file", "xtream", "stalker", "m3u_raw", "auto"];

  methods.forEach(m => {
    const card = document.getElementById("methodCard_" + m);
    const sec = document.getElementById("methodSection_" + m);
    if (card) card.classList.toggle("active", m === method);
    if (sec) sec.style.display = m === method ? "block" : "none";
  });
}

let autoDetectedData = null;

function handleAutoDetectInput(value) {
  const badge = document.getElementById("autoDetectBadge");
  const badgeText = document.getElementById("autoDetectBadgeText");
  const titleInput = document.getElementById("plTitle");

  autoDetectedData = analyzeSmartText(value);

  if (autoDetectedData) {
    badge.style.display = "flex";
    if (autoDetectedData.type === "xtream") {
      badgeText.innerHTML = `<strong>Xtream Codes Algılandı:</strong> Sunucu: <code>${autoDetectedData.server}</code> | Kullanıcı: <code>${autoDetectedData.username}</code>`;
      if (!titleInput.value) titleInput.value = autoDetectedData.suggestedTitle;
    } else if (autoDetectedData.type === "stalker") {
      badgeText.innerHTML = `<strong>Stalker Portalı Algılandı:</strong> MAC: <code>${autoDetectedData.mac}</code>`;
      if (!titleInput.value) titleInput.value = autoDetectedData.suggestedTitle;
    } else if (autoDetectedData.type === "m3u") {
      badgeText.innerHTML = `<strong>M3U Bağlantısı Algılandı:</strong> <code>${autoDetectedData.url.substring(0, 45)}...</code>`;
      if (!titleInput.value) titleInput.value = autoDetectedData.suggestedTitle;
    }
  } else {
    badge.style.display = "none";
  }
}

function clearAddPlaylistForm() {
  document.getElementById("formAddPlaylist").reset();
  document.getElementById("autoDetectInput").value = "";
  document.getElementById("autoDetectBadge").style.display = "none";
  autoDetectedData = null;
}

function handleM3uFilePicked(e) {
  const file = e.target.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = function(evt) {
    document.getElementById("plRawText").value = evt.target.result;
    const titleInput = document.getElementById("plTitle");
    if (!titleInput.value) titleInput.value = file.name.replace(/\.[^/.]+$/, "");
    showToast("📁 M3U dosyası yüklendi: " + file.name);
  };
  reader.readAsText(file);
}

// Save Playlist
async function handleSavePlaylist(e) {
  e.preventDefault();
  const method = document.getElementById("selectedPlMethod").value;
  const title = document.getElementById("plTitle").value.trim();

  let playlistData = {
    title: title || "Çalma Listem",
    createdAt: new Date().toLocaleDateString("tr-TR")
  };

  if (method === "auto") {
    const rawVal = document.getElementById("autoDetectInput").value.trim();
    if (!rawVal) {
      showToast("Lütfen sağlayıcınızın mesajını veya linkini yapıştırın.", "error");
      return;
    }
    const detected = autoDetectedData || analyzeSmartText(rawVal);
    if (!detected) {
      showToast("Girdiğiniz metinden geçerli bir bağlantı veya hesap çıkarılamadı.", "error");
      return;
    }
    playlistData = { ...playlistData, ...detected };
  } else if (method === "xtream") {
    playlistData.type = "xtream";
    playlistData.server = document.getElementById("plServer").value.trim();
    playlistData.username = document.getElementById("plUsername").value.trim();
    playlistData.password = document.getElementById("plPassword").value.trim();
    if (!playlistData.server || !playlistData.username || !playlistData.password) {
      showToast("Lütfen Sunucu, Kullanıcı Adı ve Şifreyi eksiksiz girin.", "error");
      return;
    }
  } else if (method === "m3u_url") {
    playlistData.type = "m3u";
    playlistData.url = document.getElementById("plUrl").value.trim();
    if (!playlistData.url) {
      showToast("Lütfen M3U URL bağlantısını girin.", "error");
      return;
    }
  } else if (method === "m3u_file" || method === "m3u_raw") {
    playlistData.type = "m3u_raw";
    playlistData.rawText = document.getElementById("plRawText").value.trim();
    if (!playlistData.rawText) {
      showToast("Lütfen bir M3U dosyası seçin veya metin yapıştırın.", "error");
      return;
    }
  } else if (method === "stalker") {
    playlistData.type = "stalker";
    playlistData.portalUrl = document.getElementById("plPortalUrl").value.trim();
    playlistData.mac = document.getElementById("plMac").value.trim();
    if (!playlistData.portalUrl || !playlistData.mac) {
      showToast("Lütfen Portal URL ve MAC adresini girin.", "error");
      return;
    }
  }

  if (isFirebaseConfigured && firestoreDb && currentUser) {
    try {
      const docRef = await firestoreDb.collection("users").doc(currentUser.uid).collection("playlists").add(playlistData);
      playlistData.id = docRef.id;
      userPlaylists.push(playlistData);
    } catch (err) {
      showToast("Liste kaydedilemedi: " + err.message, "error");
      return;
    }
  } else {
    playlistData.id = "local_pl_" + Date.now();
    userPlaylists.push(playlistData);
    localStorage.setItem(LOCAL_PLAYLISTS_KEY, JSON.stringify(userPlaylists));
  }

  showToast("🎉 Çalma listesi kaydedildi ve TV'nize senkronize edildi!");
  clearAddPlaylistForm();
  toggleAddPlaylistForm(false);
  renderPlaylists();
}

// Delete Playlist
async function deletePlaylist(id) {
  if (!confirm("Bu çalma listesini silmek istediğinizden emin misiniz?")) return;

  if (isFirebaseConfigured && firestoreDb && currentUser) {
    try {
      await firestoreDb.collection("users").doc(currentUser.uid).collection("playlists").doc(id).delete();
    } catch (err) {
      showToast("Silinemedi: " + err.message, "error");
      return;
    }
  }

  userPlaylists = userPlaylists.filter(p => p.id !== id);
  if (!isFirebaseConfigured) {
    localStorage.setItem(LOCAL_PLAYLISTS_KEY, JSON.stringify(userPlaylists));
  }
  showToast("Çalma listesi silindi.");
  renderPlaylists();
}

// Pair TV Device with PIN Code
async function handlePairDevice(e) {
  e.preventDefault();
  const pinInput = document.getElementById("tvPinInput");
  const deviceNameInput = document.getElementById("tvDeviceName");
  const pin = pinInput.value.replace(/\s+/g, "").toUpperCase();
  const deviceName = deviceNameInput.value.trim() || "Android TV";

  if (pin.length < 6) {
    showToast("Lütfen TV ekranında görünen 6 haneli kodu eksiksiz girin.", "error");
    return;
  }

  const newDevice = {
    id: "dev_" + Date.now(),
    name: deviceName,
    pin: pin,
    pairedAt: new Date().toLocaleDateString("tr-TR"),
    lastActive: "Şimdi"
  };

  if (isFirebaseConfigured && firestoreDb && currentUser) {
    try {
      // Create pairing session in Firestore
      await firestoreDb.collection("pairing_sessions").doc(pin).set({
        uid: currentUser.uid,
        email: currentUser.email,
        status: "paired",
        pairedAt: firebase.firestore.FieldValue.serverTimestamp()
      });

      const dRef = await firestoreDb.collection("users").doc(currentUser.uid).collection("devices").add(newDevice);
      newDevice.id = dRef.id;
      userDevices.push(newDevice);
    } catch (err) {
      showToast("Cihaz eşleştirilemedi: " + err.message, "error");
      return;
    }
  } else {
    userDevices.push(newDevice);
    localStorage.setItem(LOCAL_DEVICES_KEY, JSON.stringify(userDevices));
  }

  showToast(`📺 '${deviceName}' cihazınız başarıyla HanTV hesabınıza bağlandı!`);
  pinInput.value = "";
  deviceNameInput.value = "";
  renderDevices();
}

// Unpair Device
async function unpairDevice(id) {
  if (!confirm("Bu cihazın bağlantısını kesmek istiyor musunuz?")) return;

  if (isFirebaseConfigured && firestoreDb && currentUser) {
    try {
      await firestoreDb.collection("users").doc(currentUser.uid).collection("devices").doc(id).delete();
    } catch (err) {
      console.error(err);
    }
  }

  userDevices = userDevices.filter(d => d.id !== id);
  if (!isFirebaseConfigured) {
    localStorage.setItem(LOCAL_DEVICES_KEY, JSON.stringify(userDevices));
  }
  showToast("Cihaz bağlantısı sonlandırıldı.");
  renderDevices();
}

// Renderers
function renderPlaylists() {
  const container = document.getElementById("playlistCardsContainer");
  if (!container) return;

  if (userPlaylists.length === 0) {
    container.innerHTML = `
      <div class="empty-state">
        <span class="empty-icon">📺</span>
        <h4>Henüz kayıtlı bir çalma listeniz yok</h4>
        <p>Aşağıdaki butona tıklayarak Xtream Codes veya M3U listenizi ekleyin, TV'nizde anında izlemeye başlayın.</p>
        <button class="btn btn-primary" onclick="toggleAddPlaylistForm(true)">+ Yeni Liste Ekle</button>
      </div>
    `;
    return;
  }

  container.innerHTML = userPlaylists.map(pl => `
    <div class="playlist-card glass-card">
      <div class="pl-header">
        <span class="pl-type-badge type-${pl.type}">${pl.type.toUpperCase()}</span>
        <button class="pl-del-btn" onclick="deletePlaylist('${pl.id}')" title="Listeyi Sil">🗑️</button>
      </div>
      <h4 class="pl-title">${escapeHtml(pl.title)}</h4>
      <div class="pl-details">
        ${pl.type === "xtream" ? `
          <div class="pl-detail-row"><span>Sunucu:</span> <strong>${escapeHtml(pl.server || "")}</strong></div>
          <div class="pl-detail-row"><span>Kullanıcı:</span> <strong>${escapeHtml(pl.username || "")}</strong></div>
        ` : pl.type === "m3u" ? `
          <div class="pl-detail-row"><span>URL:</span> <strong class="truncate-text">${escapeHtml(pl.url || "")}</strong></div>
        ` : `
          <div class="pl-detail-row"><span>Portal:</span> <strong>${escapeHtml(pl.portalUrl || "")}</strong></div>
          <div class="pl-detail-row"><span>MAC:</span> <strong>${escapeHtml(pl.mac || "")}</strong></div>
        `}
        <div class="pl-detail-row"><span>Eklenme Tarihi:</span> <em>${pl.createdAt || "Yeni"}</em></div>
      </div>
      <div class="pl-sync-status">
        <span class="sync-dot"></span> TV Senkronizasyonu Aktif
      </div>
    </div>
  `).join("");
}

function renderDevices() {
  const container = document.getElementById("devicesCardsContainer");
  if (!container) return;

  if (userDevices.length === 0) {
    container.innerHTML = `
      <div class="empty-state">
        <span class="empty-icon">🔌</span>
        <h4>Henüz bağlı bir TV cihazı bulunmuyor</h4>
        <p>HanTV uygulamasını TV'nizde açın ve ekranda çıkan 6 haneli kodu yukarıdaki alana girin.</p>
      </div>
    `;
    return;
  }

  container.innerHTML = userDevices.map(d => `
    <div class="device-card-item glass-card">
      <div class="dev-item-icon">📺</div>
      <div class="dev-item-info">
        <h4>${escapeHtml(d.name)}</h4>
        <p>Eşleşme: ${d.pairedAt} • Son Etkinlik: ${d.lastActive || "Şimdi"}</p>
      </div>
      <button class="btn btn-secondary btn-sm" onclick="unpairDevice('${d.id}')">Bağlantıyı Kes</button>
    </div>
  `).join("");
}

function toggleAddPlaylistForm(show) {
  const formBox = document.getElementById("addPlaylistFormBox");
  if (formBox) {
    formBox.style.display = show ? "block" : "none";
    if (show) formBox.scrollIntoView({ behavior: "smooth" });
  }
}

function onPlaylistTypeChange() {
  const type = document.getElementById("plType").value;
  document.getElementById("xtreamFields").style.display = type === "xtream" ? "block" : "none";
  document.getElementById("m3uFields").style.display = type === "m3u" ? "block" : "none";
  document.getElementById("stalkerFields").style.display = type === "stalker" ? "block" : "none";
}

function escapeHtml(str) {
  if (!str) return "";
  return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

function getFirebaseErrorMessage(code) {
  switch (code) {
    case "auth/user-not-found":
    case "auth/wrong-password":
    case "auth/invalid-credential":
      return "E-posta adresi veya şifre hatalı.";
    case "auth/email-already-in-use":
      return "Bu e-posta adresi ile zaten bir hesap mevcut.";
    case "auth/invalid-email":
      return "Geçersiz bir e-posta adresi girdiniz.";
    case "auth/weak-password":
      return "Şifreniz çok zayıf. En az 6 karakter giriniz.";
    default:
      return "Bir hata oluştu, lütfen tekrar deneyiniz.";
  }
}

// Global initialization
document.addEventListener("DOMContentLoaded", () => {
  initAuth();

  // User menu toggle
  const userMenuBtn = document.getElementById("headerUserMenuBtn");
  const userDropdown = document.getElementById("headerUserDropdown");
  if (userMenuBtn && userDropdown) {
    userMenuBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      userDropdown.classList.toggle("active");
    });
    document.addEventListener("click", () => {
      userDropdown.classList.remove("active");
    });
  }
});
