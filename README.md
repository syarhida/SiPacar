# SiPacar - Sistem Prakiraan Cuaca Ringkas

Aplikasi Android untuk menampilkan prakiraan cuaca Jakarta dengan interface yang simpel dan modern.

## 📱 Tentang Aplikasi

**SiPacar** (Sistem Prakiraan Cuaca Ringkas) adalah aplikasi prakiraan cuaca untuk wilayah Jakarta yang menampilkan informasi cuaca per jam dengan antarmuka yang clean dan mudah digunakan.

### Fitur Utama

- 🌡️ Menampilkan suhu per jam dalam Celsius
- 🕐 Format waktu dalam Bahasa Indonesia
- 🎨 UI Modern dengan tema Putih & Biru
- 🌅 Icon cuaca berbeda untuk Pagi, Siang, Sore, dan Malam
- 🔄 Pull-to-refresh untuk memperbarui data
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
https://api.open-meteo.com/v1/forecast?latitude=-6.2&longitude=106.8&hourly=temperature_2m
```

Koordinat untuk Jakarta:
- Latitude: -6.2
- Longitude: 106.8

## 📝 Lisensi

Project ini dibuat untuk keperluan pembelajaran.

## 👨‍💻 Developer

Dibuat dengan ❤️ menggunakan Kotlin

---

**SiPacar** - Prakiraan Cuaca Jakarta, Simple & Akurat! 🌤️

