plugins {
    id("com.android.library") version "7.4.0" apply false
    id("com.android.application") version "7.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false

    kotlin("multiplatform") version "1.8.0" apply false
    id("com.squareup.sqldelight") version Dependencies.SqlDelight.Versions.core apply false
    id("co.touchlab.kermit") version Dependencies.Kermit.version apply false

    kotlin("jvm") version "1.6.0"
    java
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.novasa.languagecenter"
            artifactId = "kmm"
            version = "0.1"

            from(components["java"])
        }
    }
}
