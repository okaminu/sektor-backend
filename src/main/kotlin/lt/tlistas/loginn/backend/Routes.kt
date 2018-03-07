package lt.tlistas.loginn.backend

import lt.tlistas.loginn.backend.handler.AuthenticationHandler
import lt.tlistas.loginn.backend.handler.CollaboratorHandler
import lt.tlistas.loginn.backend.handler.ConfirmationHandler
import lt.tlistas.loginn.backend.handler.WorkLogHandler
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router

class Routes(private val collaboratorHandler: CollaboratorHandler,
             private val confirmationHandler: ConfirmationHandler,
             private val workLogHandler: WorkLogHandler,
             private val authenticationHandler: AuthenticationHandler) {

    fun router() = router {
        "/collaborator".nest {
            accept(APPLICATION_JSON).nest {
                GET("/workTime", collaboratorHandler::getWorkTime)
            }
        }
        "/worklog".nest {
            accept(APPLICATION_JSON).nest {
                POST("/log-by-location", workLogHandler::logByLocation)
            }
        }
        "/mobile".nest {
            accept(APPLICATION_JSON).nest {
                POST("/register/mobileNumber/{mobileNumber}", confirmationHandler::sendConfirmationCode)
                POST("/confirm/code/{confirmationCode}", authenticationHandler::authenticate)
            }
        }
    }
}