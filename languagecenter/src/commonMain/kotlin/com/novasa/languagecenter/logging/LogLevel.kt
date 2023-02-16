package com.novasa.languagecenter.logging

import io.ktor.client.plugins.logging.*

enum class HttpLogLevel {
    ALL,
    HEADERS,
    BODY,
    INFO,
    NONE
}

internal val HttpLogLevel.ktorLogLevel: LogLevel
    get() = when (this) {
        HttpLogLevel.ALL -> LogLevel.ALL
        HttpLogLevel.HEADERS -> LogLevel.HEADERS
        HttpLogLevel.BODY -> LogLevel.BODY
        HttpLogLevel.INFO -> LogLevel.INFO
        HttpLogLevel.NONE -> LogLevel.NONE
    }
