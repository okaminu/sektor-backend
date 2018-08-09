package lt.boldadmin.sektor.backend.handler.identityconfirmed

import lt.boldadmin.nexus.api.type.valueobject.Location
import lt.boldadmin.nexus.service.location.LocationWorkLogService
import lt.boldadmin.nexus.service.worklog.WorkLogService
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

open class WorkLogHandler(
    private val locationWorkLogService: LocationWorkLogService,
    private val collaboratorAuthService: CollaboratorAuthenticationService,
    private val workLogService: WorkLogService
) {
    open fun logByLocation(req: ServerRequest): Mono<ServerResponse> =
        req.bodyToMono<Location>()
            .doOnNext { locationWorkLogService.logWork(collaboratorAuthService.getCollaborator(req), it) }
            .flatMap { ok().build() }

    open fun getProjectNameOfStartedWork(req: ServerRequest): Mono<ServerResponse> =
        ok().body(
            BodyInserters.fromObject(
                workLogService.getProjectNameOfStartedWork(collaboratorAuthService.getCollaboratorId(req))
            )
        )

    open fun updateDescription(req: ServerRequest): Mono<ServerResponse> =
        req.bodyToMono<String>()
            .doOnNext { workLogService.updateDescription(req.pathVariable("intervalId"), it)}
            .flatMap { ok().build() }

    open fun hasWorkStarted(req: ServerRequest): Mono<ServerResponse> =
        ok().body(
                BodyInserters.fromObject(
                        workLogService.hasWorkStarted(collaboratorAuthService.getCollaboratorId(req))
                )
        )
}