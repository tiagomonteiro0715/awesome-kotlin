package link.kotlin.scripts

import io.heapy.komodo.di.module
import io.heapy.komodo.di.provide
import link.kotlin.scripts.dsl.Article
import link.kotlin.scripts.dsl.Category
import link.kotlin.scripts.model.ApplicationConfiguration
import link.kotlin.scripts.scripting.scriptModule
import link.kotlin.scripts.utils.HttpClient
import link.kotlin.scripts.utils.ObjectMapper
import link.kotlin.scripts.utils.callLogger
import link.kotlin.scripts.utils.Cache
import link.kotlin.scripts.utils.writeFile

interface AwesomeKotlinGenerator {
    fun getLinks(): List<Category>
    fun getArticles(): List<Article>
    fun generateReadme(links: List<Category>)
    fun generateSiteResources(links: List<Category>, articles: List<Article>)
}

private class DefaultAwesomeKotlinGenerator(
    private val linksSource: LinksSource,
    private val articlesSource: ArticlesSource,
    private val readmeGenerator: ReadmeGenerator,
    private val siteGenerator: SiteGenerator
) : AwesomeKotlinGenerator {
    override fun getLinks(): List<Category> {
        return linksSource.getLinks()
    }

    override fun getArticles(): List<Article> {
        return articlesSource.getArticles()
    }

    override fun generateReadme(links: List<Category>) {
        writeFile("./readme/README.md", readmeGenerator.generate(links))
    }

    override fun generateSiteResources(links: List<Category>, articles: List<Article>) {
        siteGenerator.createDistFolders()
        siteGenerator.copyResources()
        siteGenerator.generateLinksJson(links)
        siteGenerator.generateKotlinVersionsJson()
        siteGenerator.generateFeeds(articles)
        siteGenerator.generateSitemap(articles)
        siteGenerator.generateArticles(articles)
    }
}

val generatorModule by module {
    dependency(scriptModule)

    provide<RssGenerator>(::DefaultRssGenerator)
    provide<PagesGenerator>(::DefaultPagesGenerator)
    provide<LinksSource>(::FileSystemLinksSource)
    provide<KotlinVersionFetcher>(::MavenCentralKotlinVersionFetcher)
    provide<SitemapGenerator>(::DefaultSitemapGenerator)
    provide<LinksChecker>(::DefaultLinksChecker)
    provide<CategoryProcessor>(::ParallelCategoryProcessor)
    provide<ArticlesSource>(::FileSystemArticlesSource)
    provide<ArticlesProcessor>(::DefaultArticlesProcessor)
    provide<ReadmeGenerator>(::MarkdownReadmeGenerator)

    provide(::ObjectMapper)
    provide(::HttpClient)
    provide(::MarkdownRenderer)
    provide(::ApplicationConfiguration)
    provide(::GithubTrending)
    provide(::Cache)
    provide(::LinksProcessor)
    provide(::AwesomeKotlinGenerator)
    provide(::SiteGenerator)
}

// This will be eliminated after introduction of proxy-wrapping in komodo-di
fun AwesomeKotlinGenerator(
    linksSource: LinksSource,
    articlesSource: ArticlesSource,
    readmeGenerator: ReadmeGenerator,
    siteGenerator: SiteGenerator
): AwesomeKotlinGenerator {
    val implementation = DefaultAwesomeKotlinGenerator(
        linksSource = linksSource,
        articlesSource = articlesSource,
        readmeGenerator = readmeGenerator,
        siteGenerator = siteGenerator
    )

    return callLogger<AwesomeKotlinGenerator>(implementation)
}
