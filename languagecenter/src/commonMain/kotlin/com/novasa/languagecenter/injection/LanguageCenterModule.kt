package com.novasa.languagecenter.injection

import com.novasa.languagecenter.LanguageCenterDatabase
import com.novasa.languagecenter.data.database.DatabaseDriverFactory
import com.novasa.languagecenter.data.repository.LanguageCenterRepository
import com.novasa.languagecenter.data.repository.LanguageCenterRepositoryImpl
import com.novasa.languagecenter.data.service.api.LanguageCenterService
import com.novasa.languagecenter.data.service.client.HttpClientFactory
import com.novasa.languagecenter.data.service.client.ServiceConfig
import com.novasa.languagecenter.data.service.impl.KermitKtorLogger
import com.novasa.languagecenter.data.service.impl.KtorLanguageCenterClientFactory
import com.novasa.languagecenter.data.service.impl.KtorLanguageCenterService
import com.novasa.languagecenter.domain.model.LanguageCenterConfig
import com.novasa.languagecenter.logging.ktorLogLevel
import com.novasa.languagecenter.platform.DispatchersFacade
import com.novasa.languagecenter.platform.Platform
import com.novasa.languagecenter.platform.SystemLanguageProvider
import io.ktor.client.plugins.logging.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val languageCenterModule = module {

    singleOf(::Platform)
    singleOf(::DispatchersFacade)
    singleOf(::SystemLanguageProvider)
    singleOf(::KermitKtorLogger) bind Logger::class

    single {
        val config: LanguageCenterConfig = getProperty(LanguageCenterKoinComponent.PROP_CONFIG)
        ServiceConfig(
            host = "language.novasa.com",
            baseUrl = "${config.instance}/api/",
            username = config.username,
            password = config.password,
            logLevel = config.httpLogLevel.ktorLogLevel
        )
    }

    singleOf(::KtorLanguageCenterClientFactory) bind HttpClientFactory::class

    single {
        val factory: HttpClientFactory = get()
        KtorLanguageCenterService(
            client = factory.create(),
            platform = get()
        )

    } bind LanguageCenterService::class

    singleOf(::DatabaseDriverFactory)
    single {
        get<DatabaseDriverFactory>().createDriver()
    }
    single {
        LanguageCenterDatabase(get())
    }

    singleOf(::LanguageCenterRepositoryImpl) bind LanguageCenterRepository::class
}
