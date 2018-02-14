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
                POST("/confirmationCode/number/{mobileNumber}", confirmationHandler::sendConfirmationCode)
                POST("/confirmationToken/code/{confirmationCode}", confirmationHandler::sendToken)
            }
        }
    }
}