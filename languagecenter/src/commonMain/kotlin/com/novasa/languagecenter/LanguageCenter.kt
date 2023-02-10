package com.novasa.languagecenter

import com.novasa.languagecenter.domain.impl.LanguageCenterProviderImpl
import com.novasa.languagecenter.domain.provider.LanguageCenterProvider

object LanguageCenter {
    val provider: LanguageCenterProvider = LanguageCenterProviderImpl()
}
