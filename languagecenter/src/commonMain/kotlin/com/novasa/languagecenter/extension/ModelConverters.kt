package com.novasa.languagecenter.extension

import com.novasa.languagecenter.Language
import com.novasa.languagecenter.Translation
import com.novasa.languagecenter.domain.model.LanguageCenterLanguage
import com.novasa.languagecenter.domain.model.LanguageCenterTranslation

fun Language.toModel() = LanguageCenterLanguage(
    name = name,
    codename = codename
)

fun Translation.toModel() = LanguageCenterTranslation(
    key = key,
    value = value_,
    language = language
)
