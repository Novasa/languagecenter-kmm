package com.novasa.languagecenter.domain.model

import com.novasa.languagecenter.logging.HttpLogLevel
import kotlin.time.Duration

class LanguageCenterConfig(
    val instance: String,
    val username: String,
    val password: String,
    val periodicUpdate: Duration? = null,
    val httpLogLevel: HttpLogLevel = HttpLogLevel.NONE
)
