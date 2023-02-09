package com.novasa.languagecenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LanguageDto(
    @SerialName("name") val name: String,
    @SerialName("codename") val codename: String,
    @SerialName("is_fallback") val isFallback: Boolean,
    @SerialName("timestamp") val timestamp: Long
)
