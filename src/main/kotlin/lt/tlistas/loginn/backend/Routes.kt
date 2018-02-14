package lt.tlistas.loginn.backend

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router

class Routes(private val collaboratorHandler: CollaboratorHandler,
             private val confirmationHandler: ConfirmationHandler) {

    fun router() = router {
        "/collaborator".nest {
            accept(APPLICATION_JSON).nest {
                GET("/workTime", collaboratorHandler::getWorkTime)
                POST("/logWorkByLocation", collaboratorHandler::logWorkByLocation)

            }
        }
        "/confirmation".nest {
            accept(APPLICATION_JSON).nest {
                POST("/confirm/number/{mobileNumber}", confirmationHandler::sendConfirmationCode)
                POST("/authorize/code/{code}", confirmationHandler::authorize)
            }
        }
    }
}