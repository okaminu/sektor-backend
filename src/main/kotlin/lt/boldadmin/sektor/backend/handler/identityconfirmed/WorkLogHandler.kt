package lt.boldadmin.sektor.backend.handler.identityconfirmed

import lt.boldadmin.nexus.api.type.valueobject.Location
import lt.boldadmin.nexus.service.CollaboratorService
import lt.boldadmin.nexus.service.LocationWorkLogService
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.nexus.service.WorkLogService
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

open class WorkLogHandler(
    private val collaboratorService: CollaboratorService,
    private val locationWorkLogService: LocationWorkLogService,
    private val identityConfirmation: IdentityConfirmation,
    private val workLogService: WorkLogService
) {

    open fun logByLocation(req: ServerRequest): Mono<ServerResponse> =
        req.bodyToMono<Location>()
            .doOnNext { locationWorkLogService.logWork(getCollaborator(req), it) }
            .flatMap { ok().build() }

    open fun getProjectNameOfStartedWork(req: ServerRequest): Mono<ServerResponse> =
        ok().body(
                BodyInserters.fromObject(
                        workLogService.getProjectNameOfStartedWork(getCollaboratorId(req)))
        )

    private fun getCollaborator(req: ServerRequest) = collaboratorService.getById(getCollaboratorId(req))

    private fun getCollaboratorId(req: ServerRequest) = identityConfirmation.getUserIdByToken(getToken(req))

    private fun getToken(req: ServerRequest) = req.headers().header("auth-token")[0]
}