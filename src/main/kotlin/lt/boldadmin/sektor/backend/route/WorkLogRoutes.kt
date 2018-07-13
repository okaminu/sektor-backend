package lt.boldadmin.sektor.backend.route

import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorkLogHandler
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import org.springframework.http.MediaType

class WorkLogRoutes(private val workLogHandler: WorkLogHandler) {

    fun router() = router {
        "/worklog".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                POST("/log-by-location", workLogHandler::logByLocation)
            }
        }
        GET("/status", { ok().body(fromObject("OK")) })
    }
}