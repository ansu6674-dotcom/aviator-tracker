# Aviator Live Tracker — APK Build Guide (English & हिंदी)

This project is pre-configured to connect to: `https://ais-dev-jckbtxom6wi2tybszjmmbh-454261719827.asia-east1.run.app`

## Option 1: Android Studio (PC / Mac)
1. Extract this ZIP folder.
2. Open **Android Studio** -> **Open** -> select this folder.
3. Wait for Gradle sync to finish.
4. Click **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Your APK will be created at: `app/build/outputs/apk/debug/app-debug.apk`.

## Option 2: No Android Studio? Build APK Free on GitHub Actions
1. Create a new GitHub repository and upload all files from this extracted folder (including `.github/workflows/build-apk.yml`).
2. Open the **Actions** tab in your GitHub repo.
3. Wait ~2 minutes for **Build Android APK** to finish.
4. Download **AviatorLiveTracker-debug-apk** (`app-debug.apk`) directly to your Android phone and install!
