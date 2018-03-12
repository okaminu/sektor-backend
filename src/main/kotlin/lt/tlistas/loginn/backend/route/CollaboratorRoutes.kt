package lt.tlistas.loginn.backend.route

import lt.tlistas.loginn.backend.handler.AuthenticationHandler
import lt.tlistas.loginn.backend.handler.CollaboratorHandler
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router

class CollaboratorRoutes(private val collaboratorHandler: CollaboratorHandler,
                         private val authenticationHandler: AuthenticationHandler) {

    fun router() = router {
        "/collaborator".nest {
            accept(APPLICATION_JSON).nest {
                GET("/workTime", collaboratorHandler::getWorkTime)
            }
            "/authentication".nest {
                "/code".nest {
                    accept(APPLICATION_JSON).nest {
                        POST("/request/{mobileNumber}", authenticationHandler::requestConfirmationCode)
                        POST("/confirm/{code}", authenticationHandler::confirmCode)
                    }
                }
            }
        }
    }
}