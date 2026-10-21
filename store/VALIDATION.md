# Release validation — October 21, 2026

Application source: `c900e43` (subsequent delivery commits contain assets and documentation).

| Check | Result | Evidence |
| --- | --- | --- |
| Unit tests, lint, debug build, signed release APK/AAB | PASS | [Android build](https://github.com/seandyadryan/fake_live_streaming/actions/runs/39533524814) |
| Full Compose UI flow on four viewports | PASS | [Device tests](https://github.com/seandyadryan/fake_live_streaming/actions/runs/39533528766) |
| Signed and minified production APK installation, Studio, and live-room startup | PASS | [Production smoke test](https://github.com/seandyadryan/fake_live_streaming/actions/runs/39534143632) |
| APK signature | PASS | APK Signature Scheme v2; one signer |
| AAB signature | PASS | `jarsigner -verify` |
| 64-bit native libraries | PASS | All six ELF libraries have LOAD alignment of at least 16 KB |
| APK native ZIP alignment | PASS | `zipalign -c -P 16 4` |
| Store artwork dimensions, formats, size limits, and counts | PASS | `asset-manifest.json`; 22 PNGs, including 20 screenshots |
| English description lengths | PASS | 78 / 80 and 2,332 / 4,000 characters |
| Promotional video | PASS | H.264, 1920 × 1080, 30 fps, 20 seconds, silent; complete FFmpeg decode |

Package: `com.deploydulupulangnanti.fakelivestream`.
Version: `1.0.0` (versionCode 1). Minimum API: 26. Target and compile API: 36.

The four unit tests cover long sessions and bounded chat memory, automatic/manual gift behavior, comment length and whitespace handling, and audience-dependent chat rate.

Device automation uses API 36 x86_64 emulators. The full UI flow covers profile editing, optional-camera explanation cancellation, starting practice, typing and sending a comment with the software keyboard visible, verifying the send button stays above the keyboard, gifts, pause/resume, finish confirmation, summaries, activity recreation, stored history, privacy, and history deletion. It captures five screenshots per viewport.

Production smoke testing installs the actual CI-signed, R8-minified APK and verifies Studio and the live room render while the process remains alive without a fatal crash.

The final manifest requests camera access and an app-defined internal receiver permission. It contains no internet, microphone, advertising ID, or shared-storage permission.

Signing certificate subject: `CN=PT Deploy Dulu Pulang Nanti, O=PT Deploy Dulu Pulang Nanti, C=ID`.

Certificate SHA-256: `cf344f01ac9a59fdcb6efca56cc5190c25da7d54faa0de977d716b1279277cca`.

SHA-256 checksums for the packaged APK, AAB, and mapping are included inside the delivery ZIP. The private upload keystore and passwords are excluded from Git and the ZIP.

Physical camera hardware, manufacturer-specific behavior, and the owner's Play Console account requirements remain device/account checks before production publication. No Play Store submission or YouTube upload is performed.
