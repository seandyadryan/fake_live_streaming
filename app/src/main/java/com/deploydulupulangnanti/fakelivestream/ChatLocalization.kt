package com.deploydulupulangnanti.fakelivestream

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList

/** Chat follows the phone's ordered system languages, independently of English store artwork. */
fun localizedChat(context: Context, locales: LocaleList = Resources.getSystem().configuration.locales): ChatContent {
    val configuration = Configuration(context.resources.configuration).apply { setLocales(locales) }
    val resources = context.createConfigurationContext(configuration).resources
    return ChatContent(
        resources.getString(R.string.chat_welcome), resources.getStringArray(R.array.chat_general).toList(),
        resources.getString(R.string.chat_music), resources.getString(R.string.chat_story),
        resources.getString(R.string.chat_daily), resources.getString(R.string.chat_you)
    )
}
