# 🌐 HanTV Web Tanıtım Sitesi (Landing Page)

> 🚀 **Canlı Site:** [https://hanntv.netlify.app/](https://hanntv.netlify.app/)

Bu dizin, **HanTV** uygulaması için özel olarak tasarlanmış modern, cam temalı (glassmorphism), çift dilli (Türkçe / İngilizce) ve mobil/TV uyumlu tanıtım web sitesini içerir.

---

## 🚀 100% Ücretsiz Yayına Alma Yöntemleri

### 1. Yöntem: GitHub Pages (En Kolay & Otomatik)
GitHub repository'niz (`https://github.com/ahXN00/HanTV`) üzerinden **tek tıkla** ücretsiz yayınlayabilirsiniz:

1. GitHub'da **Repo Ayarları (Settings)** sayfasına gidin.
2. Sol menüden **Pages** sekmesine tıklayın.
3. **Build and deployment** altında **Source** kısmını:
   - **GitHub Actions** olarak seçin (Hazırladığımız `.github/workflows/deploy-pages.yml` otomatik olarak siteyi yayınlar).
   - *(Veya Branch: `main`, Klasör: `/website` seçebilirsiniz).*
4. Birkaç saniye içinde siteniz `https://ahXN00.github.io/HanTV/` adresinde canlıya geçer!

---

### 2. Yöntem: Vercel / Netlify / Cloudflare Pages
1. [Vercel.com](https://vercel.com) veya [Netlify.com](https://netlify.com) üzerinde ücretsiz hesap açın.
2. `website/` klasörünü sürükleyip bırakın veya GitHub deponuzu bağlayın (Root Directory: `website`).
3. Anında `hantv.vercel.app` veya kendi özel domaininizle (`hantv.com` vb.) ücretsiz yayınlayın.

---

## 💻 Yerel Olarak Önizleme (Test)

Tarayıcınızda `website/index.html` dosyasını doğrudan çift tıklayarak açabilir veya Python / Node ile yerel sunucu başlatabilirsiniz:

```bash
# Python ile:
cd website
python -m http.server 8080
# Tarayıcıda: http://localhost:8080
```

---

## 📱 Özellikler
- **Çift Dil Desteği:** Tek tuşla Türkçe / İngilizce anında dil değiştirme.
- **Glassmorphism & Karanlık Tema:** Android TV estetiğiyle birebir uyumlu modern arayüz.
- **Dinamik Ekran Görüntüsü Galerisi:** Filtrelenebilir sekmeler ve tam ekran Lightbox önizleme.
- **Adım Adım Kurulum Rehberi:** Downloader, USB ve SFTV ile TV'ye kurulum adımları ve kopyalanabilir linkler.
- **Direkt İndirme Butonları:** GitHub Release v5.0.0 APK indirme bağlantıları.
- **Sıkça Sorulan Sorular (SSS):** Açılır-kapanır interaktif akordiyon.
