package lt.boldadmin.sektor.backend.service

import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.nexus.api.service.collaborator.CollaboratorService
import org.springframework.web.reactive.function.server.ServerRequest

class CollaboratorAuthenticationService(
    private val collaboratorService: CollaboratorService,
    private val identityConfirmation: IdentityConfirmation
) {
    internal fun getCollaborator(req: ServerRequest) = collaboratorService.getById(getCollaboratorId(req))

    internal fun getCollaboratorId(req: ServerRequest) = identityConfirmation.getUserIdByToken(getToken(req))

    private fun getToken(req: ServerRequest) = req.headers().header("auth-token")[0]
}