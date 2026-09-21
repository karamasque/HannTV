# 🔥 HanTV Firebase Kurulum Rehberi (100% Ücretsiz)

HanTV web sitenizin üyelik ve TV senkronizasyon altyapısı **Google Firebase** ile tam entegre çalışacak şekilde hazırlanmıştır.

---

## 🚀 3 Dakikada Kendi Ücretsiz Firebase Projenizi Oluşturma

1. **Firebase Console'a Gidin:**
   - [https://console.firebase.google.com/](https://console.firebase.google.com/) adresine Google hesabınızla giriş yapın.

2. **Yeni Proje Ekleyin:**
   - **"Add project" (Proje Ekle)** butonuna tıklayın.
   - Proje adına **`HanTV`** yazın ve devam edin. (Google Analytics isteğe bağlıdır, kapatabilirsiniz).

3. **Authentication (Giriş / Kayıt) Aktif Edin:**
   - Sol menüden **Build > Authentication** sekmesine gidin.
   - **"Get Started"** butonuna basın.
   - **Sign-in method** listesinden **Email/Password** seçeneğini seçip **Enable (Etkinleştir)** yapın ve kaydedin.

4. **Firestore Veritabanını Açın:**
   - Sol menüden **Build > Firestore Database** sekmesine gidin.
   - **"Create database"** butonuna tıklayın.
   - Konum olarak en yakın bölgeyi (örn: `eur3 - europe-west` veya `us-central`) seçin.
   - Güvenlik kuralı olarak **"Start in test mode"** seçin.

5. **Web API Bilgilerini Alın:**
   - Sol üstteki ⚙️ **Project Settings (Proje Ayarları)** simgesine tıklayın.
   - **General** sekmesinde aşağı kaydırıp **"Your apps"** altında **Web (`</>`)** simgesine tıklayın.
   - Uygulama takma adına **`HanTV Web`** yazıp **Register app** deyin.
   - Ekranda çıkan `firebaseConfig` kodlarını kopyalayın.

6. **Web Sitesine Yapıştırın:**
   - `website/firebase-config.js` dosyasını açıp kendi anahtarlarınızı yapıştırın:
   ```javascript
   const firebaseConfig = {
     apiKey: "AIzaSy...",
     authDomain: "hantv-xxxx.firebaseapp.com",
     projectId: "hantv-xxxx",
     storageBucket: "hantv-xxxx.appspot.com",
     messagingSenderId: "123456789",
     appId: "1:123456789:web:abcdef"
   };
   ```

---

## ⚡ Demo Modu (Geliştirici / Test Modu)
Firebase anahtarlarınızı henüz girmemiş olsanız bile, sistem **akıllı yerel modda (Demo Mode)** çalışır:
- Giriş yapabilir, kayıt olabilir,
- Xtream / M3U çalma listesi ekleyebilir,
- TV PIN eşleştirmesini anında test edebilirsiniz!
