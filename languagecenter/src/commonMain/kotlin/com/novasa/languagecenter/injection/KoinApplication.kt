package com.novasa.languagecenter.injection

import co.touchlab.kermit.Logger
import com.novasa.languagecenter.domain.model.LanguageCenterConfig
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.dsl.koinApplication

internal abstract class LanguageCenterKoinComponent : KoinComponent {

    companion object {
        const val PROP_CONFIG = "config"
    }

    protected abstract val config: LanguageCenterConfig

    private val application by lazy {
        Logger.d("Starting LC Koin application...")
        koinApplication {
            properties(
                mapOf(PROP_CONFIG to config)
            )
            modules(languageCenterModule)
        }
    }

    override fun getKoin(): Koin = application.koin
}
