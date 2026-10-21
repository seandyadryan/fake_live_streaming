package com.deploydulupulangnanti.fakelivestream

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

class StudioFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val model get() = ViewModelProvider(compose.activity)[StudioModel::class.java]

    private fun screenshot(name: String) {
        if (InstrumentationRegistry.getArguments().getString("capture") != "true") return
        compose.waitForIdle()
        Thread.sleep(350)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val directory = File(instrumentation.targetContext.getExternalFilesDir(null), "screenshots").apply { mkdirs() }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(directory, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }

    @Test fun completePracticeAndManageHistory() {
        compose.runOnIdle {
            model.clearHistory()
            model.configure(StudioConfig())
            model.screen = "studio"
        }
        screenshot("01-studio")
        compose.onNodeWithContentDescription("Edit profile").performScrollTo().performClick()
        compose.onNodeWithText("Creator name").performTextReplacement("Alex Morgan")
        compose.onNodeWithText("Save profile").performClick()
        compose.onNodeWithContentDescription("Use my camera").performScrollTo().performClick()
        compose.onNodeWithText("Your camera, just for you").assertIsDisplayed()
        compose.onNodeWithText("Use background").performClick()
        compose.runOnIdle { assertFalse(model.config.camera) }
        Crowd.entries.forEach { crowd ->
            compose.onNodeWithText(crowd.label).performScrollTo().performClick()
            compose.runOnIdle {
                assertEquals(crowd, model.config.crowd)
                assertEquals(crowd, StudioModel(compose.activity.application).config.crowd)
            }
        }
        compose.onNodeWithText("Your live photo").performScrollTo()
        screenshot("06-audience")
        compose.onNodeWithText("Start practice live").performScrollTo()
        screenshot("05-customize")
        compose.onNodeWithText("Start practice live").performClick()
        compose.onNodeWithContentDescription("End session").assertIsDisplayed()
        listOf("SIMULATED", "A little creativity, a lot of good vibes", "General · 10,000–100,000 viewers", "Virtual crowd · Gifts have no cash value").forEach {
            compose.onNodeWithText(it).assertDoesNotExist()
        }
        compose.onNodeWithText("Say something…").performClick().performTextInput("Hello from practice")
        compose.waitUntil(5_000) {
            ViewCompat.getRootWindowInsets(compose.activity.window.decorView)?.isVisible(WindowInsetsCompat.Type.ime()) == true
        }
        val send = compose.onNodeWithContentDescription("Send comment").assertIsDisplayed()
        val sendBottom = send.fetchSemanticsNode().boundsInWindow.bottom
        compose.runOnIdle {
            val decor = compose.activity.window.decorView
            val keyboard = ViewCompat.getRootWindowInsets(decor)!!.getInsets(WindowInsetsCompat.Type.ime()).bottom
            assertTrue("Send button must remain above the keyboard", sendBottom <= decor.height - keyboard + 1)
        }
        send.performClick()
        compose.onNodeWithText("Hello from practice").assertIsDisplayed()
        compose.runOnIdle {
            compose.activity.getSystemService(android.view.inputmethod.InputMethodManager::class.java)
                ?.hideSoftInputFromWindow(compose.activity.window.decorView.windowToken, 0)
        }
        compose.waitUntil(5_000) {
            ViewCompat.getRootWindowInsets(compose.activity.window.decorView)?.isVisible(WindowInsetsCompat.Type.ime()) != true
        }
        compose.runOnIdle {
            repeat(32) { model.tick() }
            model.gift(giftCatalog.first())
        }
        screenshot("02-live")
        compose.onNodeWithContentDescription("Open gifts").performClick()
        compose.onNodeWithText("Virtual gifts").assertIsDisplayed()
        compose.onNodeWithText("Demo values only. No cash value.").assertDoesNotExist()
        screenshot("03-gifts")
        compose.onNodeWithText("Moon gem").performClick()
        compose.onNodeWithText("Send gift").performClick()
        compose.onNodeWithText("Combo +1").performClick()
        compose.runOnIdle { assertEquals(2, model.live.gift?.combo) }
        compose.onNodeWithContentDescription("Close gifts").performClick()
        compose.onNodeWithText("Pause").performClick()
        compose.onNodeWithText("Take a breath.").assertIsDisplayed()
        compose.runOnIdle {
            val before = model.live
            model.tick()
            model.gift(giftCatalog.last())
            model.comment("Paused comment")
            assertEquals(before, model.live)
        }
        compose.onNodeWithText("Resume").performClick()
        compose.onNodeWithContentDescription("End session").performClick()
        compose.onNodeWithText("Finish session").performClick()
        compose.onNodeWithText("Look at you go.").assertIsDisplayed()
        screenshot("04-summary")
        compose.runOnIdle {
            assertEquals(1, model.history.size)
            // A new model must load the journal from disk, beyond configuration-change retention.
            val reloaded = StudioModel(compose.activity.application)
            assertEquals(model.history, reloaded.history)
        }
        compose.onNodeWithText("Back to studio").performScrollTo().performClick()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Sessions", useUnmergedTree = true).performClick()
        compose.onNodeWithText("My practice room").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, model.history.size) }
        compose.onNodeWithText("About", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Privacy policy").performScrollTo().performClick()
        compose.onNodeWithText("Close policy").performClick()
        compose.onNodeWithText("Delete session history").performScrollTo().performClick()
        compose.onNodeWithText("Delete history").performClick()
        compose.runOnIdle { assertTrue(model.history.isEmpty()) }
    }

    @Test fun photoImportPersistsNormalizesAndRemovesPrivateCopy() {
        val context = compose.activity.application
        val input = File(context.cacheDir, "source-photo.jpg")
        val bitmap = Bitmap.createBitmap(3000, 1000, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(android.graphics.Color.MAGENTA)
        input.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        bitmap.recycle()
        androidx.exifinterface.media.ExifInterface(input).apply {
            setAttribute(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, "6")
            saveAttributes()
        }
        compose.runOnIdle { model.importPhoto(android.net.Uri.fromFile(input)) }
        compose.waitUntil(15_000) { !model.importingPhoto }
        lateinit var copy: File
        compose.runOnIdle {
            assertNull(model.photoError)
            copy = requireNotNull(PhotoStore.file(context, model.config.photo))
            assertTrue(copy.exists())
            val decoded = android.graphics.BitmapFactory.decodeFile(copy.path)
            assertTrue(maxOf(decoded.width, decoded.height) <= 2048)
            assertEquals(500, decoded.width)
            assertEquals(1500, decoded.height)
            decoded.recycle()
            assertEquals(model.config.photo, StudioModel(context).config.photo)
        }
        compose.onNodeWithText("Remove photo").performScrollTo().assertIsDisplayed()
        input.writeText("invalid image")
        compose.runOnIdle { model.importPhoto(android.net.Uri.fromFile(input)) }
        compose.waitUntil(15_000) { !model.importingPhoto }
        compose.runOnIdle {
            assertNotNull(model.photoError)
            assertEquals(copy.name, model.config.photo)
            model.photoError = null
            model.removePhoto()
            assertNull(model.config.photo)
            assertFalse(copy.exists())
            assertNull(StudioModel(context).config.photo)
        }
        input.delete()
    }

    @Test fun chatResourcesFollowPhoneLanguagesWithEnglishFallback() {
        val context = compose.activity.application
        fun content(tags: String) = localizedChat(context, android.os.LocaleList.forLanguageTags(tags))
        assertEquals(ChatContent.English, content("en-US"))
        assertEquals("Selamat datang di LIVE! 👋", content("id-ID").welcome)
        assertEquals("¡Bienvenidos al LIVE! 👋", content("es-ES").welcome)
        assertEquals("أهلاً بكم في البث المباشر! 👋", content("ar-SA").welcome)
        assertEquals("LIVEへようこそ！ 👋", content("ja-JP").welcome)
        assertEquals("歡迎來到直播間！ 👋", content("zh-TW").welcome)
        assertEquals("J'adore cette énergie ✨", content("fr-FR").general.first())
        assertEquals(ChatContent.English, content("zz-ZZ"))
        assertEquals(content("id-ID"), content("zz-ZZ,id-ID,en-US"))
        val tags = listOf("id", "ms", "es", "fr", "de", "it", "pt", "ar", "hi", "bn", "ur", "ja", "ko", "zh", "zh-Hant", "ru", "tr", "vi", "th", "fil", "nl", "pl", "uk", "fa")
        tags.forEach { language ->
            val translated = content(language)
            assertNotEquals("Missing translation: $language", ChatContent.English.welcome, translated.welcome)
            assertEquals(8, translated.general.size)
            assertTrue(translated.general.all { it.isNotBlank() })
        }
        // Existing installations must not retain the retired default room title.
        val prefs = context.getSharedPreferences("studio", 0)
        val previousTitle = prefs.getString("title", null)
        try {
            prefs.edit().putString("title", "A little creativity, a lot of good vibes").commit()
            assertEquals("", StudioModel(context).config.title)
        } finally { prefs.edit().putString("title", previousTitle).commit() }
    }
}
