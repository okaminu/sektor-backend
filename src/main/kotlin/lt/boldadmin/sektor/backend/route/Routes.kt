package lt.boldadmin.sektor.backend.route

import lt.boldadmin.sektor.backend.handler.CollaboratorMessageHandler
import lt.boldadmin.sektor.backend.handler.IdentityConfirmationHandler
import lt.boldadmin.sektor.backend.handler.identityconfirmed.CollaboratorHandler
import lt.boldadmin.sektor.backend.handler.identityconfirmed.ProjectHandler
import lt.boldadmin.sektor.backend.handler.identityconfirmed.WorklogHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.router

@Configuration
class Routes(
    private val worklogHandler: WorklogHandler,
    private val collaboratorHandler: CollaboratorHandler,
    private val collaboratorMessageHandler: CollaboratorMessageHandler,
    private val identityConfirmationHandler: IdentityConfirmationHandler,
    private val projectHandler: ProjectHandler
) {

    @Bean
    fun router() = router {
        "/collaborator".nest(
            collaboratorRoutes(collaboratorHandler, collaboratorMessageHandler, identityConfirmationHandler)
        )
        "/worklog".nest(worklogRoutes(worklogHandler))
        GET("/projects", projectHandler::getProjects)
        GET("/is-healthy") { ok().body(fromObject(true)) }
    }

}
