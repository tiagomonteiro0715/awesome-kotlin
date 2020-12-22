package link.kotlin.scripts.scripting

import io.heapy.komodo.di.module
import io.heapy.komodo.di.provide
import link.kotlin.scripts.utils.Cache
import link.kotlin.scripts.utils.cacheKey
import kotlin.reflect.KClass
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.valueOrThrow
import kotlin.script.experimental.host.StringScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

interface ScriptEvaluator {
    fun <T : Any> eval(source: String, name: String, type: KClass<T>): T
}

internal class ScriptingScriptEvaluator(
    private val scriptingHost: BasicJvmScriptingHost
) : ScriptEvaluator {
    override fun <T : Any> eval(source: String, name: String, type: KClass<T>): T {
        val eval = scriptingHost.evalWithTemplate<AwesomeScript>(StringScriptSource(source, name))
        @Suppress("UNCHECKED_CAST")
        return (eval.valueOrThrow().returnValue as ResultValue.Value).value as T
    }
}

private class CachingScriptEvaluator(
    private val cache: Cache,
    private val scriptEvaluator: ScriptEvaluator
) : ScriptEvaluator {
    override fun <T : Any> eval(source: String, name: String, type: KClass<T>): T {
        val cacheKey = Cache.cacheKey(source)
        val cacheValue = cache.get(cacheKey, type)

        return if (cacheValue == null) {
            val result = scriptEvaluator.eval(source, name, type)
            cache.put(cacheKey, result)
            result
        } else {
            cacheValue
        }
    }
}

val scriptModule by module {
    provide({ BasicJvmScriptingHost() })
    provide(::cachingScriptEvaluator)
}

// This will be eliminated after introduction of wrapping in komodo-di
internal fun cachingScriptEvaluator(
    scriptingHost: BasicJvmScriptingHost,
    cache: Cache
): ScriptEvaluator {
    val scriptingScriptEvaluator = ScriptingScriptEvaluator(
        scriptingHost = scriptingHost
    )

    return CachingScriptEvaluator(
        cache = cache,
        scriptEvaluator = scriptingScriptEvaluator
    )
}
