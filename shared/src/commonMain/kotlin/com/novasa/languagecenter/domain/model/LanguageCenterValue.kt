package com.novasa.languagecenter.domain.model

interface LanguageCenterValue {
    val category: String
    val key: String
    val fallback: String
    val comment: String?
        get() = null
}
