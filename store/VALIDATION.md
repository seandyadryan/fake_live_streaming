# Release validation — October 21, 2026

Application source: `fed2851` (subsequent delivery commits contain assets and documentation).

| Check | Result | Evidence |
| --- | --- | --- |
| Five unit tests, lint, debug build, signed APK/AAB | PASS | [Android build](https://github.com/seandyadryan/fake_live_streaming/actions/runs/39541927303) |
| Two instrumented flows on four viewports | PASS | [Device tests](https://github.com/seandyadryan/fake_live_streaming/actions/runs/39541951265) |
| Actual signed, minified APK installation, Studio and live startup | PASS | [Production smoke test](https://github.com/seandyadryan/fake_live_streaming/actions/runs/39542527892) |
| APK/AAB signatures | PASS | apksigner verification and jarsigner verification; original upload certificate |
| Native ELF and APK ZIP alignment | PASS | Six 64-bit libraries aligned to at least 16 KB; zipalign check passed |
| Store image dimensions and file limits | PASS | 26 PNGs, including 24 screenshots; asset-manifest.json |
| English description limits | PASS | 78 / 80 and 2,770 / 4,000 characters |
| Promotional video | PASS | H.264, 1920 × 1080, 30 fps, 20 seconds, silent; full decode checked |

Package: `com.deploydulupulangnanti.fakelivestream`.
Version: `1.1.0` (versionCode 2). Minimum API: 26. Target and compile API: 36.

The unit tests cover 100,000 simulated seconds for each of four viewer ranges, bounded chat memory, automatic/manual gifts, combo growth/reset, comment length and whitespace, and audience-dependent chat rate. Viewer counts and peaks remain inside each selected inclusive range.

API 36 x86_64 device automation covers all four viewer choices and their saved preferences, profile editing, optional-camera explanation cancellation, live startup, typing and sending a comment with the software keyboard visible, verifying the Send button stays above the keyboard, gift selection and combo, pause/resume, finish confirmation, summaries, activity recreation, stored history, privacy, and history deletion. Six actual screenshots are captured per viewport.

A separate photo flow imports a 3,000 × 1,000 JPEG tagged with 90-degree EXIF orientation, verifies its private normalized copy is 500 × 1,500, reloads the saved selection, rejects invalid image data without replacing the previous copy, and verifies removal deletes the file and saved reference.

AndroidX ExifInterface is used for orientation parsing. Explicit backup and device-transfer exclusions cover private files and preferences. References: [AndroidX ExifInterface](https://developer.android.com/jetpack/androidx/releases/exifinterface), [Android backup configuration](https://developer.android.com/identity/data/autobackup).

Production smoke testing installs the CI-signed, R8-minified APK and verifies Studio and the live room render while the process remains alive without a fatal crash.

Signing certificate subject: `CN=PT Deploy Dulu Pulang Nanti, O=PT Deploy Dulu Pulang Nanti, C=ID`.
Certificate SHA-256: `cf344f01ac9a59fdcb6efca56cc5190c25da7d54faa0de977d716b1279277cca`.

Delivery packaging checks APK/AAB signatures, native alignment, image limits, promo decoding and archive checksums. The private upload keystore and passwords are excluded from Git and the ZIP.

Physical camera hardware, manufacturer-specific behavior, and Play Console account requirements remain device/account checks before production publication. No Play Store submission or YouTube upload is performed.
