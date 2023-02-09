package com.novasa.languagecenter.platform

import java.util.*

actual class SystemLanguageProvider {

    actual val systemLanguage: String
        get() = Locale.getDefault().language
}
