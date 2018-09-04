package daggerok

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.web.reactive.function.server.RenderingResponse
import org.springframework.web.reactive.function.server.RenderingResponse.create
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@SpringBootApplication
class App {

  val index: Mono<RenderingResponse> by lazy {
    create("index")
        .modelAttribute("message", "hey")
        .build()
  }

  @Bean
  fun routes() = router {
    resources("/**", ClassPathResource("/static"))
    "/".nest {
      arrayOf("/", "/ololo", "/trololo").forEach {
        GET(it) { index as Mono<ServerResponse> }
      }
      contentType(APPLICATION_JSON_UTF8)
      GET("/api/**") {
        ok().body(
            mapOf("hello" to "world").toMono()
        )
      }
    }
  }.filter { request, next -> // TODO: add filter...
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
