package com.novasa.languagecenter.domain.model

sealed interface LanguageCenterStatus {

    object NotInitialized : LanguageCenterStatus {
        override fun toString() = "Not initialized"
    }

    object Updating : LanguageCenterStatus {
        override fun toString() = "Updating"
    }

    data class Ready(val language: LanguageCenterLanguage) : LanguageCenterStatus

    data class Failure(val error: Throwable) : LanguageCenterStatus
}
