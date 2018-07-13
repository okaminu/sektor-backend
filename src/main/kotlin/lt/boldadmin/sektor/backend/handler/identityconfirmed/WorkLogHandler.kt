package lt.boldadmin.sektor.backend.handler.identityconfirmed

import lt.boldadmin.nexus.api.type.Location
import lt.boldadmin.nexus.service.CollaboratorService
import lt.boldadmin.nexus.service.LocationWorkLogService
import lt.boldadmin.crowbar.IdentityConfirmation
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

open class WorkLogHandler(private val collaboratorService: CollaboratorService,
                          private val locationWorkLogService: LocationWorkLogService,
                          private val identityConfirmation: IdentityConfirmation) {

    open fun logByLocation(req: ServerRequest): Mono<ServerResponse> =
            req.bodyToMono<Location>()
                    .doOnNext { locationWorkLogService.logWork(getCollaborator(req), it) }
                    .flatMap { ok().build() }

    private fun getCollaborator(req: ServerRequest) = collaboratorService.getById(getUserId(req))

    private fun getUserId(req: ServerRequest) = identityConfirmation.getUserIdByToken(getToken(req))

    private fun getToken(req: ServerRequest) = req.headers().header("auth-token")[0]
}