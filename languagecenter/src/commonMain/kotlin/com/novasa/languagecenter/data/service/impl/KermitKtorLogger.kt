package com.novasa.languagecenter.data.service.impl

import io.ktor.client.plugins.logging.*
import kotlin.math.min
import co.touchlab.kermit.Logger as KermitLogger


internal class KermitKtorLogger(
    private val logger: KermitLogger
) : Logger {

    override fun log(message: String) {
        logger.d(message.substring(0 until min(message.length, 1000)))
    }
}
