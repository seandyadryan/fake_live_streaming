# LiveMuse — Live Simulator

A native Kotlin / Jetpack Compose Android app for private live-stream practice. Package: `com.deploydulupulangnanti.fakelivestream`.

## Included

- Adaptive dark UI for phones, tablets, and large Android windows.
- Creator profile, three audience levels, three visual moods, and three chat topics.
- Local simulated viewers, comments, likes, six virtual gifts, random gifts, and manual gift triggers.
- Optional CameraX front/back preview; no recording, microphone, or internet permission.
- Pause/resume, finish confirmation, session summary, and a local journal of the last 50 sessions.
- Offline privacy policy and a GitHub Pages policy in `docs/index.html`.
- English Play Store copy, brand icon, feature graphic, screenshots, and promotional materials under `store/`.
- GitHub Actions for tests/lint, debug APK, signed release APK/AAB, and manually dispatched device tests.

All audience interactions are explicitly simulated. Gifts have no cash value. The product has its own branding and is not affiliated with any social media service.

## Build

Use JDK 17+, Android SDK 36, and Android Studio. Set `sdk.dir` in ignored `local.properties`, or use `ANDROID_HOME`.

```sh
./gradlew testDebugUnitTest lintDebug assembleDebug
./gradlew connectedDebugAndroidTest
```

On Windows use `.\gradlew.bat`. Gradle 8.14, Android Gradle Plugin 8.13.0, and Kotlin 2.2.20 are pinned. Minimum Android version: 8.0 (API 26). Target: API 36.

## Release signing

The private upload keystore is stored separately at `D:\KEYSTORE\fake_live_stream.jks`. Alias: `fake_live_stream`. Keep a secure backup; it is not committed.

Use environment variables or an ignored `keystore.properties`:

```properties
KEYSTORE_PATH=/absolute/path/to/upload-key.jks
KEYSTORE_PASSWORD=your-secret
KEY_ALIAS=fake_live_stream
KEY_PASSWORD=your-secret
```

```sh
./gradlew bundleRelease assembleRelease
```

Release output:

- `app/build/outputs/bundle/release/app-release.aab`
- `app/build/outputs/apk/release/app-release.apk`
- `app/build/outputs/mapping/release/mapping.txt`

Release packaging fails if signing credentials are missing. Never put passwords, private keys, or local signing configuration into Git.

## GitHub Actions

`Android build` runs on pushes to main, version tags, pull requests, and manual dispatch. It uses ephemeral GitHub-hosted Ubuntu runners, not a persistent runner installed on a personal machine.

Required repository Actions secrets:

- `ANDROID_KEYSTORE_BASE64`: base64-encoded JKS bytes.
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

PR checks do not access release signing. Signed release artifacts are produced only after verification succeeds on non-PR events. Downloads appear in the run's Artifacts section. A separate `Android device tests` workflow runs the full Compose flow on an emulator.

The workflows build artifacts; they do not publish to Google Play. Increment versionCode and versionName for each store update.

## Screenshots

`StudioFlowTest` exercises profile editing, practice, gifts, pause, summary, recreation, history persistence, privacy, and deletion. With instrumentation argument `capture=true` it saves four actual UI screenshots under the app's external files `screenshots` directory.

```sh
adb shell am instrument -w -e capture true \
  com.deploydulupulangnanti.fakelivestream.test/androidx.test.runner.AndroidJUnitRunner
```

`tools/capture_store.ps1` captures the app in phone, 7-inch tablet, 10-inch tablet, and desktop-shaped Android viewports on a running emulator. It restores the original emulator display overrides afterward.

## Store publication

See `store/RELEASE-CHECKLIST.md`, `store/VIDEO.md`, and `store/asset-manifest.json`. The privacy policy is published from `docs/` on GitHub Pages.

The app is suitable for an upfront paid listing, but store price, countries, developer-account verification, content rating, testing requirements, and final submission are completed in the owner's Play Console. A YouTube URL must point to the actual uploaded promotional video.

## Privacy and storage

Preferences and session summaries are private local SharedPreferences. Android backup is disabled. The timer pauses while the activity is not resumed. CameraX is lifecycle-bound and the preview is unbound when leaving the live screen. The current practice survives configuration changes through a ViewModel; after process death a new studio opens rather than fabricating a continuing session.

## Ownership

Copyright © 2026 PT Deploy Dulu Pulang Nanti. All rights reserved.
Third-party dependencies remain under their respective licenses.
