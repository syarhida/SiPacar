# SiPacar - Sistem Prakiraan Cuaca Ringkas

Aplikasi Android untuk menampilkan prakiraan cuaca Jakarta dengan interface yang simpel dan modern.

## 📱 Tentang Aplikasi

**SiPacar** (Sistem Prakiraan Cuaca Ringkas) adalah aplikasi prakiraan cuaca untuk wilayah Jakarta yang menampilkan informasi cuaca per jam dengan antarmuka yang clean dan mudah digunakan.

### Fitur Utama

- 🌡️ Menampilkan suhu realtime dengan card besar
- 📅 **Card prakiraan 4 hari ke depan** (horizontal scroll)
- 🕐 **List cuaca per jam** dengan format Indonesia
- 💧 Menampilkan **kelembapan** untuk setiap data cuaca
- ⏰ **Filter jam cerdas**: 
  - Hari ini: menampilkan dari jam sekarang sampai 23:00
  - Hari lain: menampilkan 00:00 - 23:00
- 🎨 UI Modern dengan tema Putih & Biru (inspired by JAWIR)
- 🌅 Icon cuaca berbeda untuk Pagi, Siang, Sore, dan Malam
- 🔄 Pull-to-refresh untuk memperbarui data
- 📱 Mobile responsive design
- 📶 Error handling untuk koneksi internet

## 🛠️ Teknologi

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit + Gson
- **UI**: Material Design 3, ViewBinding, RecyclerView, CardView
- **Async**: Kotlin Coroutines + LiveData
- **API**: [Open-Meteo Weather API](https://open-meteo.com/)

## 🎨 Desain

### Tema Warna
- **Primary**: #3b82f6 (Biru)
- **Background**: #FFFFFF (Putih)
- **Text**: #333333 (Abu Gelap)
- **Card**: Putih dengan shadow ringan

### Icon Cuaca
- 🌅 **Pagi** (05:00 - 10:59): Icon sunrise
- ☀️ **Siang** (11:00 - 14:59): Icon matahari penuh
- 🌇 **Sore** (15:00 - 17:59): Icon sunset
- 🌙 **Malam** (18:00 - 04:59): Icon bulan & bintang

## 📦 Package Structure

```
com.syarhida.sipacar
├── data
│   ├── api
│   │   ├── WeatherApiService.kt
│   │   └── RetrofitInstance.kt
│   ├── model
│   │   ├── WeatherResponse.kt
│   │   └── WeatherItem.kt
│   └── repository
│       └── WeatherRepository.kt
└── ui
    ├── MainActivity.kt
    ├── adapter
    │   └── WeatherAdapter.kt
    └── viewmodel
        └── WeatherViewModel.kt
```

## 🚀 Cara Menjalankan

1. Clone repository ini
2. Buka project di Android Studio
3. Sync Gradle
4. Run aplikasi di emulator atau device fisik
5. Pastikan device memiliki koneksi internet

## 📋 Requirements

- Android Studio Arctic Fox atau lebih baru
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- JDK 17

## 🌐 API

Aplikasi ini menggunakan [Open-Meteo Weather API](https://open-meteo.com/):
```
https://api.open-meteo.com/v1/forecast?latitude=-6.2&longitude=106.8&hourly=temperature_2m,relative_humidity_2m,weathercode&forecast_days=7
```

**Parameter API:**
- `latitude=-6.2` & `longitude=106.8` - Koordinat Jakarta
- `hourly=temperature_2m,relative_humidity_2m,weathercode` - Data suhu, kelembapan, dan kode cuaca per jam
- `forecast_days=7` - Prakiraan 7 hari (diambil 4 hari pertama)

**Weather Code (WMO Weather interpretation codes):**
- `0` - Cerah ☀️
- `1, 2` - Cerah Berawan ⛅
- `3` - Berawan ☁️
- `45-48` - Berkabut 🌫️
- `51-57` - Hujan Ringan 🌧️
- `61-67` - Hujan 🌧️
- `71-77` - Salju ❄️
- `80-82` - Hujan Shower 🌦️
- `85-86` - Salju Shower ❄️
- `95-99` - Hujan Petir ⛈️

## 📝 Lisensi

Project ini dibuat untuk keperluan pembelajaran.

## 👨‍💻 Developer

Dibuat dengan ❤️ menggunakan Kotlin

---

**SiPacar** - Prakiraan Cuaca Jakarta, Simple & Akurat! 🌤️

