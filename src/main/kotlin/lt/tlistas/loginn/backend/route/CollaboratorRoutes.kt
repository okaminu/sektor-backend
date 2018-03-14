package lt.tlistas.loginn.backend.route

import lt.tlistas.loginn.backend.handler.IdentityConfirmationHandler
import lt.tlistas.loginn.backend.handler.token.CollaboratorHandler
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router

class CollaboratorRoutes(private val collaboratorHandler: CollaboratorHandler,
                         private val identityConfirmationHandler: IdentityConfirmationHandler) {

    fun router() = router {
        "/collaborator".nest {
            accept(APPLICATION_JSON).nest {
                GET("/workTime", collaboratorHandler::getWorkTime)
            }
            "/authentication".nest {
                "/code".nest {
                    accept(APPLICATION_JSON).nest {
                        POST("/request/{mobileNumber}", identityConfirmationHandler::requestCode)
                        POST("/confirm/{code}", identityConfirmationHandler::confirmCode)
                    }
                }
            }
        }
    }
}