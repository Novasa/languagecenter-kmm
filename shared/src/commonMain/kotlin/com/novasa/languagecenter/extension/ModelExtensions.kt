package com.novasa.languagecenter.extension

import com.novasa.languagecenter.domain.model.LanguageCenterValue

internal fun LanguageCenterValue.fullKey() = "${category.lowercase()}.${key.lowercase()}"

internal fun LanguageCenterValue.string() = "[category: $category, key: $key, fallback: $fallback, comment: $comment]"