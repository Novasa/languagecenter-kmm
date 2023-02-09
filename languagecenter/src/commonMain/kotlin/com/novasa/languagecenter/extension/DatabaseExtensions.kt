package com.novasa.languagecenter.extension

import com.novasa.languagecenter.InfoQueries
import com.novasa.languagecenter.Language
import com.novasa.languagecenter.LanguageQueries
import com.novasa.languagecenter.TranslationQueries
import com.novasa.languagecenter.data.dto.LanguageDto
import com.novasa.languagecenter.data.dto.TranslationDto

internal var InfoQueries.forcedLanguage: String?
    get() = get().executeAsOneOrNull()?.forcedLanguage
    set(value) = transaction {
        createInfoIfNotExists()
        setCurrentLanguage(value)
    }

private fun InfoQueries.createInfoIfNotExists() = transaction {
    if (getCount().executeAsOne() == 0L) {
        create()
    }
}

internal fun LanguageQueries.insertLanguages(languages: List<LanguageDto>) {
    transaction {
        languages.forEach {
            if (getLanguage(it.codename) == null) {
                insertLanguage(
                    codename = it.codename,
                    name = it.name,
                    is_fallback = it.isFallback,
                    timestamp = it.timestamp
                )
            } else {
                updateLanguage(
                    name = it.name,
                    is_fallback = it.isFallback,
                    timestamp = it.timestamp,
                    codename = it.codename
                )
            }
        }
    }
}

internal fun LanguageQueries.setActiveLanguage(codename: String) {
    transaction {
        val current = activeLanguage
        current?.let {
            if (it.codename != codename) {
                setLanguageActive(false, it.codename)
            }
        }
        if (codename != current?.codename) {
            setLanguageActive(true, codename)
        }
    }
}

internal val LanguageQueries.activeLanguage: Language?
    get() = getActiveLanguage().executeAsOneOrNull()

internal val LanguageQueries.fallbackLanguage: Language?
    get() = getFallbackLanguage().executeAsOneOrNull()

internal fun LanguageQueries.getLanguage(language: String) = getLanguageByCodename(language).executeAsOneOrNull()


internal fun TranslationQueries.insertTranslations(translations: List<TranslationDto>) {
    transaction {
        translations.forEach {
            insertTranslations(
                key = it.key,
                value_ = it.value,
                language = it.language
            )
        }
    }
}
