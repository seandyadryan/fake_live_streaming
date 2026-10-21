package com.deploydulupulangnanti.fakelivestream

import kotlin.random.Random

enum class Crowd(val label: String, val min: Int, val max: Int, val base: Int, val interval: Int) {
    COZY("0–100", 0, 100, 24, 5),
    BUZZING("100–1,000", 100, 1_000, 420, 3),
    SPOTLIGHT("1,000–10,000", 1_000, 10_000, 2_400, 2),
    VIRAL("10,000–100,000", 10_000, 100_000, 24_000, 1)
}
enum class Scene(val label: String) { AURORA("Aurora"), SUNSET("Sunset"), MIDNIGHT("Midnight") }
enum class Topic(val label: String) { CHAT("Just chatting"), MUSIC("Music & creativity"), STORY("Story time") }
data class StudioConfig(
    val name: String = "Alex Morgan",
    val title: String = "A little creativity, a lot of good vibes",
    val crowd: Crowd = Crowd.BUZZING,
    val scene: Scene = Scene.AURORA,
    val topic: Topic = Topic.CHAT,
    val randomGifts: Boolean = true,
    val camera: Boolean = false,
    val photo: String? = null
)
data class Gift(val id: String, val symbol: String, val name: String, val color: Long, val coins: Int, val premium: Boolean = false)
val giftCatalog = listOf(
    Gift("rose", "✿", "Rose", 0xFFFF7696, 1),
    Gift("coffee", "☕", "Coffee", 0xFFFFD185, 5),
    Gift("heart", "♥", "Sweet heart", 0xFFFF7696, 10),
    Gift("bloom", "✿", "Bloom", 0xFFA998FF, 20),
    Gift("comet", "☄", "Comet", 0xFF83B8FF, 100, true),
    Gift("crown", "♛", "Crown", 0xFFFFD185, 500, true),
    Gift("gem", "◆", "Moon gem", 0xFF69E7D2, 250, true),
    Gift("spark", "✦", "Spotlight", 0xFFFFD185, 1_000, true)
)
data class ChatLine(val id: Int, val name: String, val message: String)
data class GiftEvent(val id: Int, val sender: String, val gift: Gift, val combo: Int = 1)
data class LiveState(
    val seconds: Int = 0,
    val viewers: Int = 0,
    val peak: Int = 0,
    val likes: Int = 0,
    val gifts: Int = 0,
    val comments: Int = 0,
    val chat: List<ChatLine> = emptyList(),
    val gift: GiftEvent? = null
)
data class SessionRecord(val date: Long, val title: String, val seconds: Int, val peak: Int, val likes: Int, val gifts: Int)

class Simulator(private val random: Random = Random.Default) {
    private val names = listOf("mia.creates", "Kai", "Sofia", "noah.wav", "Luna", "Oliver", "Amara", "Theo", "Isla", "Aria")
    private val general = listOf("Love the energy here ✨", "Hello from my little corner of the world!", "This is such a good vibe", "You make this look so natural", "Tell us more!", "That made my day ♥", "Just joined. What did I miss?", "Keep going, you've got this!")
    fun initial(config: StudioConfig) = LiveState(viewers = config.crowd.base, peak = config.crowd.base,
        chat = listOf(ChatLine(0, "LiveMuse", "Your practice room is ready. Everyone here is simulated.")))

    fun step(state: LiveState, config: StudioConfig): LiveState {
        val seconds = state.seconds + 1
        val crowd = config.crowd
        val jitter = maxOf(1, (crowd.max - crowd.min) / 80)
        val viewers = (state.viewers + random.nextInt(-jitter, jitter + 1)).coerceIn(crowd.min, crowd.max)
        val newChat = seconds % config.crowd.interval == 0
        val message = when {
            seconds % 4 != 0 -> general.random(random)
            config.topic == Topic.MUSIC -> "What inspired your creative process?"
            config.topic == Topic.STORY -> "What happened next? I'm invested!"
            else -> "What's one thing you're excited about today?"
        }
        var next = state.copy(
            seconds = seconds, viewers = viewers, peak = maxOf(state.peak, viewers),
            likes = state.likes + random.nextInt(1, 7) * (config.crowd.ordinal + 1),
            comments = state.comments + if (newChat) 1 else 0,
            chat = if (newChat) (state.chat + ChatLine(seconds, names.random(random), message)).takeLast(5) else state.chat,
            gift = if (seconds % 7 == 0) null else state.gift
        )
        if (config.randomGifts && seconds % 9 == 0) next = sendGift(next, giftCatalog.random(random))
        return next
    }

    fun sendGift(state: LiveState, gift: Gift) = state.copy(
        gifts = state.gifts + 1,
        gift = GiftEvent(state.gifts + 1, if (state.gift?.gift?.id == gift.id) state.gift.sender else names.random(random), gift,
            if (state.gift?.gift?.id == gift.id) state.gift.combo + 1 else 1)
    )
    fun comment(state: LiveState, text: String): LiveState {
        if (text.isBlank()) return state
        return state.copy(comments = state.comments + 1, chat = (state.chat + ChatLine(-state.comments - 1, "You", text.trim().take(160))).takeLast(5))
    }
}
fun duration(seconds: Int) = "%02d:%02d".format(seconds / 60, seconds % 60)
fun compact(value: Int): String = if (value >= 10_000) "%.1fk".format(java.util.Locale.US, value / 1000f) else java.text.NumberFormat.getIntegerInstance(java.util.Locale.US).format(value)
