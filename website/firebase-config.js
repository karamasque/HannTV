/**
 * HanTV Firebase Configuration & Client Setup
 * 
 * Firebase Console'dan (https://console.firebase.google.com/) aldığınız
 * proje ayarlarını buraya yapıştırabilirsiniz.
 * 
 * Eğer henüz kendi Firebase projenizi oluşturmadıysanız, sistem otomatik olarak
 * yerel (Demo/Local) modda çalışarak tüm arayüzü ve testleri denemenize olanak sağlar.
 */

const firebaseConfig = {
  apiKey: "YOUR_API_KEY",
  authDomain: "hantv-player.firebaseapp.com",
  projectId: "hantv-player",
  storageBucket: "hantv-player.appspot.com",
  messagingSenderId: "1234567890",
  appId: "1:1234567890:web:abcdef123456"
};

// Demo/Local mod kontrolü (API Key girilmemişse LocalStorage ile simüle eder)
const isFirebaseConfigured = firebaseConfig.apiKey !== "YOUR_API_KEY";

// Firebase App ve Servis Değişkenleri
let firebaseApp = null;
let firebaseAuth = null;
let firestoreDb = null;

if (isFirebaseConfigured && typeof firebase !== "undefined") {
  try {
    firebaseApp = firebase.initializeApp(firebaseConfig);
    firebaseAuth = firebase.auth();
    firestoreDb = firebase.firestore();
    console.log("✅ HanTV Firebase Başarıyla Başlatıldı.");
  } catch (err) {
    console.warn("Firebase başlatılırken bir hata oluştu, demo mod devrede:", err);
  }
} else {
  console.log("ℹ️ HanTV Demo / Yerel Hesap Modunda Çalışıyor.");
}
