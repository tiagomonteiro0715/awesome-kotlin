package link.kotlin.scripts

import io.heapy.komodo.di.createContextAndGet
import io.heapy.komodo.di.type

suspend fun main() {
    val fetcher = createContextAndGet(type<KotlinVersionFetcher>(), generatorModule)
    val versions = fetcher.getLatestVersions(listOf("1.3", "1.4"))
    println(versions)
}
