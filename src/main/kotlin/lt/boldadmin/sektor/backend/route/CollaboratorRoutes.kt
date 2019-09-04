package lt.boldadmin.sektor.backend.route

import lt.boldadmin.sektor.backend.handler.CollaboratorMessageHandler
import lt.boldadmin.sektor.backend.handler.IdentityConfirmationHandler
import lt.boldadmin.sektor.backend.handler.identityconfirmed.CollaboratorHandler
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.RouterFunctionDsl

fun collaboratorRoutes(
    collaboratorHandler: CollaboratorHandler,
    collaboratorMessageHandler: CollaboratorMessageHandler,
    identityConfirmationHandler: IdentityConfirmationHandler
): RouterFunctionDsl.() -> Unit = {
    accept(APPLICATION_JSON).nest {
        GET("/workTime", collaboratorHandler::getWorkTime)
        "/location".nest {
            POST("/coordinates", collaboratorHandler::updateLocationByCoordinates)
            POST("/message", collaboratorMessageHandler::updateLocationByMessage)
        }
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