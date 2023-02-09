package com.novasa.languagecenter.data.service.impl

import com.novasa.languagecenter.data.service.client.HttpClientFactory
import com.novasa.languagecenter.data.service.client.ServiceConfig
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.errors.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger.Companion as KLogger

class KtorClientFactory(
    private val config: ServiceConfig,
    private val ktorLogger: Logger

) : HttpClientFactory {

    @OptIn(ExperimentalSerializationApi::class)
    override fun create(): HttpClient = HttpClient {
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = config.host

                path(config.baseUrl)
                header("Content-Type", "application/json")
            }
        }

        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(
                        username = config.username,
                        password = config.password
                    )
                }
            }
        }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }

        install(HttpRequestRetry) {
            maxRetries = 5
            retryOnExceptionIf { _, cause: Throwable ->
                (cause is IOException).also { retry ->
                    if (retry) {
                        KLogger.e("Encountered IOException, retrying...", cause)
                    }
                }
            }
            retryIf { _, response ->
                if (!response.status.isSuccess()) {
                    KLogger.e("Response: $response")
                }
                false
            }

            delayMillis { attempt -> attempt * 1000L }
        }

        // Ktor will throw an exception for non 2xx responses
        expectSuccess = true

        install(Logging) {
            logger = ktorLogger
            level = LogLevel.ALL
        }
    }
}