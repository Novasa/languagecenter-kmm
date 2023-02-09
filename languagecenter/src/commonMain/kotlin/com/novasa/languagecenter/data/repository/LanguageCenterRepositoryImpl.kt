package com.novasa.languagecenter.data.repository

import co.touchlab.kermit.Logger
import com.novasa.languagecenter.LanguageCenterDatabase
import com.novasa.languagecenter.Translation
import com.novasa.languagecenter.data.dto.LanguageDto
import com.novasa.languagecenter.data.service.api.LanguageCenterService
import com.novasa.languagecenter.domain.model.LanguageCenterLanguage
import com.novasa.languagecenter.domain.model.LanguageCenterTranslation
import com.novasa.languagecenter.domain.model.LanguageCenterValue
import com.novasa.languagecenter.extension.*
import com.novasa.languagecenter.platform.DispatchersFacade
import com.novasa.languagecenter.platform.SystemLanguageProvider
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

internal class LanguageCenterRepositoryImpl(
    private val service: LanguageCenterService,
    private val database: LanguageCenterDatabase,
    private val dispatchers: DispatchersFacade,
    private val systemLanguageProvider: SystemLanguageProvider

) : LanguageCenterRepository {

    private val preferredLanguage: String
        get() = database.infoQueries.forcedLanguage
            ?: systemLanguageProvider.systemLanguage

    override val activeLanguage: Flow<LanguageCenterLanguage> by lazy {
        database.languageQueries.getActiveLanguage()
            .asFlow()
            .mapToOneOrNull()
            .filterNotNull()
            .onEach { Logger.d("Active language updated: $it") }
            .map { it.toModel() }
    }

    override val translations: Flow<Map<String, LanguageCenterTranslation>> by lazy {
        database.translationQueries.getActiveTranslations()
            .toModelFlow()
            .map { translations ->
                if (database.languageQueries.activeLanguage?.codename != database.languageQueries.fallbackLanguage?.codename) {
                    translations.toMutableMap().apply {
                        database.translationQueries.getFallbackTranslations()
                            .executeAsList()
                            .forEach {
                                if (!containsKey(it.key)) {
                                    put(it.key, it.toModel())
                                }
                            }
                    }.toMap()

                } else translations
            }
            .onEach { Logger.d("Translations updated") }
    }

    private fun Query<Translation>.toModelFlow() = this
        .asFlow()
        .mapToList()
        .map { translations -> translations.map { it.toModel() } }
        .map { translations -> translations.associateBy { it.key } }


    override suspend fun update() = withContext(dispatchers.io) {

        val preferredLanguage = preferredLanguage

        Logger.d("Updating...")

        val languages = service.getLanguages()

        Logger.d("Languages: $languages, inserting...")

        database.languageQueries.insertLanguages(languages)

        val preferred = languages.find { it.codename == preferredLanguage }
        val fallback = languages.find { it.isFallback }

        preferred?.let {
            Logger.d("Preferred language: ${it.codename}")
            updateLanguage(it)
            database.languageQueries.setActiveLanguage(it.codename)

        } ?: Logger.d("Preferred language not found")

        fallback?.let {
            if (it.codename != preferred?.codename) {
                Logger.d("Fallback language (${it.codename}) differed from preferred language")
                updateLanguage(it)
            }
        } ?: Logger.e("Fallback language not found")
    }

    override suspend fun setLanguage(language: String) = withContext(dispatchers.io) {

        Logger.d("Setting language: $language...")

        with(database) {
            transaction {
                infoQueries.forcedLanguage = language
                languageQueries.setActiveLanguage(language)
            }
        }

        updateLanguage(language)
    }

    override suspend fun createTranslation(value: LanguageCenterValue) = withContext(dispatchers.io) {
        Logger.d("Creating translation: ${value.string()}...")
        service.createTranslation(
            category = value.category,
            key = value.key,
            value = value.fallback,
            comment = value.comment
        )
    }

    private suspend fun updateLanguage(codename: String) {
        val remoteLanguage = service.getLanguage(language = codename)
        updateLanguage(remoteLanguage)
    }

    private suspend fun updateLanguage(remoteLanguage: LanguageDto) {

        val localLanguage = database.languageQueries.getLanguage(language = remoteLanguage.codename)

        if ((localLanguage?.updated ?: 0L) < remoteLanguage.timestamp) {
            Logger.d("Updating language ${remoteLanguage.codename}...")

            val result = service.getTranslations(remoteLanguage.codename)

            Logger.d("${result.size} translations found. Inserting...")

            database.apply {
                transaction {
                    translationQueries.insertTranslations(result)
                    languageQueries.setLanguageUpdated(remoteLanguage.timestamp, remoteLanguage.codename)
                }
            }

            Logger.d("Language (${remoteLanguage.codename}) update complete")

        } else {
            Logger.d("Language (${remoteLanguage.codename}) up to date.")
        }
    }
}
