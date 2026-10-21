package com.deploydulupulangnanti.fakelivestream

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class StudioModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("studio", 0)
    private val simulator = Simulator()
    var config by mutableStateOf(loadConfig()); private set
    var history by mutableStateOf(loadHistory()); private set
    var screen by mutableStateOf("studio")
    var live by mutableStateOf(LiveState()); private set
    var paused by mutableStateOf(false)
    var latest by mutableStateOf<SessionRecord?>(null); private set
    var importingPhoto by mutableStateOf(false); private set
    var photoError by mutableStateOf<String?>(null)
    private var chatLanguageTags: String? = null
    private var chatContent = ChatContent.English

    fun refreshChatLanguage() {
        val locales = android.content.res.Resources.getSystem().configuration.locales
        val tags = locales.toLanguageTags()
        if (tags == chatLanguageTags) return
        chatContent = localizedChat(getApplication(), locales)
        chatLanguageTags = tags
        live = live.withChatLanguage(chatContent)
    }

    fun importPhoto(uri: Uri) {
        if (importingPhoto) return
        importingPhoto = true
        viewModelScope.launch {
            try {
                val name = withContext(Dispatchers.IO) { PhotoStore.import(getApplication(), uri) }
                val old = PhotoStore.file(getApplication(), config.photo)
                configure(config.copy(photo = name, camera = false))
                withContext(Dispatchers.IO) { old?.delete() }
            } catch (_: Exception) { photoError = "Could not open this photo. Choose a JPEG, PNG or WebP image under 20 MB." }
            finally { importingPhoto = false }
        }
    }
    fun removePhoto() {
        PhotoStore.file(getApplication(), config.photo)?.delete()
        configure(config.copy(photo = null))
    }

    fun configure(next: StudioConfig) {
        config = next.copy(name = next.name.take(32), title = next.title.take(80))
        prefs.edit().putString("name", config.name).putString("title", config.title)
            .putString("crowd", config.crowd.name).putString("scene", config.scene.name)
            .putString("topic", config.topic.name).putBoolean("gifts", config.randomGifts).putString("photo", config.photo).apply()
    }
    fun start() { refreshChatLanguage(); live = simulator.initial(config, chatContent); paused = false; screen = "live" }
    fun tick() { refreshChatLanguage(); if (screen == "live" && !paused) live = simulator.step(live, config, chatContent) }
    fun gift(gift: Gift) { if (!paused) live = simulator.sendGift(live, gift) }
    fun comment(text: String) { refreshChatLanguage(); if (!paused) live = simulator.comment(live, text, chatContent) }
    fun like() { if (!paused) live = live.copy(likes = live.likes + 1) }
    fun finish() {
        if (screen != "live") return
        val record = SessionRecord(System.currentTimeMillis(), config.title.ifBlank { "My practice room" }, live.seconds, live.peak, live.likes, live.gifts)
        latest = record
        history = (listOf(record) + history).take(50)
        persistHistory()
        screen = "summary"
    }
    fun clearHistory() { history = emptyList(); latest = null; persistHistory() }
    private fun persistHistory() {
        val array = JSONArray()
        history.forEach { r -> array.put(JSONObject().put("date", r.date).put("title", r.title).put("seconds", r.seconds).put("peak", r.peak).put("likes", r.likes).put("gifts", r.gifts)) }
        prefs.edit().putString("history", array.toString()).apply()
    }
    private fun loadConfig() = StudioConfig(
        name = prefs.getString("name", "Alex Morgan")!!,
        title = prefs.getString("title", "").orEmpty().takeUnless { it == "A little creativity, a lot of good vibes" }.orEmpty(),
        crowd = runCatching { Crowd.valueOf(prefs.getString("crowd", "")!!) }.getOrDefault(Crowd.BUZZING),
        scene = runCatching { Scene.valueOf(prefs.getString("scene", "")!!) }.getOrDefault(Scene.AURORA),
        topic = runCatching { Topic.valueOf(prefs.getString("topic", "")!!) }.getOrDefault(Topic.CHAT),
        randomGifts = prefs.getBoolean("gifts", true),
        photo = prefs.getString("photo", null)?.takeIf { PhotoStore.file(getApplication(), it) != null }
    )
    private fun loadHistory(): List<SessionRecord> = runCatching {
        val a = JSONArray(prefs.getString("history", "[]"))
        List(a.length()) { i ->
            val o = a.getJSONObject(i)
            SessionRecord(o.getLong("date"), o.getString("title"), o.getInt("seconds"), o.getInt("peak"), o.getInt("likes"), o.getInt("gifts"))
        }
    }.getOrDefault(emptyList())
}
