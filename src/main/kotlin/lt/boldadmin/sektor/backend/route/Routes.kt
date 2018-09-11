package lt.boldadmin.sektor.backend.route

import lt.boldadmin.sektor.backend.handler.IdentityConfirmationHandler
import lt.boldadmin.sektor.backend.handler.identityconfirmed.CollaboratorHandler
import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorkLogHandler
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router

internal fun router(
    workLogHandler: WorkLogHandler,
    collaboratorHandler: CollaboratorHandler,
    identityConfirmationHandler: IdentityConfirmationHandler
) = {
    router {
        "/collaborator".nest(collaboratorRoutes(collaboratorHandler, identityConfirmationHandler))
        "/worklog".nest(workLogRoutes(workLogHandler))
        GET("/healthy") { ok().body(fromObject(true)) }
    }
}