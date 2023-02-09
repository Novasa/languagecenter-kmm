package com.novasa.languagecenter.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CreateTranslationRequest(
    @SerialName("platform") val platform: String,
    @SerialName("category") val category: String,
    @SerialName("key") val key: String,
    @SerialName("value") val value: String,
    @SerialName("comment") val comment: String?
)
