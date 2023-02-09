package com.novasa.languagecenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TranslationDto(
    @SerialName("key") val key: String,
    @SerialName("value") val value: String,
    @SerialName("language") val language: String,
    @SerialName("html_tags") val htmlTags: List<String> = emptyList()
)
