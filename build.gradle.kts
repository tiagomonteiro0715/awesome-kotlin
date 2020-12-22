import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm").version(kotlinVersion)
}

application {
    mainClass.set("link.kotlin.scripts.Application")
}

repositories {
    jcenter()
    maven { url = uri("https://dl.bintray.com/heapy/heap") }
    maven { url = uri("https://dl.bintray.com/heapy/heap-dev") }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        languageVersion = "1.4"
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(stdlib)
    implementation(reflect)
    implementation(coroutines)

    implementation(jacksonXml)
    implementation(jacksonKotlin)
    implementation(jacksonJsr310)

    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.sentry:sentry-logback:1.7.30")

    implementation("com.rometools:rome:1.12.2")
    implementation("com.github.dfabulich:sitemapgen4j:1.1.2")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("by.heap.remark:remark-kotlin:1.2.0")

    implementation("io.heapy.komodo:komodo:0.1.0-development+000083")

    implementation(kotlin("scripting-common"))
    implementation(kotlin("scripting-jvm"))
    implementation(kotlin("scripting-jvm-host"))

    implementation(commonmark)
    implementation(commonmarkExtGfmTables)

    implementation(ktorClientApache)
    implementation(ktorClientJackson)

    testImplementation(mockk)
    testImplementation(junitApi)
    testRuntimeOnly(junitEngine)
}
