# Google Play release handoff

- App title: **LiveMuse — Live Simulator** (25 characters).
- Developer: **PT Deploy Dulu Pulang Nanti**.
- Package: `com.deploydulupulangnanti.fakelivestream`.
- Category suggestion: Entertainment.
- Language: English (global listing).
- Short and full descriptions: adjacent UTF-8 text files.
- Distribution: a paid upfront app is supported; set price and countries in Play Console. No billing SDK is required for the initial store purchase. No advertising or in-app purchases are implemented.
- Upload the signed `app-release.aab`, not a debug APK. Keep the upload key backed up privately.
- Enroll in Play App Signing and use this key as the upload key.
- Complete content rating, target audience, app access, Data safety, pricing, tax/payment profile, and testing requirements using the actual app behavior and your developer account's requirements.
- App access: no login required. Camera permission is optional.
- Data safety draft: no user data collected or shared by the app. Camera is used only on-device for preview. No microphone or internet permission. Confirm current Play definitions before submission; support emails and store transactions occur outside the app.
- Privacy URL: `https://seandyadryan.github.io/fake_live_streaming/`.
- Test camera grant, denial, permanent denial, front/rear camera availability, background/foreground, rotation, large fonts, tablets, and release APK on physical devices before production.
- Screenshots in this repository come from the actual Android app; all metrics are fictional simulation output.
- Desktop screenshots show the Android app in a large landscape viewport, not a separate Windows/macOS app. Verify device eligibility if distributing on ChromeOS or other desktop Android environments.
- Store video: a local silent promotional MP4 and upload instructions are provided. A public/unlisted YouTube URL requires upload through the owner's YouTube account. Do not substitute an invented URL.
- Publication and approval on Google Play are not performed by the GitHub build pipeline.

References checked October 20, 2026:
- https://developer.android.com/google/play/requirements/target-sdk
- https://support.google.com/googleplay/android-developer/answer/9888374
- https://support.google.com/googleplay/android-developer/answer/9866151
