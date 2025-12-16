# Building the Dickel Android App

## Current Build Status
The Android app structure is complete with all required functionality implemented. However, the sandbox build environment has restricted network access that prevents downloading the Android Gradle Plugin from dl.google.com.

## Build Requirements
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API Level 35
- Internet access to download dependencies

## Building in Android Studio

### Option 1: Using Android Studio (Recommended)
1. **Open the project in Android Studio**
   - File → Open → Select the Dickel folder
   - Android Studio will automatically detect the Gradle project

2. **Sync Gradle**
   - Android Studio will prompt to sync Gradle
   - Click "Sync Now" in the banner that appears
   - This will download all required dependencies from:
     - Google Maven Repository (dl.google.com)
     - Maven Central
     - Gradle Plugin Portal

3. **Build the project**
   - Build → Make Project (Ctrl+F9 / Cmd+F9)
   - Or use: Build → Build Bundle(s) / APK(s) → Build APK(s)

4. **Run on emulator or device**
   - Select a device/emulator from the device dropdown
   - Click the Run button (green triangle)

### Option 2: Command Line Build
Once Gradle dependencies are synced (either through Android Studio or by having proper internet access), you can build from command line:

```bash
# Assemble debug APK
./gradlew assembleDebug

# Assemble release APK
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
./gradlew connectedAndroidTest
```

## Troubleshooting Network Issues

### Problem: Cannot download Android Gradle Plugin
**Error**: `Could not GET 'https://dl.google.com/dl/android/maven2/...'`

**Solution**: This happens in restricted network environments. Try:

1. **Use Android Studio**: It often has better network handling
2. **Check proxy settings**: File → Settings → Appearance & Behavior → System Settings → HTTP Proxy
3. **Gradle properties**: Add to `gradle.properties`:
   ```properties
   systemProp.http.proxyHost=your.proxy.host
   systemProp.http.proxyPort=8080
   systemProp.https.proxyHost=your.proxy.host
   systemProp.https.proxyPort=8080
   ```
4. **Use local Maven repository**: If you have dependencies cached locally

### Problem: Build fails with "Plugin not found"
This occurs when Gradle cannot resolve the Android Gradle Plugin from the repositories.

**Solution**: Ensure your `settings.gradle.kts` includes:
```kotlin
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
```

## Updating AGP and SDK Versions

The specification requested AGP >= 8.9.1 and compileSdk 36, which are not yet available. Current configuration:
- AGP: 8.3.0
- compileSdk: 35

To update when newer versions are available:

1. **Update build.gradle.kts** (root):
   ```kotlin
   buildscript {
       dependencies {
           classpath("com.android.tools.build:gradle:8.9.1")  // Update version
       }
   }
   ```

2. **Update app/build.gradle.kts**:
   ```kotlin
   android {
       compileSdk = 36  // Update SDK version
   }
   ```

3. **Sync Gradle** and rebuild

## Known Limitations in Sandbox Environment
- Cannot access dl.google.com (Google's Maven repository)
- Cannot build APK without network access
- Cannot run emulator in headless CI environment

## Next Steps After Successful Build
1. Run the app on an emulator or device
2. Test time tracking flows
3. Test Serviceschein creation and PDF generation
4. Verify speech-to-text functionality (requires microphone permission)
5. Test sync functionality with the backend API
6. Review UI on different screen sizes

## Dependencies That Will Be Downloaded
When the build succeeds, Gradle will download:
- Android Gradle Plugin (8.3.0) - ~130 MB
- Kotlin compiler and stdlib - ~50 MB
- AndroidX libraries - ~100 MB
- Compose libraries - ~80 MB
- Room, Retrofit, and other dependencies - ~50 MB
- **Total**: ~400-500 MB of dependencies

This is normal for an Android project and only needs to be downloaded once (cached in `~/.gradle/caches`).
