package com.novasa.languagecenter.platform

import platform.Foundation.NSLocaleLanguageCode

actual class SystemLanguageProvider {

    actual val systemLanguage: String
        get() = NSLocaleLanguageCode ?: ""

}
