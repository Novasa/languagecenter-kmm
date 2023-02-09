package com.novasa.languagecenter.data.service.impl

import com.novasa.languagecenter.data.dto.CreateTranslationRequest
import com.novasa.languagecenter.data.dto.LanguageDto
import com.novasa.languagecenter.data.dto.TranslationDto
import com.novasa.languagecenter.data.service.api.LanguageCenterService
import com.novasa.languagecenter.platform.Platform
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

internal class KtorLanguageCenterService(
    private val client: HttpClient,
    private val platform: Platform
) : LanguageCenterService {

    override suspend fun getLanguages(timestamp: Boolean): List<LanguageDto> {
        return client.get("v1/languages") {
            parameter("timestamp", timestamp.toParam())
        }.body()
    }

    override suspend fun getLanguage(language: String, timestamp: Boolean): LanguageDto {
        return client.get("v1/language/$language") {
            parameter("timestamp", timestamp.toParam())
        }.body()
    }

    override suspend fun getTranslations(language: String, indexing: Boolean): List<TranslationDto> {
        return client.get("v1/strings") {
            parameter("language", language)
            parameter("platform", platform.platform)
            parameter("indexing", indexing.toParam())
        }.body()
    }

    override suspend fun createTranslation(category: String, key: String, value: String, comment: String?) {
        client.post("v1/string") {
            parameter("platform", platform.platform)
            setBody(
                CreateTranslationRequest(
                    platform = platform.platform,
                    category = category,
                    key = key,
                    value = value,
                    comment = comment
                )
            )
        }
    }

    private fun Boolean.toParam() = if (this) "on" else "off"
}
