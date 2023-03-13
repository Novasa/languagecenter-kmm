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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

internal class LanguageCenterRepositoryImpl(
    private val service: LanguageCenterService,
    private val database: LanguageCenterDatabase,
    private val dispatchers: DispatchersFacade,
    private val systemLanguageProvider: SystemLanguageProvider,
    private val logger: Logger
) : LanguageCenterRepository {

    private val preferredLanguage: String
        get() = database.infoQueries.forcedLanguage
            ?: systemLanguageProvider.systemLanguage

    override val activeLanguage: Flow<LanguageCenterLanguage> by lazy {
        database.languageQueries.getActiveLanguage()
            .asFlow()
            .mapToOneOrNull()
            .filterNotNull()
            .onEach { logger.d("Active language updated: $it") }
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
            .onEach { logger.d("Translations updated") }
    }

    private fun Query<Translation>.toModelFlow() = this
        .asFlow()
        .mapToList()
        .map { translations -> translations.map { it.toModel() } }
        .map { translations -> translations.associateBy { it.key } }


    override suspend fun update() = withContext(dispatchers.io) {

        val preferredLanguage = preferredLanguage

        logger.d("Preferred language: $preferredLanguage, updating...")

        val languages = service.getLanguages()

        logger.d("Available languages: $languages, inserting...")

        database.languageQueries.insertLanguages(languages)

        val preferred = languages.find { it.codename == preferredLanguage }
        val fallback = languages.find { it.isFallback }

        preferred?.let {
            logger.d("Preferred language found (${it.codename}).")
            database.languageQueries.setActiveLanguage(it.codename)
            updateLanguage(it)

        } ?: logger.d("Preferred language not found (${preferredLanguage}).")

        fallback?.let {
            if (it.codename != preferred?.codename) {
                logger.d("Fallback language (${it.codename}) differed from preferred language (${preferredLanguage})")
                if (preferred == null) {
                    database.languageQueries.setActiveLanguage(it.codename)
                }
                updateLanguage(it)
            }
        } ?: logger.e("Fallback language not found")
    }

    override suspend fun setLanguage(language: String) = withContext(dispatchers.io) {

        logger.d("Setting language: $language...")

        with(database) {
            transaction {
                infoQueries.forcedLanguage = language
                languageQueries.setActiveLanguage(language)
            }
        }

        update()
    }

    override suspend fun createTranslation(value: LanguageCenterValue) = withContext(dispatchers.io) {
        logger.d("Creating translation: ${value.string()}...")
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

        logger.d {
            "Timestamp check for language ($codename) Local: $localTimestamp, Remote: $remoteTimestamp, Diff: ${remoteTimestamp - localTimestamp}"
        }

        if (localTimestamp < remoteTimestamp) {
            logger.d("Language ($codename) out of date. Updating ...")

            val result = service.getTranslations(codename)

            logger.d("${result.size} translations found for language ($codename). Inserting...")

            database.apply {
                transaction {
                    translationQueries.insertTranslations(result)
                    languageQueries.setLanguageUpdated(remoteLanguage.timestamp, codename)
                }
            }

            logger.d("Language ($codename) update complete")

        } else {
            logger.d("Language ($codename) up to date.")
        }
    }
}