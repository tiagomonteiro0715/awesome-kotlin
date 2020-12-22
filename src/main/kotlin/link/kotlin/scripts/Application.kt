@file:JvmName("Application")

package link.kotlin.scripts

import io.heapy.komodo.UnitEntryPoint
import io.heapy.komodo.di.provide
import io.heapy.komodo.komodo
import link.kotlin.scripts.utils.logger
import kotlin.system.exitProcess

suspend fun main() {
    try {
        komodo<AwesomeKotlinEntryPoint> {
            provide(::AwesomeKotlinEntryPoint)
            dependency(generatorModule)
        }

        LOGGER.info("Done, exit.")
        exitProcess(0)
    } catch (e: Exception) {
        LOGGER.error("Failed, exit.", e)
        exitProcess(1)
    }
}

class AwesomeKotlinEntryPoint(
    private val generator: AwesomeKotlinGenerator
) : UnitEntryPoint {
    override suspend fun run() {
        // Load data
        val articles = generator.getArticles()
        val links = generator.getLinks()

        // Create README.md
        generator.generateReadme(links)

        // Generate resources for site
        generator.generateSiteResources(links, articles)
    }
}

private val LOGGER = logger {}
