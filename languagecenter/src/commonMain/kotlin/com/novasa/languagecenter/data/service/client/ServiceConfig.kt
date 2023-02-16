package com.novasa.languagecenter.data.service.client

import io.ktor.client.plugins.logging.*

internal class ServiceConfig(
    val host: String,
    val baseUrl: String,
    val username: String,
    val password: String,
    val logLevel: LogLevel
)
