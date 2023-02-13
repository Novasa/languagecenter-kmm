package com.novasa.languagecenter.extension

import com.novasa.languagecenter.domain.model.LanguageCenterValue

internal val LanguageCenterValue.key: String
    get() = "$category.$id"

internal fun LanguageCenterValue.string() = "[category: $category, id: $id, fallback: $fallback, comment: $comment]"
