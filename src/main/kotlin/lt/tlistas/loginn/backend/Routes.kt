package lt.tlistas.loginn.backend

import lt.tlistas.loginn.backend.handler.AuthenticationHandler
import lt.tlistas.loginn.backend.handler.CollaboratorHandler
import lt.tlistas.loginn.backend.handler.ConfirmationHandler
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router

class Routes(private val collaboratorHandler: CollaboratorHandler,
             private val confirmationHandler: ConfirmationHandler,
             private val authenticationHandler: AuthenticationHandler) {

    fun router() = router {
        "/collaborator".nest {
            accept(APPLICATION_JSON).nest {
                GET("/workTime", collaboratorHandler::getWorkTime)
                POST("/logWorkByLocation", collaboratorHandler::logWorkByLocation)

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