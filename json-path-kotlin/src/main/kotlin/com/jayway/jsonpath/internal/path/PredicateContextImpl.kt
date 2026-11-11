package com.jayway.jsonpath.internal.path
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.Predicate
import com.jayway.jsonpath.internal.Path
import com.jayway.jsonpath.spi.mapper.MappingException
import org.slf4j.LoggerFactory
import java.util.HashMap

open class PredicateContextImpl(
    private val contextDocument: Any?,
    private val rootDocument: Any?,
    private val configuration: Configuration,
    private val documentPathCache: HashMap<Path, Any?>,
) : Predicate.PredicateContext {
    open fun evaluate(path: Path): Any? {
        val result: Any?
        if (path.isRootPath()) {
            if (documentPathCache.containsKey(path)) {
                logger.debug("Using cached result for root path: " + path.toString())
                result =
                    documentPathCache[path]
            } else {
                result = path.evaluate(rootDocument, rootDocument, configuration).getValue()
                documentPathCache[path] = result
            }
        } else {
            result = path.evaluate(contextDocument, rootDocument, configuration).getValue()
        }
        return result
    }

    open fun documentPathCache(): HashMap<Path, Any?> = documentPathCache

    override fun item(): Any? = contextDocument

    @Throws(MappingException::class)
    override fun <T> item(clazz: Class<T>): T? = configuration().mappingProvider().map(contextDocument, clazz, configuration)

    override fun root(): Any? = rootDocument

    override fun configuration(): Configuration = configuration

    companion object {
        private val logger = LoggerFactory.getLogger(PredicateContextImpl::class.java)
    }
}
