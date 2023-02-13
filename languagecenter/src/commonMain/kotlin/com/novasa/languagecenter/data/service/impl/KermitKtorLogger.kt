package com.novasa.languagecenter.data.service.impl

import io.ktor.client.plugins.logging.*
import kotlin.math.min
import co.touchlab.kermit.Logger.Companion as KermitLogger


internal class KermitKtorLogger : Logger {

    override fun log(message: String) {
        KermitLogger.d(message.substring(0 until min(message.length, 1000)))
    }
}
