package com.deploydulupulangnanti.fakelivestream

enum class ChatPrompt { WELCOME, GENERAL_0, GENERAL_1, GENERAL_2, GENERAL_3, GENERAL_4, GENERAL_5, GENERAL_6, GENERAL_7, MUSIC, STORY, DAILY }

data class ChatContent(
    val welcome: String, val general: List<String>, val music: String,
    val story: String, val daily: String, val you: String
) {
    init { require(general.size == 8) }
    fun message(prompt: ChatPrompt): String = when (prompt) {
        ChatPrompt.WELCOME -> welcome
        ChatPrompt.MUSIC -> music
        ChatPrompt.STORY -> story
        ChatPrompt.DAILY -> daily
        else -> general[prompt.ordinal - ChatPrompt.GENERAL_0.ordinal]
    }
    companion object {
        val English = ChatContent("Welcome to the LIVE! 👋", listOf(
            "Love the energy here ✨", "Hello from my little corner of the world!",
            "This is such a good vibe", "You make this look so natural", "Tell us more!",
            "That made my day ♥", "Just joined. What did I miss?", "Keep going, you've got this!"
        ), "What inspired your creative process?", "What happened next? I'm invested!",
            "What's one thing you're excited about today?", "You")
    }
}

/** Re-translate generated lines, while preserving the user's own message verbatim. */
fun LiveState.withChatLanguage(content: ChatContent) = copy(chat = chat.map { line ->
    line.copy(name = if (line.isSelf) content.you else line.name,
        message = line.prompt?.let(content::message) ?: line.message)
})
