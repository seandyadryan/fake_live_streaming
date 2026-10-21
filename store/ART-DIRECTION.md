# LiveMuse art direction

Brand: midnight navy, soft violet, mint, and coral. Native icon geometry combines an L, a play triangle, and a small signal dot. No third-party platform identity is used.

The Aurora background was created with the built-in imagegen tool and copied into `app/src/main/res/drawable-nodpi/studio_aurora.png`.

Final image generation prompt:

> Create one premium abstract background illustration for an Android live-stream rehearsal app named LiveMuse. Portrait 9:16 composition. Deep midnight navy background, flowing translucent violet and electric blue luminous silk ribbons forming a subtle abstract wave, a few tiny coral-pink sparkles, rich soft cinematic volumetric glow. Elegant, spacious, clean, no text, no logos, no people, no UI, no device frame, no brands. Central and lower areas should be dark enough for white UI overlays. This is the actual local Studio backdrop in the product, not a promotional mockup.

Store icon and feature graphic are deterministic brand artwork rendered by `tools/create_store_assets.py`. Screenshots are captured from the running Android app through the instrumented test; they are not generated UI mockups.

## Virtual gift illustrations (1.1.0)

Six separate transparent raster illustrations were generated using the built-in imagegen tool. Their alpha channels are preserved in the 320 px app assets `app/src/main/res/drawable-nodpi/gift_*.png`. Moon gem and Spotlight use the app's native vector icon system. No reference-platform artwork was copied.

Final prompt template (one built-in call per subject):

> Use case: stylized-concept. Asset type: single original virtual gift icon for LiveMuse Android app. Subject: {subject}. Premium charming glossy 3D toy illustration, soft studio lighting, saturated colors, bold recognizable silhouette at 64px. Exactly ONE centered isolated object, generous transparent padding, square composition. No text, no frame, no logo, no platform branding. Transparent background.

Subjects:

- Rose: a single pink rose blossom with a short green stem and two leaves.
- Coffee: a warm latte in a rounded ivory ceramic cup with caramel foam.
- Sweet heart: a glossy coral pink heart with tiny gold sparkles.
- Crown: a playful gold crown with lavender jewels.
- Comet: a turquoise shooting star with a lavender sparkling trail.
- Bloom: a lavender daisy flower with a sunny yellow center.
