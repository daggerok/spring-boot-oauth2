package daggerok

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus.*
import org.springframework.http.MediaType
import org.springframework.http.MediaType.*
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFunction

/*
class GlobalErrorAttributes : DefaultErrorAttributes() {
  override fun getErrorAttributes(request: ServerRequest, includeStackTrace: Boolean) =
    super.getErrorAttributes(request, includeStackTrace).toMutableMap().plus(
        mapOf(
            "status" to BAD_REQUEST,
            "message" to "ololo-trololo"
        )
    )
}

@Component
@Order(-2)
class GlobalErrorWebExceptionHandler(val attributes: ErrorAttributes,
                                     val props: ResourceProperties,
                                     val ctx: ApplicationContext)
  : AbstractErrorWebExceptionHandler(attributes, props, ctx) {

  // constructors

  override fun getRoutingFunction(errorAttributes: ErrorAttributes): RouterFunction<ServerResponse> {
    return RouterFunctions.route(
        RequestPredicates.all(), HandlerFunction<ServerResponse> { this.renderErrorResponse(it) })
  }

  private fun renderErrorResponse(request: ServerRequest): Mono<ServerResponse> {
    val errorPropertiesMap = getErrorAttributes(request, false)

    return ServerResponse.status(BAD_REQUEST)
        .contentType(APPLICATION_JSON_UTF8)
        .body(BodyInserters.fromObject(errorPropertiesMap))
  }
}
*/

//@PropertySource("classpath:messages.properties")
@EnableConfigurationProperties(ThymeleafProperties::class)
@SpringBootApplication
class App {

  val index: Mono<ServerResponse> by lazy {
    ok().contentType(TEXT_HTML)
        .render("index", mapOf("message" to "hey"))
  }

  @Bean fun routes() = router {
    resources("/**", ClassPathResource("/static"))
    ("/").nest {
      arrayOf("/", "/ololo", "/trololo").forEach {
        GET(it) { index }
      }
      contentType(APPLICATION_JSON_UTF8)
      GET("/api/**") {
        ok().body(
            mapOf("hello" to "world").toMono()
        )
      }
    }
  }.filter { request, next ->
    try { next.handle(request) } catch (ex: Exception) {
      println("error: $ex")
      when (ex) {
        is IllegalStateException -> status(SERVICE_UNAVAILABLE).build()
        else -> notFound().build()
      }
    }
  }
}

fun main(args: Array<String>) {
  runApplication<App>(*args)
}
