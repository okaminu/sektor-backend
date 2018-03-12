package lt.tlistas.loginn.backend.route

import lt.tlistas.loginn.backend.handler.WorkLogHandler
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

class WorkLogRoutes(private val workLogHandler: WorkLogHandler) {

    fun router() = router {
        "/worklog".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                POST("/log-by-location", workLogHandler::logByLocation)
            }
        }
    }
}