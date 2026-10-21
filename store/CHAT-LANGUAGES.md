# Chat language behavior

LiveMuse follows the phone's ordered system language list. Android selects the first matching bundled translation, with English as the default when no supported language matches. No internet or translation service is used.

Language splitting is disabled for the Play Store App Bundle so all chat translations remain installed and usable offline after the phone language changes.

Supported chat languages: English, Indonesian, Malay, Spanish, French, German, Italian, Portuguese, Arabic, Hindi, Bengali, Urdu, Japanese, Korean, Simplified Chinese, Traditional Chinese, Russian, Turkish, Vietnamese, Thai, Filipino, Dutch, Polish, Ukrainian, and Persian.

Localized content includes the welcome, eight general comment templates, topic-specific prompts, the self label, comment-input hint, send accessibility label, and chat heading. User messages and creator names are never translated. Generated lines store their template identity so they can be re-rendered after a system-language change, including while a session is paused.

The English Play Store artwork remains English. Chat localization does not claim that every app screen is translated.

Edit the translation source in `tools/generate_chat_resources.py`, then run that script to regenerate the native `res/values*/chat.xml` resources. Every translation must contain eight general comments and all scalar chat strings.
