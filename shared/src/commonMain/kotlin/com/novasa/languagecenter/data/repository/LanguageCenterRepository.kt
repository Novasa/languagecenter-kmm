package com.novasa.languagecenter.data.repository

import com.novasa.languagecenter.domain.model.LanguageCenterLanguage
import com.novasa.languagecenter.domain.model.LanguageCenterTranslation
import com.novasa.languagecenter.domain.model.LanguageCenterValue
import kotlinx.coroutines.flow.Flow

interface LanguageCenterRepository {

    val activeLanguage: Flow<LanguageCenterLanguage>

    val translations: Flow<Map<String, LanguageCenterTranslation>>

    val fallbackTranslations: Flow<Map<String, LanguageCenterTranslation>>

    suspend fun update()

    suspend fun setLanguage(language: String)

    suspend fun createTranslation(value: LanguageCenterValue)
}
