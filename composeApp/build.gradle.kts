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
            val voyagerVersion = "1.0.0-rc10"
            implementation(compose.desktop.currentOs)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation("com.russhwolf:multiplatform-settings:1.1.1")
            implementation("io.ktor:ktor-client-auth:2.3.6")
            implementation("io.insert-koin:koin-core:3.5.2-RC1")
            implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
            implementation("io.ktor:ktor-server-core:2.3.6")
            implementation("io.ktor:ktor-client-core:2.3.6")
            implementation("io.ktor:ktor-client-cio:2.3.6")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
            implementation("org.slf4j:slf4j-api:2.0.9")
            implementation("org.slf4j:slf4j-nop:2.0.9")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.6")
            implementation("io.ktor:ktor-client-serialization:2.3.6")
            implementation("io.ktor:ktor-client-json:2.3.6")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")
            implementation(compose.materialIconsExtended)
            implementation(compose.material3)
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
            implementation("at.quickme.kotlinmailer:kotlinmailer-core:0.2.0")
            implementation("io.github.kevinnzou:compose-progressIndicator-multiplatform:1.3.0")
            implementation("io.ktor:ktor-server-netty:2.3.6")
            implementation("io.ktor:ktor-client-logging-jvm:2.3.6")
            implementation("com.sletmoe.bucket4k:bucket4k:1.0.0")
            implementation("org.apache.logging.log4j:log4j-api:2.22.1")
            implementation("org.apache.logging.log4j:log4j-core:2.22.1")


        }
        val desktopTest by getting {
            dependencies {
                implementation(compose.desktop.uiTestJUnit4)
                implementation(compose.desktop.currentOs)
            }
        }

        commonMain.dependencies {


        }
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"


        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "EbayLexoffice Connector"
            packageVersion = "1.0.0"
        }
        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}


