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
import com.novasa.languagecenter.logging.LC_TAG
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
            .onEach { Logger.d("$LC_TAG Active language updated: $it") }
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
            .onEach { Logger.d("$LC_TAG Translations updated") }
    }

    private fun Query<Translation>.toModelFlow() = this
        .asFlow()
        .mapToList()
        .map { translations -> translations.map { it.toModel() } }
        .map { translations -> translations.associateBy { it.key } }


    override suspend fun update() = withContext(dispatchers.io) {

        val preferredLanguage = preferredLanguage

        Logger.d("$LC_TAG Preferred language: $preferredLanguage, updating...")

        val languages = service.getLanguages()

        Logger.d("$LC_TAG Available languages: $languages, inserting...")

        database.languageQueries.insertLanguages(languages)

        val preferred = languages.find { it.codename == preferredLanguage }
        val fallback = languages.find { it.isFallback }

        preferred?.let {
            Logger.d("$LC_TAG Preferred language found (${it.codename}).")
            database.languageQueries.setActiveLanguage(it.codename)
            updateLanguage(it)

        } ?: Logger.d("$LC_TAG Preferred language not found (${preferredLanguage}).")

        fallback?.let {
            if (it.codename != preferred?.codename) {
                Logger.d("$LC_TAG Fallback language (${it.codename}) differed from preferred language (${preferredLanguage})")
                if (preferred == null) {
                    database.languageQueries.setActiveLanguage(it.codename)
                }
                updateLanguage(it)
            }
        } ?: Logger.e("$LC_TAG Fallback language not found")
    }

    override suspend fun setLanguage(language: String) = withContext(dispatchers.io) {

        Logger.d("$LC_TAG Setting language: $language...")

        with(database) {
            transaction {
                infoQueries.forcedLanguage = language
                languageQueries.setActiveLanguage(language)
            }
        }

        update()
    }

    override suspend fun createTranslation(value: LanguageCenterValue) = withContext(dispatchers.io) {
        Logger.d("$LC_TAG Creating translation: ${value.string()}...")
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
            "$LC_TAG Timestamp check for language ($codename) Local: $localTimestamp, Remote: $remoteTimestamp, Diff: ${remoteTimestamp - localTimestamp}"
        }

        if (localTimestamp < remoteTimestamp) {
            Logger.d("$LC_TAG Language ($codename) out of date. Updating ...")

            val result = service.getTranslations(codename)

            Logger.d("$LC_TAG ${result.size} translations found for language ($codename). Inserting...")

            database.apply {
                transaction {
                    translationQueries.insertTranslations(result)
                    languageQueries.setLanguageUpdated(remoteLanguage.timestamp, codename)
                }
            }

            Logger.d("$LC_TAG Language ($codename) update complete")

        } else {
            Logger.d("$LC_TAG Language ($codename) up to date.")
        }
    }
}