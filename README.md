# Dickel Zeiterfassung Android App

## Overview
This is a time tracking Android application built with Jetpack Compose and Material 3. The app allows employees to track work time, customer visits, travel, breaks, and create service reports (Serviceschein) with signatures.

## Requirements Met

### Build Configuration
- ✅ Gradle wrapper 8.10.2
- ✅ Android Gradle Plugin 8.3.0 (targeting compileSdk 35, as 36 not yet available)
- ✅ Kotlin 1.9.22
- ✅ Compose BOM 2024.12.01
- ✅ Material3 enabled
- ✅ minSdk 26, targetSdk 34
- ✅ BuildConfig fields set:
  - `API_BASE_URL` = "https://script.google.com/macros/s/AKfycbyDamcZLDF-CDCzRy_xdgIBBs71rNK_XbQLE9CbTVfY/exec/"
  - `API_KEY` = "AIzaSyBIP6a9voiLVpQ8s2gWlxjeiAMJlE20l7o"

### Network Layer
- ✅ Retrofit configured with BuildConfig.API_BASE_URL (with trailing slash)
- ✅ X-API-Key header interceptor
- ✅ Logging interceptor
- ✅ Relative endpoint paths

### UI Components (Jetpack Compose + Material 3)

#### Top Bar
- ✅ Company logo/icon only (no title text)

#### Status/Error Banners
- ✅ Error banner (red, with close button)
- ✅ Sync Error banner (red, retry + close buttons)
- ✅ Pending Events banner (secondary color, upload icon, "Sync Now" or spinner)

#### Ampel Card
- ✅ 5 status lights in a row:
  - Arbeit (green)
  - Kunde (blue)
  - Fahrt (cyan)
  - Pause (yellow)
  - Feierabend (red)
  - Inactive: grey
- ✅ State-based buttons:
  - Idle → "Arbeitszeit starten"
  - Arbeitszeit → "Kundenzeit starten", "Fahrt starten", "Pause starten", "Feierabend"
  - Kundenzeit/Fahrt/Pause → respective "... beenden" + "Feierabend"

#### Serviceschein Flow
- ✅ Kundenauswahl dialog with customer selection buttons
- ✅ Prompt after Kundenzeit stop: "Serviceschein jetzt/später"
- ✅ Serviceschein form with fields:
  - Customer (read-only, from selection)
  - Leistungen
  - Zeiten
  - Material
  - Notizen
- ✅ Signature pad (Compose Canvas with drag gestures)
- ✅ PDF generation from form + signature (using iText7)
- ✅ Email functionality via Intent for PDF sending
- ✅ Store/send status tracking

#### NFC Fallback Dialog
- ✅ Options: "Auto (Abfahrt zum Kunden)", "Firma (Einstempeln)", "Abbrechen"
- Note: Tag reading functionality excluded as per spec

#### MA-Übersicht
- ✅ Shows Benutzer-ID

#### Bottom Navigation
- ✅ Status screen (info icon)
- ✅ Material screen (list icon, placeholder)

#### Diktierfunktion
- ✅ Speech-to-text integration for form fields
- ✅ Microphone icon on each text field in Serviceschein form

### Data Layer
- ✅ Room database with entities:
  - TimeEvent (work time events)
  - Customer (customer data)
  - Serviceschein (service reports)
- ✅ DAOs for all entities
- ✅ Repositories with sync logic
- ✅ Pending sync handling

### Sync Controls
- ✅ Manual sync trigger
- ✅ Pending events counter
- ✅ Sync error display and retry

## Architecture

### Data Layer
```
data/
├── local/
│   ├── entity/         # Room entities
│   ├── dao/           # Data Access Objects
│   └── AppDatabase.kt # Room database
├── remote/
│   ├── ApiService.kt  # Retrofit API interface
│   ├── RetrofitClient.kt
│   └── model/         # API models
└── repository/        # Repository pattern implementation
```

### UI Layer
```
ui/
├── components/        # Reusable Compose components
│   ├── ActionButtons.kt
│   ├── AmpelCard.kt
│   ├── Dialogs.kt
│   ├── ServicescheinForm.kt
│   └── StatusBanners.kt
├── screens/          # Full screen composables
│   ├── StatusScreen.kt
│   └── MaterialScreen.kt
└── theme/            # Material 3 theming
```

### Utilities
```
utils/
├── PdfGenerator.kt      # PDF creation with iText7
├── EmailSender.kt       # Email via Intent
└── SpeechToTextHelper.kt # Speech recognition
```

## Building the Project

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK with API 35

### Steps
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle (should download all dependencies from Google and Maven)
4. Build and run on emulator or device

### Build Commands
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

## Key Features

### Time Tracking
- Track different work states: Arbeit, Kundenzeit, Fahrt, Pause, Feierabend
- Visual status indicator (Ampel) with colored lights
- Automatic timestamp recording
- Local storage with Room database

### Service Reports (Serviceschein)
- Create detailed service reports
- Multiple input fields with speech-to-text support
- Digital signature capture
- PDF generation with all details and signature
- Email integration for sending reports

### Sync Functionality
- Automatic background sync
- Manual sync trigger
- Pending events tracking
- Error handling and retry mechanism
- Visual feedback with status banners

### Speech-to-Text
- Voice input for all text fields in Serviceschein
- German language support
- Triggered by microphone icon next to each field

## Permissions Required
- `INTERNET` - API communication
- `ACCESS_NETWORK_STATE` - Network status checking
- `RECORD_AUDIO` - Speech-to-text functionality

## API Integration
The app communicates with a Google Apps Script backend at:
- Base URL: `https://script.google.com/macros/s/AKfycbyDamcZLDF-CDCzRy_xdgIBBs71rNK_XbQLE9CbTVfY/exec/`
- Authentication: `X-API-Key` header with value from BuildConfig

### Endpoints
- `POST /sync-event` - Sync time events
- `POST /send-serviceschein` - Send service report
- `GET /customers` - Fetch customer list

## Dependencies

### Core
- AndroidX Core KTX 1.15.0
- Lifecycle Runtime KTX 2.8.7
- Activity Compose 1.9.3

### Compose
- Compose BOM 2024.12.01
- Material3
- Material Icons Extended
- Navigation Compose 2.8.5

### Database
- Room 2.6.1 with KSP

### Network
- Retrofit 2.11.0
- Gson Converter
- OkHttp Logging Interceptor 4.12.0

### PDF & Email
- iText7 Core 7.2.5
- Android Mail 1.6.7

### Coroutines
- Kotlinx Coroutines 1.9.0

## Notes

### Build Configuration
The spec requested AGP >= 8.9.1 and compileSdk 36, but these versions are not yet available:
- Used AGP 8.3.0 (latest stable available in build environment)
- Used compileSdk 35 (API 36 not yet released by Google)
- Once newer versions are available, update `build.gradle.kts` accordingly

### NFC Functionality
NFC tag reading is explicitly excluded per specification. The NFC fallback dialog is implemented to provide manual selection of actions that would normally be triggered by tags.

### Material Screen
The Material screen is a placeholder as per specification, showing "Material und Verbrauch" with a coming soon message.

## Future Enhancements
- Actual NFC tag reading implementation
- Material management functionality
- Advanced reporting and analytics
- Offline-first sync improvements
- Widget for quick time tracking
