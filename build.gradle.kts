plugins {
    id("com.android.library") version "7.4.0" apply false
    id("com.android.application") version "7.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false

    kotlin("multiplatform") version "1.8.0" apply false
    id("com.squareup.sqldelight") version Dependencies.SqlDelight.Versions.core apply false
    id("co.touchlab.kermit") version Dependencies.Kermit.version apply false
}
