package com.novasa.languagecenter.data.service.api

import com.novasa.languagecenter.data.dto.LanguageDto
import com.novasa.languagecenter.data.dto.TranslationDto

internal interface LanguageCenterService {

    suspend fun getLanguages(timestamp: Boolean = true): List<LanguageDto>

    suspend fun getLanguage(language: String, timestamp: Boolean = true): LanguageDto

    suspend fun getTranslations(language: String, indexing: Boolean = false): List<TranslationDto>

    suspend fun createTranslation(category: String, key: String, value: String, comment: String?)
}
