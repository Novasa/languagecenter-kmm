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
            .onEach { Logger.d("[Language Center] Active language updated: $it") }
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
            .onEach { Logger.d("[Language Center] Translations updated") }
    }

    private fun Query<Translation>.toModelFlow() = this
        .asFlow()
        .mapToList()
        .map { translations -> translations.map { it.toModel() } }
        .map { translations -> translations.associateBy { it.key } }


    override suspend fun update() = withContext(dispatchers.io) {

        val preferredLanguage = preferredLanguage

        Logger.d("[Language Center] Preferred language: $preferredLanguage, updating...")

        val languages = service.getLanguages()

        Logger.d("[Language Center] Languages: $languages, inserting...")

        database.languageQueries.insertLanguages(languages)

        val preferred = languages.find { it.codename == preferredLanguage }
        val fallback = languages.find { it.isFallback }

        preferred?.let {
            Logger.d("[Language Center] Preferred language found (${it.codename}).")
            updateLanguage(it)
            database.languageQueries.setActiveLanguage(it.codename)

        } ?: Logger.d("[Language Center] Preferred language not found (${preferredLanguage}).")

        fallback?.let {
            if (it.codename != preferred?.codename) {
                Logger.d("[Language Center] Fallback language (${it.codename}) differed from preferred language (${preferredLanguage})")
                updateLanguage(it)
            }
        } ?: Logger.e("Fallback language not found")
    }

    override suspend fun setLanguage(language: String) = withContext(dispatchers.io) {

        Logger.d("[Language Center] Setting language: $language...")

        with(database) {
            transaction {
                infoQueries.forcedLanguage = language
                languageQueries.setActiveLanguage(language)
            }
        }

        updateLanguage(language)
    }

    override suspend fun createTranslation(value: LanguageCenterValue) = withContext(dispatchers.io) {
        Logger.d("[Language Center] Creating translation: ${value.string()}...")
        service.createTranslation(
            category = value.category,
            key = value.id,
            value = value.fallback,
            comment = value.comment
        )
    }

    private suspend fun updateLanguage(codename: String) {
        val remoteLanguage = service.getLanguage(language = codename)
        updateLanguage(remoteLanguage)
    }

    private suspend fun updateLanguage(remoteLanguage: LanguageDto) {

        val codename = remoteLanguage.codename
        val localTimestamp = database.languageQueries.getLanguage(language = remoteLanguage.codename)?.updated ?: 0L
        val remoteTimestamp = remoteLanguage.timestamp

        Logger.d {
            "[Language Center] Timestamp check for language ($codename) Local: $localTimestamp, Remote: $remoteTimestamp, Diff: ${remoteTimestamp - localTimestamp}"
        }

        if (localTimestamp < remoteTimestamp) {
            Logger.d("[Language Center] Language ($codename) out of date. Updating ...")

            val result = service.getTranslations(codename)

            Logger.d("[Language Center] ${result.size} translations found for language ($codename). Inserting...")

            database.apply {
                transaction {
                    translationQueries.insertTranslations(result)
                    languageQueries.setLanguageUpdated(remoteLanguage.timestamp, codename)
                }
            }

            Logger.d("[Language Center] Language ($codename) update complete")

        } else {
            Logger.d("[Language Center] Language ($codename) up to date.")
        }
    }
}