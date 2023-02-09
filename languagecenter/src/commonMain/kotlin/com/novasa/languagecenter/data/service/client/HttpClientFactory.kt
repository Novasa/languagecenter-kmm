package com.novasa.languagecenter.data.service.client

import io.ktor.client.*

interface HttpClientFactory {

    fun create(): HttpClient
}
