package com.novasa.languagecenterexample

import android.app.Application
import com.novasa.languagecenter.LanguageCenter
import com.novasa.languagecenter.domain.model.LanguageCenterConfig

class LanguageCenterExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        LanguageCenter {
            initialize(
                LanguageCenterConfig(
                    instance = "test",
                    username = "test",
                    password = "test"
                )
            )
        }
    }
}