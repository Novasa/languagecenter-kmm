package com.novasa.languagecenter.platform

import platform.Foundation.*

actual class SystemLanguageProvider {

    actual val systemLanguage: String
        get() = NSLocale.preferredLanguages.firstOrNull()?.toString()?.let { identifier ->
            NSLocale.componentsFromLocaleIdentifier(identifier)[NSLocaleLanguageCode]?.toString()
        } ?: "en"
}
