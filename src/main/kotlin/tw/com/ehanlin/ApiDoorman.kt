package tw.com.ehanlin

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientRequest
import io.vertx.core.http.RequestOptions
import io.vertx.ext.web.AllowForwardHeaders
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import tw.com.ehanlin.rule.RuleProvider
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.inject.Inject

@ApplicationScoped
class ApiDoorman {

    @Inject
    lateinit var ruleProvider: RuleProvider

    @Inject
    lateinit var vertx: Vertx

    private val ruleSourceRegexMap = mutableMapOf<String, Regex>()

    private lateinit var proxyClient: HttpClient

    @PostConstruct
    fun init(@Observes router: Router) {
        proxyClient = vertx.createHttpClient()
        router.allowForward(AllowForwardHeaders.ALL)
        router.route().handler(BodyHandler.create())
        ruleProvider.rules().forEach { rule ->
            val pathInfo = parseUrl(rule.path)
            val originInfo = parseUrl(rule.origin)
            if (pathInfo != null && originInfo != null) {
                router.routeWithRegex(pathInfo.path).handler(routingHandler(pathInfo, originInfo))
            }
        }
    }

    private fun routingHandler(pathInfo: UrlInfo, originInfo: UrlInfo): Handler<RoutingContext> = Handler { context ->
        val proxyRequestOptions = buildProxyRequestOptions(context, originInfo)
        proxyClient.request(proxyRequestOptions)
            .onSuccess { handleProxyRequest(context, it) }
            .onFailure { ex ->
                context.response().apply {
                    statusCode = 502
                    end("api-doorman: ${ex.message}")
                }
            }
    }

    private fun buildProxyRequestOptions(context: RoutingContext, originInfo: UrlInfo): RequestOptions {
        var originUri = originInfo.path
        context.pathParams().forEach { params ->
            val sourceRegex =
                ruleSourceRegexMap.getOrPut(params.key) { Regex("\\\$\\{${params.key}\\}") }
            originUri = originUri.replace(sourceRegex, params.value)
        }
        return RequestOptions().apply {
            method = context.request().method()
            host = originInfo.host
            port = originInfo.port
            uri = originUri
            headers = context.request().headers()
            followRedirects = true
        }
    }

    private fun handleProxyRequest(context: RoutingContext, proxyRequest: HttpClientRequest) {
        proxyRequest.response()
            .onSuccess { proxyResponse ->
                context.response().apply {
                    statusCode = proxyResponse.statusCode()
                    headers().setAll(proxyResponse.headers())
                    proxyResponse.bodyHandler { body ->
                        end(body)
                    }
                }
            }
            .onFailure { ex ->
                context.response().apply {
                    statusCode = 502
                    end("api-doorman: ${ex.message}")
                }
            }

        if (context.body != null) {
            proxyRequest.write(context.body)
        }
        proxyRequest.end()
    }

    private val urlRegex = Regex("^(.*?)(\\:\\d*?)?(/.*)\$")
    private val spaceRegex = Regex("\\s")
    private fun parseUrl(url: String): UrlInfo? = urlRegex.find(url)?.run {
        UrlInfo(
            groupValues[1].replace(spaceRegex, ""),
            if (groupValues[2].isNullOrBlank()) 80 else groupValues[2].substring(1).toInt(),
            groupValues[3].replace(spaceRegex, "")
        )
    }

    private data class UrlInfo(
        val host: String,
        val port: Int,
        val path: String
    )

}