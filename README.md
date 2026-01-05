# 📍 AQI Watch Tile (Wear OS)

A lightweight, standalone **Wear OS Tile** that displays real-time Air Quality Index (AQI) data on your wrist. Designed for accuracy using the **World Air Quality Index (WAQI)** API, featuring a battery-efficient manual location setup and a color-coded circular gauge UI.

Tested on **Samsung Galaxy Watch 4**, but compatible with most modern Wear OS devices (API 30+).

---

## ✨ Features

* **Real-Time Data:** Fetches accurate, station-level AQI data using the WAQI API (ideal for India & Global use).
* **Color-Coded Meter:** Dynamic circular progress bar that changes color based on pollution intensity (Good -> Severe).
* **0-500 Scale:** Supports the full standard AQI scale, not just simplified 1-5 levels.
* **Manual City Setup:** Avoids battery-draining GPS checks by allowing users to type and save their city (e.g., "New Delhi") once.
* **Geocoding:** Uses OpenWeatherMap Geocoding to translate city names into coordinates.
* **Interval Based Refresh:** The data is automatically updated at an interval of 15 minutes.
* **Manual Sync:** Use the SYNC button to force refresh the AQI data.

---

## 📱 Screenshots

<img width="384" height="384" alt="image" src="https://github.com/user-attachments/assets/53d31702-761a-4a31-91c9-03fb82d0f0ed" />
<img width="384" height="384" alt="image" src="https://github.com/user-attachments/assets/d03cac4b-3144-4b47-8b7b-e79157ae4f33" />

---

## 🛠️ Tech Stack

* **Language:** Kotlin
* **Platform:** Android Wear OS (API 30+)
* **UI:** Jetpack Wear Tiles (ProtoLayout & Material)
* **Networking:** Retrofit 2 + Gson
* **Concurrency:** Kotlin Coroutines & Guava Futures
* **APIs:**
    * [WAQI API](https://aqicn.org/api/) (For Pollution Data)
    * [OpenWeatherMap API](https://openweathermap.org/api) (For City Search/Geocoding)

---

## 🚀 Setup & Build Instructions

### 1. Clone the Repository

    git clone https://github.com/aaryanrr/aqi-watch-tile.git
    open aqi-watch-tile

### 2. Get API Keys
You need two free API keys for this project to work:
1.  **WAQI Token:** Get it [here](https://aqicn.org/data-platform/token/). (Required for AQI numbers).
2.  **OpenWeatherMap Key:** Get it [here](https://home.openweathermap.org/users/sign_up). (Required for searching city names).

### 3. Configure Keys in Code
*Note: For a production app, these should be in `local.properties`, but for this simple project, find the variables in the files below:*

* **Open `AqiTileService.kt`**:

    private val WAQI_TOKEN = "PASTE_YOUR_WAQI_TOKEN_HERE"

* **Open `CitySetupActivity.kt`**:

    private val API_KEY = "PASTE_YOUR_OPENWEATHER_KEY_HERE"

### 4. Build & Run
1.  Open the project in **Android Studio**.
2.  Connect your Wear OS device via Wi-Fi Debugging (or use an Emulator).
3.  Select the device and click **Run** (▶).

---

## ⌚ How to Use

### Adding the Tile
After installing the app, the Tile does **not** appear automatically.
1.  On your watch, swipe right to access your current Tiles.
2.  Long-press any Tile to enter **Edit Mode**.
3.  Swipe to the very end of the list and tap **(+) Add Tile**.
4.  Select **"AQI Monitor"** (or "Set City") from the list.

### Setting Your City
1.  Tap the center of the AQI Tile (it may say "Setup" or "Err" initially).
2.  A text input screen will open.
3.  Type your city name (e.g., `Mumbai`, `London`) and tap **Save & Sync**.
4.  The app will fetch coordinates, save them, and refresh the Tile instantly.

---

## 🐛 Troubleshooting

**"Net Error" or "Check WIFI"**
* Ensure your watch/emulator has active internet.
* If using an Emulator: It may have lost the network bridge. Try a "Cold Boot" of the emulator.
* Check if your API keys are valid (OpenWeather keys take ~10 mins to activate).

**"401 Unauthorized"**
* This means your API Key is incorrect or not yet active. Double-check the keys in `CitySetupActivity.kt`.

**Tile doesn't update**
* Tap the tile to open the setup screen and hit "Save" again to force a refresh.

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
