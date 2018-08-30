package lt.boldadmin.sektor.backend.route

import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorkLogHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import org.springframework.http.MediaType.APPLICATION_JSON

@Configuration
open class WorkLogRoutes(private val workLogHandler: WorkLogHandler) {

    @Bean
    open fun router() = router {
        "/worklog".nest {
            accept(APPLICATION_JSON).nest {
                POST("/log-by-location", workLogHandler::logByLocation)
                POST("/update-description/{intervalId}", workLogHandler::updateDescription)
                GET("/project-name-of-started-work", workLogHandler::getProjectNameOfStartedWork)
                GET("/has-work-started", workLogHandler::hasWorkStarted)
            }
        }
        GET("/status", { ok().body(fromObject("OK")) })

    }
}