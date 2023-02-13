package com.novasa.languagecenterexample

import android.app.Application
import com.novasa.languagecenter.LanguageCenter
import com.novasa.languagecenter.domain.model.LanguageCenterConfig

class LanguageCenterExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        LanguageCenter.provider.initialize(
            config = LanguageCenterConfig(
                instance = "test",
                username = "test",
                password = "test"
            )
        )
    }
}