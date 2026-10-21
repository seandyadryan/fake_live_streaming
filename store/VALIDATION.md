# Release validation — October 21, 2026

Version: `1.2.0` (versionCode 3). Package: `com.deploydulupulangnanti.fakelivestream`.
Minimum API: 26. Target and compile API: 36.

Android/chat implementation: `351df83`. App Bundle language-delivery configuration: `f03c029`. Subsequent delivery commits contain screenshots, promo, packaging checks and documentation.

| Check | Result | Evidence |
| --- | --- | --- |
| Six unit tests, lint, debug build and signed APK/AAB | PASS | [Android build](https://github.com/seandyadryan/fake_live_streaming/actions/runs/39547017330) |
| Three instrumented flows on four viewports | PASS | [Device tests](https://github.com/seandyadryan/fake_live_streaming/actions/runs/39546437364) |
| Actual signed, minified APK installation, Studio and live startup | PASS | [Production smoke test](https://github.com/seandyadryan/fake_live_streaming/actions/runs/39547541488) |
| APK/AAB signatures | PASS | apksigner and jarsigner verification; original upload certificate |
| Native ELF and APK ZIP alignment | PASS | Six 64-bit libraries aligned to at least 16 KB; zipalign passed |
| Offline language delivery in AAB | PASS | BundleConfig.pb: LANGUAGE split dimension has negate=true |
| Store image dimensions and file limits | PASS | 26 PNGs, including 24 screenshots; asset-manifest.json |
| English description limits | PASS | 78 / 80 and 3,023 / 4,000 characters |
| Promotional video | PASS | H.264, 1920 × 1080, 30 fps, 20 seconds, silent; full decode checked |

The six unit tests cover 100,000 simulated seconds for each viewer range, bounded chat memory, automatic/manual gifts, combos, comment limits, audience-dependent chat rates, and changing the language of generated messages while preserving user-written text and translating the self label.

API 36 x86_64 device tests cover the full practice flow, software-keyboard input, viewer preferences, gifts and combos, pause/resume, history, privacy and deletion. They also assert that the retired live badge/title/topic/range/footer and gift-sheet cash-value caption are absent. Six actual screenshots are captured in each of four viewports.

The photo test verifies EXIF rotation, downsampling, persisted selection, rejection of invalid data without losing the previous image, and removal of the private copy.

The localization test resolves all 25 bundled language variants using Android resources, verifies exact Indonesian/Spanish/Arabic/Japanese/Traditional Chinese/French text, English fallback for an unsupported locale, ordered language-preference fallback, and migration of the retired default room title.

All chat language resources ship together for offline phone-language changes. The packaging validator reads the delivered AAB's BundleConfig.pb, following [Google's bundletool schema](https://github.com/google/bundletool/blob/master/src/main/proto/config.proto). As a negative control, the validator rejected the prior 1.1.0 bundle that allowed language splitting.

Production smoke testing installs the actual CI-signed R8-minified APK and confirms Studio and live render, the retired badge is absent, and the process stays alive without a fatal crash. The final manifest has camera access and an app-defined internal receiver permission; no internet, microphone or shared-storage permission.

Signing certificate: `CN=PT Deploy Dulu Pulang Nanti, O=PT Deploy Dulu Pulang Nanti, C=ID`.
SHA-256: `cf344f01ac9a59fdcb6efca56cc5190c25da7d54faa0de977d716b1279277cca`.

The delivery ZIP includes binary checksums and excludes the private keystore and passwords. Physical camera behavior and Play Console account requirements remain device/account checks before production publication. No Play Store submission or YouTube upload is performed.
