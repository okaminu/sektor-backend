package lt.boldadmin.sektor.backend.route

import lt.boldadmin.sektor.backend.handler.IdentityConfirmationHandler
import lt.boldadmin.sektor.backend.handler.identityconfirmed.CollaboratorHandler
import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorkLogHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.RouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router

@Configuration
class Routes(
    private val workLogHandler: WorkLogHandler,
    private val collaboratorHandler: CollaboratorHandler,
    private val identityConfirmationHandler: IdentityConfirmationHandler
) {

    @Bean
    fun router() = router {
        "/collaborator".nest(collaboratorRoutes())
        "/worklog".nest(workLogRoutes())
        GET("/healthy") { ok().body(fromObject(true)) }
    }

    private fun collaboratorRoutes(): RouterFunctionDsl.() -> Unit = {
        accept(APPLICATION_JSON).nest {
            GET("/workTime", collaboratorHandler::getWorkTime)
        }
        "/identity-confirmation".nest {
            "/code".nest {
                accept(APPLICATION_JSON).nest {
                    POST("/request/{mobileNumber}", identityConfirmationHandler::requestCode)
                    POST("/confirm/{code}", identityConfirmationHandler::confirmCode)
                }
            }
        }
    }

    private fun workLogRoutes(): RouterFunctionDsl.() -> Unit = {
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
}