package com.novasa.languagecenter.domain.model

import io.ktor.client.plugins.logging.*

class LanguageCenterConfig(
    val instance: String,
    val username: String,
    val password: String,
    val httpLogLevel: LogLevel = LogLevel.NONE
)
