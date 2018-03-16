package lt.tlistas.loginn.backend.route

import lt.tlistas.loginn.backend.handler.identityconfirmed.WorkLogHandler
import org.aspectj.apache.bcel.classfile.AttributeUtils.accept
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

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