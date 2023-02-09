@Suppress("MemberVisibilityCanBePrivate")
object Dependencies {

    object Jetpack {
        object Versions {
            const val startup = "1.1.1"
        }

        const val startup = "androidx.startup:startup-runtime:${Versions.startup}"
    }

    /** Kotlin features */
    object KotlinX {
        object Versions {
            const val coroutines = "1.6.4"
            const val datetime = "0.4.0"
        }

        object Coroutines {
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        }

        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.datetime}"
    }

    /** Dependency Injection */
    object Koin {
        object Version {
            const val core = "3.2.0"
            const val android = "3.3.0"
        }

        const val core = "io.insert-koin:koin-core:${Version.core}"
        const val test = "io.insert-koin:koin-test:${Version.core}"
        const val android = "io.insert-koin:koin-android:${Version.android}"
        const val compose = "io.insert-koin:koin-androidx-compose:${Version.android}"
    }

    /** HTTP client */
    object Ktor {
        object Version {
            const val core = "2.1.3"
        }

        const val core = "io.ktor:ktor-client-core:${Version.core}"
        const val auth = "io.ktor:ktor-client-auth:${Version.core}"
        const val contentNegotiation = "io.ktor:ktor-client-content-negotiation:${Version.core}"
        const val kotlinxSerializationJson = "io.ktor:ktor-serialization-kotlinx-json:${Version.core}"
        const val clientAndroid = "io.ktor:ktor-client-android:${Version.core}"
        const val clientNative = "io.ktor:ktor-client-darwin:${Version.core}"
        const val logging = "io.ktor:ktor-client-logging:${Version.core}"
    }

    /** SQLite Database */
    object SqlDelight {
        object Versions {
            const val core = "1.5.5"
        }

        const val runtime = "com.squareup.sqldelight:runtime:${Versions.core}"
        const val coroutines = "com.squareup.sqldelight:coroutines-extensions:${Versions.core}"
        const val driverAndroid = "com.squareup.sqldelight:android-driver:${Versions.core}"
        const val driverNative = "com.squareup.sqldelight:native-driver:${Versions.core}"
    }

    /** Kermit the Log */
    object Kermit {
        const val version = "1.1.3"
        const val kermit = "co.touchlab:kermit:$version"
    }

    /** Jetpack Compose */
    object Compose {
        object Versions {
            const val ui = "1.3.1"
            const val foundation = "1.3.0"
            const val material3 = "1.0.1"
            const val activity = "1.6.1"
        }

        const val ui = "androidx.compose.ui:ui:${Versions.ui}"
        const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.ui}"
        const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview:${Versions.ui}"
        const val foundation = "androidx.compose.foundation:foundation:${Versions.foundation}"
        const val material = "androidx.compose.material3:material3:${Versions.material3}"
        const val activity = "androidx.activity:activity-compose:${Versions.activity}"
    }
}
