package lt.boldadmin.sektor.backend.route

import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorklogHandler
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.RouterFunctionDsl

fun worklogRoutes(worklogHandler: WorklogHandler): RouterFunctionDsl.() -> Unit = {
    accept(APPLICATION_JSON).nest {
        GET("/project-name-of-started-work", worklogHandler::getProjectNameOfStartedWork)
        GET("/has-work-started", worklogHandler::hasWorkStarted)
    }
}
