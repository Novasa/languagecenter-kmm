@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.8.0"

    id("com.android.library")
    id("co.touchlab.kermit")
    id("com.squareup.sqldelight")
}

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependencies.KotlinX.Coroutines.core)
                api(Dependencies.KotlinX.datetime)

                api(Dependencies.Koin.core)
                api(Dependencies.Koin.test)

                implementation(Dependencies.Ktor.core)
                implementation(Dependencies.Ktor.auth)
                implementation(Dependencies.Ktor.contentNegotiation)
                implementation(Dependencies.Ktor.kotlinxSerializationJson)
                implementation(Dependencies.Ktor.logging)

                implementation(Dependencies.SqlDelight.runtime)
                implementation(Dependencies.SqlDelight.coroutines)


                api(Dependencies.Kermit.kermit)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Dependencies.Jetpack.startup)
                implementation(Dependencies.Ktor.clientAndroid)
                implementation(Dependencies.SqlDelight.driverAndroid)

                implementation(Dependencies.Compose.foundation)
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)

            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation(Dependencies.Ktor.clientNative)
                implementation(Dependencies.SqlDelight.driverNative)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "com.novasa.languagecenter"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }

    buildFeatures {
        compose = true
    }
}

sqldelight {
    database("LanguageCenterDatabase") {
        packageName = "com.novasa.languagecenter"
    }
}