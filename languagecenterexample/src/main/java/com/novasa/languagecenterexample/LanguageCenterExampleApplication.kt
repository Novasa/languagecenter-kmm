package com.novasa.languagecenterexample

import android.app.Application
import com.novasa.languagecenter.LanguageCenter
import com.novasa.languagecenter.domain.model.LanguageCenterConfig
import com.novasa.languagecenter.logging.HttpLogLevel
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class LanguageCenterExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        LanguageCenter.provider.initialize(
            config = LanguageCenterConfig(
                instance = "test",
                username = "test",
                password = "test",
                periodicUpdate = 5.minutes,
                httpLogLevel = HttpLogLevel.INFO
            )
        )
    }
}
