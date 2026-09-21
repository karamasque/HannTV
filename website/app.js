/**
 * HanTV Landing Page Scripts
 * Includes i18n (TR/EN), screenshot filtering, lightbox, tabs & interactive elements.
 */

// --- Translations ---
const translations = {
  tr: {
    nav_features: "Özellikler",
    nav_screenshots: "Ekran Görüntüleri",
    nav_devices: "Cihazlar",
    nav_install: "Kurulum Rehberi",
    nav_faq: "SSS",
    header_download: "v5.0.0 İndir",
    hero_badge: "HanTV v5.0.0 Yayınlandı • Android TV İçin Özel",
    hero_title_1: "Android TV İçin",
    hero_title_gradient: "En Hızlı & Modern",
    hero_title_2: "IPTV Oynatıcı",
    hero_desc: "Çift oynatma motoru (mpv + ExoPlayer) ile donatılmış, 4K HDR destekli, modern cam arayüzlü ve kumanda odaklı açık kaynaklı IPTV oynatıcınız. Kendi M3U, Xtream veya Stalker listenizi ekleyin ve TV keyfinizi katlayın.",
    hero_download_sub: "Ücretsiz & Direkt APK",
    hero_download_main: "HanTV v5.0.0 İndir",
    hero_github: "GitHub Projesi",
    stat_1: "Çift Motor (mpv + ExoPlayer)",
    stat_2: "4K 60FPS HDR & Dolby",
    stat_3: "4'lü Multiview Desteği",
    stat_4: "%100 Reklamsız & GPLv3",
    notice_title: "Önemli Hatırlatma & Yasal Bilgilendirme",
    notice_desc: "HanTV yalnızca bir medya oynatıcıdır. Uygulama içerisinde hiçbir kanal, yayın, dizi veya film bulunmamaktadır ve satışı yapılmamaktadır. Kullanıcılar kendi yasal IPTV aboneliklerini (M3U, Xtream, Stalker) ekleyerek kullanır.",
    features_tag: "Benzersiz Yetenekler",
    features_title: "Neden HanTV Tercih Etmelisiniz?",
    features_subtitle: "Android TV deneyimini baştan tanımlayan en gelişmiş oynatma motorları ve şık arayüz özellikleri.",
    f1_title: "Çift Oynatma Motoru",
    f1_desc: "<strong>libmpv</strong> (FFmpeg) ile en zorlu kodekleri ve formatları sorunsuz açın; <strong>ExoPlayer</strong> (Media3) ile canlı yayınlara anında bağlanın. Otomatik motor geçişi veya kanal bazlı seçim imkanı.",
    f2_title: "4K HDR & Surround Ses",
    f2_desc: "Doğrudan donanım hızlandırmalı 4K HDR ve Dolby Vision işleme, otomatik kare hızı (Auto Frame Rate), surround ses passthrough ve düşük sesli yayınlar için %150 Ses Güçlendirme (Volume Boost).",
    f3_title: "4'lü Çoklu Ekran (Multiview)",
    f3_desc: "Maç günlerinde veya haber takiplerinde aynı anda 4 farklı canlı kanalı tek ekranda izleyin. Kumanda ile kanallar arası anında ses geçişi yapın veya tam ekrana büyütün.",
    f4_title: "Akıllı EPG & 7 Günlük Catch-Up",
    f4_desc: "XMLTV EPG rehberi, canlı zaman çizgisi, yayın hatırlatıcıları ve 7 güne kadar geriye dönük yayınları tekrar izleme (Catch-up) ve canlı yayını durdurup geri sarma desteği.",
    f5_title: "Çoklu Profil & Çocuk Kilidi",
    f5_desc: "Her aile bireyi için bağımsız favori listeleri, izleme geçmişi ve kaldığı yerden devam etme noktaları. PIN korumalı yetişkin kategorileri ve güvenli Çocuk Modu.",
    f6_title: "Telefondan QR / Wi-Fi İle Kurulum",
    f6_desc: "TV kumandasıyla uzun linkler ve şifreler yazmaya son! Ekranda beliren QR kodu telefonunuzla okutun veya aynı Wi-Fi ağındaki tarayıcıdan anında listenizi ekleyin.",
    f7_title: "TMDB & Altyazı Entegrasyonu",
    f7_desc: "The Movie Database (TMDB) ile filmler ve diziler için Türkçe afişler, açıklamalar, puanlar ve fragmanlar. Dahili OpenSubtitles entegrasyonu ile tek tuşla altyazı arama.",
    f8_title: "Özel DNS & DRM Desteği",
    f8_desc: "Dahili Cloudflare, Google, Quad9 DNS ve DNS-over-HTTPS (DoH) desteği ile engelleri aşın. Widevine ve ClearKey DRM korumalı yayınları doğrudan oynatabilme.",
    shots_tag: "Görsel Tur",
    shots_title: "Şık ve Kusursuz TV Arayüzü",
    shots_subtitle: "Jetpack Compose for TV ile baştan sona büyük ekranlar ve uzaktan kumandalar için tasarlandı.",
    tab_all: "Tüm Ekranlar",
    tab_home: "Ana Sayfa",
    tab_live: "Canlı TV & EPG",
    tab_vod: "Film & Dizi",
    tab_settings: "Ayarlar & Profil",
    compat_tag: "Geniş Uyumluluk",
    compat_title: "Tüm Akıllı TV ve TV Box Cihazlarınızda",
    compat_subtitle: "Android TV 5.0 (Lollipop) ve üzerindeki tüm resmi veya kutu TV cihazlarıyla %100 tam uyumlu.",
    dev_1_title: "Android & Google TV",
    dev_1_desc: "Sony, Philips, TCL, Vestel, Toshiba, Xiaomi TV ve Google TV destekli tüm televizyonlar.",
    dev_2_title: "TV Box & Medya Oynatıcılar",
    dev_2_desc: "Xiaomi Mi Box / TV Stick, Mecool, Homatics, Nvidia Shield TV, Nokia Box ve tüm Android TV kutuları.",
    dev_3_title: "Amazon Fire TV",
    dev_3_desc: "Fire TV Stick 4K, 4K Max, Lite ve Fire TV Cube cihazlarına Downloader ile anında yüklenir.",
    dev_4_title: "Kolay Telefon Entegrasyonu",
    dev_4_desc: "Kurulum aşamasında telefonunuzdaki kamerayla QR okutarak çalma listesi bilgilerini TV'ye anında aktarın.",
    proto_label: "Desteklenen Kaynak & Formatlar:",
    install_tag: "Adım Adım",
    install_title: "HanTV TV'ye Nasıl Yüklenir?",
    install_subtitle: "Cihazınıza en uygun yöntemi seçerek birkaç dakika içinde HanTV'yi kullanmaya başlayın.",
    inst_m1_btn: "Downloader İle (Önerilen)",
    inst_m2_btn: "USB Bellek İle",
    inst_m3_btn: "Telefondan Gönder (SFTV)",
    step1_title: "Downloader'ı Yükleyin",
    step1_desc: "TV'nizin Google Play Store veya Amazon Appstore mağazasından <strong>Downloader by AFTVnews</strong> uygulamasını yükleyin.",
    step2_title: "İndirme Linkini Girin",
    step2_desc: "Downloader'ın URL çubuğuna GitHub Release linkini yazıp <strong>Go</strong> butonuna basın.",
    step3_title: "Yükleyin ve Başlatın",
    step3_desc: "İndirme tamamlandığında ekrana gelen <strong>Yükle (Install)</strong> butonuna basın. Uygulama kurulduktan sonra HanTV açılmaya hazırdır!",
    usb_s1_title: "APK Dosyasını İndirin",
    usb_s1_desc: "Aşağıdaki butondan <strong>HanTV v5.0.0 APK</strong> dosyasını bilgisayarınıza veya telefonunuza indirin.",
    usb_s2_title: "USB Belleğe Kopyalayın",
    usb_s2_desc: "İndirdiğiniz <code>.apk</code> dosyasını bir USB belleğe atın ve TV'nizin veya TV Box'ınızın USB girişine takın.",
    usb_s3_title: "Dosya Yöneticisiyle Kurun",
    usb_s3_desc: "TV'nizdeki herhangi bir dosya yöneticisi (FX File Explorer, X-plore vb.) ile USB içindeki APK'ya tıklayıp kurulumu tamamlayın.",
    sftv_s1_title: "SFTV Uygulamasını Açın",
    sftv_s1_desc: "Hem Android telefonunuza hem de TV'nize Google Play Store'dan <strong>Send Files to TV (SFTV)</strong> uygulamasını yükleyin.",
    sftv_s2_title: "Telefona APK'yı İndirin & Yollayın",
    sftv_s2_desc: "Telefondan HanTV APK dosyasını indirin. SFTV üzerinden <strong>Send</strong> diyerek TV'nize gönderin.",
    sftv_s3_title: "TV'den Açıp Kurun",
    sftv_s3_desc: "TV'ye gelen APK dosyasının üzerine tıklayın ve yükleyin. Kurulum saniyeler içinde tamamlanır.",
    dl_box_title: "HanTV'yi Şimdi Ücretsiz İndirin",
    dl_box_desc: "En son kararlı sürümümüzle kristal netliğinde yayınların ve eşsiz TV deneyiminin tadını çıkarın.",
    faq_tag: "Merak Edilenler",
    faq_title: "Sıkça Sorulan Sorular",
    faq_subtitle: "HanTV hakkında en çok sorulan soruların cevapları.",
    faq1_q: "HanTV tamamen ücretsiz mi? Reklam içeriyor mu?",
    faq1_a: "Evet! HanTV tamamen ücretsizdir ve GPLv3 lisanslı açık kaynak kodlu bir projedir. Uygulama içinde kesinlikle hiçbir reklam, takipçi veya gizli abonelik bulunmamaktadır.",
    faq2_q: "Uygulamada hazır kanal veya yayın listesi var mı?",
    faq2_a: "Hayır. HanTV saf bir medya oynatıcıdır. İçinde hiçbir kanal, video veya yayın kaynağı barındırmaz. Kullanıcılar kendi satın aldıkları veya sahip oldukları M3U, Xtream Codes ya da Stalker portal bilgilerini eklerler.",
    faq3_q: "Hangi çalma listesi formatları destekleniyor?",
    faq3_a: "HanTV; <strong>Xtream Codes API</strong> (kullanıcı adı, şifre, sunucu adresi), <strong>M3U & M3U8</strong> (URL bağlantısı veya yerel dosya) ve <strong>Stalker / Ministra</strong> (MAC adresi) portallarını eksiksiz olarak destekler.",
    faq4_q: "TV kumandasıyla kolay kontrol edilebiliyor mu?",
    faq4_a: "Kesinlikle! HanTV sıfırdan TV kumandalarının yön (D-Pad), renk tuşları ve medya tuşları için geliştirilmiştir. Ayrıca ayarlar menüsünden kumandanızın renkli tuşlarına veya sayı tuşlarına 25 farklı kısayol eylemi atayabilirsiniz.",
    faq5_q: "Telefonumdaki listeyi TV'ye nasıl hızlıca aktarabilirim?",
    faq5_a: "HanTV'yi açtığınızda 'Kaynak Ekle' ekranındaki QR kodu telefonunuzun kamerasıyla okutarak veya yerel Wi-Fi ağı üzerinden verilen PIN kodunu tarayıcınıza girerek saniyeler içinde listenizi TV'ye aktarabilirsiniz.",
    footer_about: "Android TV için hızlı, modern, açık kaynaklı IPTV oynatıcı.",
    footer_disclaimer: "⚠️ Yasal Uyarı: HanTV bağımsız, açık kaynaklı bir video oynatıcıdır. Hiçbir yayın veya içerik barındırmaz ve sağlamaz."
  },
  en: {
    nav_features: "Features",
    nav_screenshots: "Screenshots",
    nav_devices: "Devices",
    nav_install: "Install Guide",
    nav_faq: "FAQ",
    header_download: "Download v5.0.0",
    hero_badge: "HanTV v5.0.0 Released • Built for Android TV",
    hero_title_1: "For Android TV",
    hero_title_gradient: "The Fastest & Sleekest",
    hero_title_2: "IPTV Player",
    hero_desc: "Powered by dual playback engines (mpv + ExoPlayer), 4K HDR support, modern glass UI, and remote-first navigation. Bring your own M3U, Xtream or Stalker sources and elevate your TV experience.",
    hero_download_sub: "Free & Direct APK",
    hero_download_main: "Download HanTV v5.0.0",
    hero_github: "GitHub Project",
    stat_1: "Dual Engine (mpv + ExoPlayer)",
    stat_2: "4K 60FPS HDR & Dolby",
    stat_3: "4-Stream Multiview",
    stat_4: "100% Ad-Free & GPLv3",
    notice_title: "Important Notice & Legal Disclaimer",
    notice_desc: "HanTV is purely a media player. The app contains NO channels, streams, playlists or subscriptions. Users are solely responsible for adding their own legitimate sources.",
    features_tag: "Unmatched Capabilities",
    features_title: "Why Choose HanTV?",
    features_subtitle: "Next-generation playback engines and exquisite glass design tailored for big TV screens.",
    f1_title: "Dual Playback Engines",
    f1_desc: "<strong>libmpv</strong> (FFmpeg) for supreme codec compatibility; <strong>ExoPlayer</strong> (Media3) for instantaneous live channel switching. Automatic fallback or per-channel overrides.",
    f2_title: "4K HDR & Surround Sound",
    f2_desc: "Direct 4K HDR & Dolby Vision rendering, auto frame-rate matching, surround sound passthrough, and 150% Volume Boost for low-level streams.",
    f3_title: "4-Stream Multiview",
    f3_desc: "Watch up to four live channels simultaneously on a single screen during match days. Switch audio focus seamlessly with remote navigation.",
    f4_title: "Smart EPG & 7-Day Catch-Up",
    f4_desc: "XMLTV electronic programme guide, live timeline marker, reminders, up to 7-day catch-up replay, and live rewind support.",
    f5_title: "Multi-Profile & Kids Lock",
    f5_desc: "Independent favorite lists, watch history, and resume points for everyone in your household. PIN-locked adult categories and dedicated Kids Mode.",
    f6_title: "Fast Mobile QR / Wi-Fi Setup",
    f6_desc: "No more typing long URLs with a TV remote. Scan the on-screen QR code with your phone or configure via local Wi-Fi PIN in seconds.",
    f7_title: "TMDB & Subtitles Integration",
    f7_desc: "The Movie Database (TMDB) posters, plots, ratings, and trailers. Built-in OpenSubtitles search and local subtitle loading.",
    f8_title: "Custom DNS & DRM Support",
    f8_desc: "Built-in Cloudflare, Google, Quad9 DNS & DNS-over-HTTPS (DoH). Direct playback for Widevine and ClearKey DRM protected streams.",
    shots_tag: "Visual Showcase",
    shots_title: "Sleek & Seamless TV Interface",
    shots_subtitle: "Crafted with Jetpack Compose for TV specifically for large screens and remote controllers.",
    tab_all: "All Screens",
    tab_home: "Home",
    tab_live: "Live TV & EPG",
    tab_vod: "Movies & Series",
    tab_settings: "Settings & Profiles",
    compat_tag: "Broad Compatibility",
    compat_title: "Works on All Smart TVs & TV Boxes",
    compat_subtitle: "100% compatible with Android TV 5.0 (Lollipop) and above, Google TV and Fire OS devices.",
    dev_1_title: "Android & Google TV",
    dev_1_desc: "Sony, Philips, TCL, Vestel, Toshiba, Xiaomi TV, Chromecast with Google TV and all certified TVs.",
    dev_2_title: "TV Boxes & Streaming Sticks",
    dev_2_desc: "Xiaomi Mi Box / Stick, Mecool, Homatics, Nvidia Shield TV, Nokia Box and all Android TV boxes.",
    dev_3_title: "Amazon Fire TV",
    dev_3_desc: "Fire TV Stick 4K, 4K Max, Lite and Fire TV Cube via Downloader app.",
    dev_4_title: "Easy Phone Sync",
    dev_4_desc: "Effortlessly sync playlists from your mobile phone to TV via local QR code or web browser PIN.",
    proto_label: "Supported Protocols & Formats:",
    install_tag: "Step by Step",
    install_title: "How to Install HanTV on TV",
    install_subtitle: "Choose the easiest installation method for your device and get started in minutes.",
    inst_m1_btn: "Via Downloader (Recommended)",
    inst_m2_btn: "Via USB Drive",
    inst_m3_btn: "Send Files to TV (SFTV)",
    step1_title: "Install Downloader",
    step1_desc: "Get <strong>Downloader by AFTVnews</strong> from Google Play Store or Amazon Appstore on your TV.",
    step2_title: "Enter Download URL",
    step2_desc: "Type the GitHub Releases link in Downloader's URL box and press <strong>Go</strong>.",
    step3_title: "Install & Launch",
    step3_desc: "Once downloaded, select <strong>Install</strong> on the screen prompt. HanTV is ready to enjoy!",
    usb_s1_title: "Download APK",
    usb_s1_desc: "Download the <strong>HanTV v5.0.0 APK</strong> file to your computer or phone using the button below.",
    usb_s2_title: "Copy to USB Drive",
    usb_s2_desc: "Copy the <code>.apk</code> file onto a USB flash drive and plug it into your TV's USB port.",
    usb_s3_title: "Install via File Manager",
    usb_s3_desc: "Use any TV file manager (e.g. FX File Explorer, X-plore) to open the APK from USB and install.",
    sftv_s1_title: "Install SFTV",
    sftv_s1_desc: "Install <strong>Send Files to TV (SFTV)</strong> on both your Android phone and TV from Google Play Store.",
    sftv_s2_title: "Send APK to TV",
    sftv_s2_desc: "Download the HanTV APK on your phone and send it wirelessly to your TV using the SFTV app.",
    sftv_s3_title: "Open & Install on TV",
    sftv_s3_desc: "Click the received APK file on your TV to install. Done in seconds!",
    dl_box_title: "Download HanTV for Free",
    dl_box_desc: "Experience ultra-smooth streaming and a premium TV experience with our latest stable release.",
    faq_tag: "Got Questions?",
    faq_title: "Frequently Asked Questions",
    faq_subtitle: "Everything you need to know about HanTV.",
    faq1_q: "Is HanTV completely free? Are there any ads?",
    faq1_a: "Yes! HanTV is completely free and licensed under GNU GPLv3. There are zero ads, zero trackers, and no hidden subscriptions.",
    faq2_q: "Does HanTV come with pre-loaded channels or streams?",
    faq2_a: "No. HanTV is strictly a media player. It ships with NO channels or content. You must provide your own legitimate M3U, Xtream Codes, or Stalker portal.",
    faq3_q: "Which playlist formats and protocols are supported?",
    faq3_a: "HanTV fully supports <strong>Xtream Codes API</strong> (username, password, server URL), <strong>M3U & M3U8</strong> (URL or local file), and <strong>Stalker / Ministra</strong> (MAC address).",
    faq4_q: "Is it fully optimized for TV remote controls?",
    faq4_a: "Absolutely. HanTV was designed from the ground up for D-Pad remotes. You can also map color and number keys to 25 quick actions in settings.",
    faq5_q: "How can I easily transfer my playlist from my phone to TV?",
    faq5_a: "When adding a source, scan the QR code shown on your TV with your phone camera or enter the displayed PIN in your browser over local Wi-Fi.",
    footer_about: "Fast, modern, open-source IPTV player for Android TV.",
    footer_disclaimer: "⚠️ Legal Notice: HanTV is an independent open-source media player. It does not provide, host or sell any streams."
  }
};

let currentLang = localStorage.getItem("hantv_lang") || "tr";

function applyLanguage(lang) {
  currentLang = lang;
  localStorage.setItem("hantv_lang", lang);
  document.documentElement.lang = lang;

  const dict = translations[lang] || translations.tr;
  document.querySelectorAll("[data-i18n]").forEach((elem) => {
    const key = elem.getAttribute("data-i18n");
    if (dict[key]) {
      elem.innerHTML = dict[key];
    }
  });

  const langLabel = document.getElementById("langLabel");
  if (langLabel) {
    langLabel.textContent = lang.toUpperCase();
  }
}

// --- Lightbox Modal ---
function openLightbox(imgSrc, title) {
  const modal = document.getElementById("lightboxModal");
  const img = document.getElementById("lightboxImg");
  const titleEl = document.getElementById("lightboxTitle");
  if (modal && img && titleEl) {
    img.src = imgSrc;
    img.alt = title;
    titleEl.textContent = title;
    modal.classList.add("active");
    document.body.style.overflow = "hidden";
  }
}

function closeLightbox() {
  const modal = document.getElementById("lightboxModal");
  if (modal) {
    modal.classList.remove("active");
    document.body.style.overflow = "";
  }
}

// --- Copy to Clipboard ---
function copyText(text) {
  navigator.clipboard.writeText(text).then(() => {
    alert(currentLang === "tr" ? "Bağlantı panoya kopyalandı!" : "Link copied to clipboard!");
  }).catch(() => {
    const tempInput = document.createElement("input");
    tempInput.value = text;
    document.body.appendChild(tempInput);
    tempInput.select();
    document.execCommand("copy");
    document.body.removeChild(tempInput);
    alert(currentLang === "tr" ? "Bağlantı panoya kopyalandı!" : "Link copied to clipboard!");
  });
}

// --- DOM Initializations ---
document.addEventListener("DOMContentLoaded", () => {
  // Apply saved language
  applyLanguage(currentLang);

  // Language toggle button
  const langToggle = document.getElementById("langToggle");
  if (langToggle) {
    langToggle.addEventListener("click", () => {
      const nextLang = currentLang === "tr" ? "en" : "tr";
      applyLanguage(nextLang);
    });
  }

  // Header scroll blur effect
  const navbar = document.getElementById("navbar");
  window.addEventListener("scroll", () => {
    if (window.scrollY > 40) {
      navbar.classList.add("scrolled");
    } else {
      navbar.classList.remove("scrolled");
    }
  });

  // Mobile menu toggle
  const mobileMenuToggle = document.getElementById("mobileMenuToggle");
  const mainNav = document.getElementById("mainNav");
  if (mobileMenuToggle && mainNav) {
    mobileMenuToggle.addEventListener("click", () => {
      mainNav.classList.toggle("open");
    });

    // Close menu when clicking nav links
    mainNav.querySelectorAll("a").forEach((link) => {
      link.addEventListener("click", () => {
        mainNav.classList.remove("open");
      });
    });
  }

  // Screenshot tab filters
  const shotTabs = document.querySelectorAll(".shot-tab-btn");
  const shotCards = document.querySelectorAll(".shot-card");

  shotTabs.forEach((tab) => {
    tab.addEventListener("click", () => {
      shotTabs.forEach((t) => t.classList.remove("active"));
      tab.classList.add("active");

      const filter = tab.getAttribute("data-filter");
      shotCards.forEach((card) => {
        const cat = card.getAttribute("data-category");
        if (filter === "all" || cat === filter) {
          card.style.display = "block";
        } else {
          card.style.display = "none";
        }
      });
    });
  });

  // Installation method tabs
  const instTabs = document.querySelectorAll(".inst-tab-btn");
  const instContents = document.querySelectorAll(".install-content");

  instTabs.forEach((btn) => {
    btn.addEventListener("click", () => {
      instTabs.forEach((b) => b.classList.remove("active"));
      instContents.forEach((c) => c.classList.remove("active"));

      btn.classList.add("active");
      const targetId = btn.getAttribute("data-target");
      const targetContent = document.getElementById(targetId);
      if (targetContent) {
        targetContent.classList.add("active");
      }
    });
  });

  // FAQ Accordion
  const faqItems = document.querySelectorAll(".faq-item");
  faqItems.forEach((item) => {
    const question = item.querySelector(".faq-question");
    const answer = item.querySelector(".faq-answer");

    question.addEventListener("click", () => {
      const isActive = item.classList.contains("active");

      // Close all other items
      faqItems.forEach((otherItem) => {
        otherItem.classList.remove("active");
        otherItem.querySelector(".faq-answer").style.maxHeight = null;
      });

      if (!isActive) {
        item.classList.add("active");
        answer.style.maxHeight = answer.scrollHeight + 40 + "px";
      }
    });
  });

  // Escape key to close lightbox
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
      closeLightbox();
    }
  });
});
