package lt.boldadmin.sektor.backend.handler.identityconfirmed

import lt.boldadmin.nexus.api.service.UserService
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

open class ProjectHandler(
    private val authService: CollaboratorAuthenticationService,
    private val userService: UserService
) {
    open fun getProjects(req: ServerRequest): Mono<ServerResponse> =
        ok().body(fromObject(userService.getByCollaboratorId(authService.getCollaboratorId(req)).projects))
}
