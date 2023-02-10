package com.novasa.languagecenter.extension

import com.novasa.languagecenter.domain.model.LanguageCenterValue

internal val LanguageCenterValue.fullKey: String
    get() = "${category.lowercase()}.${key.lowercase()}"

internal fun LanguageCenterValue.string() = "[category: $category, key: $key, fallback: $fallback, comment: $comment]"