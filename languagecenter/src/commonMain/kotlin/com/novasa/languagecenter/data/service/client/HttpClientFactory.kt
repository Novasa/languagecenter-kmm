package com.novasa.languagecenter.data.service.client

import io.ktor.client.*

internal interface HttpClientFactory {

    fun create(): HttpClient
}
