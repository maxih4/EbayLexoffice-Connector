import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.ExperimentalComposeLibrary

repositories {
    mavenCentral()
    maven("https://mobile.maven.couchbase.com/maven2/dev/")
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    kotlin("plugin.serialization") version "1.9.21"
    alias(libs.plugins.jetbrainsCompose)
    }



kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation("io.ktor:ktor-server-core:2.3.6")
            implementation("io.ktor:ktor-server-netty:2.3.6")
            implementation("io.ktor:ktor-client-core:2.3.6")
            implementation("io.ktor:ktor-client-cio:2.3.6")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
            implementation("org.slf4j:slf4j-api:2.0.9")
            implementation("org.slf4j:slf4j-nop:2.0.9")
            implementation("io.ktor:ktor-client-logging-jvm:2.3.6")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.6")
            implementation("io.ktor:ktor-client-serialization:2.3.6")
            implementation("io.ktor:ktor-client-json:2.3.6")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")
            implementation("com.sletmoe.bucket4k:bucket4k:1.0.0")
            implementation(compose.materialIconsExtended)




        }
        commonMain.dependencies {
            val voyagerVersion = "1.0.0-rc10"

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation("org.slf4j:slf4j-api:2.0.9")
            implementation("com.russhwolf:multiplatform-settings:1.1.1")
            implementation("io.ktor:ktor-client-auth:2.3.6")
            implementation("io.insert-koin:koin-core:3.5.2-RC1")
            implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")



        }
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"


        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}


