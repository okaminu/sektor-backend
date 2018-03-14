package lt.tlistas.loginn.backend.handler.identityconfirmed

import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.crowbar.IdentityConfirmation
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

open class CollaboratorHandler(private val collaboratorService: CollaboratorService,
                               private val identityConfirmation: IdentityConfirmation) {

    open fun getWorkTime(req: ServerRequest): Mono<ServerResponse> =
            ok().body(fromObject(getCollaborator(req).workTime))

    private fun getCollaborator(req: ServerRequest) = collaboratorService.getById(getCollaboratorId(req))

    private fun getCollaboratorId(req: ServerRequest) = identityConfirmation.getUserIdByToken(getToken(req))

    private fun getToken(req: ServerRequest) = req.headers().header("auth-token")[0]
}