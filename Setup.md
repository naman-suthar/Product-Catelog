# Setup Guide

## 1. Prerequisites
- Android Studio Meerkat (2024.3) or newer
- JDK 17 (Android Studio bundled JDK works)
- Android SDK installed for:
  - `compileSdk 36`
  - Device/emulator with API 24+ (`minSdk 24`)
- Stable internet for first-time Gradle sync

## 2. Clone Repository
```bash
git clone https://github.com/naman-suthar/Product-Catelog.git
cd Product-Catelog
```

## 3. Open in Android Studio
1. Open Android Studio
2. `File > Open` and select the project root
3. Wait for Gradle sync to finish
4. Install any missing SDK components if prompted

## 4. Local Configuration
- `local.properties` is usually generated automatically by Android Studio.
- If required, ensure `sdk.dir` is correctly set in `local.properties`.

## 5. Run the App (Android Studio)
1. Select `app` run configuration
2. Choose an emulator or connected physical device (API 24+)
3. Click **Run**

## 6. Build & Verify (CLI)
```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

## 7. Optional Offline Validation
1. Launch app while online and browse products/details (to warm cache)
2. Disable internet
3. Relaunch app and verify cached data/images still appear

## 8. Troubleshooting
- Gradle sync issues: `File > Invalidate Caches / Restart`
- SDK mismatch: install required SDK/platform/build tools in SDK Manager
- Network/proxy issues: verify internet/proxy configuration
- Stale build artifacts:
```bash
./gradlew clean
```

## 9. Notes
- No API key required (DummyJSON public API)
- CI checks include:
  - `lintDebug`
  - `testDebugUnitTest`
  - `assembleDebug`

