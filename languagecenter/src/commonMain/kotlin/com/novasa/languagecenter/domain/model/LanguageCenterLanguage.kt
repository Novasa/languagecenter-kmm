package com.novasa.languagecenter.domain.model

data class LanguageCenterLanguage(
    val name: String,
    val codename: String
) {
    companion object {
        val UNDEFINED = LanguageCenterLanguage("", "")
    }
}
