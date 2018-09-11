package lt.boldadmin.sektor.backend.route

import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorkLogHandler
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.RouterFunctionDsl

internal fun workLogRoutes(workLogHandler: WorkLogHandler): RouterFunctionDsl.() -> Unit = {
    accept(APPLICATION_JSON).nest {
        POST("/log-by-location", workLogHandler::logByLocation)
        POST("/update-description/{intervalId}", workLogHandler::updateDescription)
        GET("/project-name-of-started-work", workLogHandler::getProjectNameOfStartedWork)
        GET("/has-work-started", workLogHandler::hasWorkStarted)
        GET("/collaborator/interval-ids", workLogHandler::getIntervalIdsByCollaborator)
        "/interval".nest {
            GET("/{intervalId}/endpoints", workLogHandler::getIntervalEndpoints)
            GET("/{intervalId}/description", workLogHandler::getDescription)
            GET("/{intervalIds}/durations-sum", workLogHandler::getDurationsSum)
        }
    }
}
