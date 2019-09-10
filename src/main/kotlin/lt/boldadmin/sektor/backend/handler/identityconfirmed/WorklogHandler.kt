package lt.boldadmin.sektor.backend.handler.identityconfirmed

import lt.boldadmin.nexus.api.service.worklog.status.WorklogStartEndService
import lt.boldadmin.nexus.api.service.worklog.status.location.WorklogLocationService
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.nexus.api.service.worklog.WorklogDurationService
import lt.boldadmin.nexus.api.service.worklog.WorklogService
import lt.boldadmin.nexus.api.service.worklog.WorklogStartEndService
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

open class WorklogHandler(
    private val collaboratorAuthService: CollaboratorAuthenticationService,
    private val workLogStartEndService: WorklogStartEndService
) {
    open fun getProjectNameOfStartedWork(req: ServerRequest): Mono<ServerResponse> =
        ok().body(
            fromObject(
                workLogStartEndService.getProjectOfStartedWork(
                    collaboratorAuthService.getCollaboratorId(req)
                ).name
            )
        )

    open fun hasWorkStarted(req: ServerRequest): Mono<ServerResponse> =
        ok().body(
            fromObject(
                workLogStartEndService.hasWorkStarted(
                    collaboratorAuthService.getCollaboratorId(req)
                )
            )
        )

    open fun getIntervalEndpointsByIntervalId(req: ServerRequest) =
        ok().body(
            Mono.just(
                mapOf(
                    "workLogs" to worklogService.getIntervalEndpoints(req.pathVariable("intervalId")),
                    "workDuration" to workLogDurationService.measureDuration(req.pathVariable("intervalId"))
                )
            )
        )
}