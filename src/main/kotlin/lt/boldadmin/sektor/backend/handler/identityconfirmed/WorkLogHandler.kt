package lt.boldadmin.sektor.backend.handler.identityconfirmed

import lt.boldadmin.nexus.api.service.worklog.WorklogService
import lt.boldadmin.nexus.api.service.worklog.duration.WorklogDurationService
import lt.boldadmin.nexus.api.service.worklog.status.WorklogStartEndService
import lt.boldadmin.nexus.api.service.worklog.status.location.WorklogLocationService
import lt.boldadmin.nexus.api.type.valueobject.Location
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

open class WorkLogHandler(
    private val workLogLocationService: WorklogLocationService,
    private val collaboratorAuthService: CollaboratorAuthenticationService,
    private val worklogService: WorklogService,
    private val workLogStartEndService: WorklogStartEndService,
    private val workLogDurationService: WorklogDurationService
) {
    open fun getIntervalIdsByCollaborator(req: ServerRequest) =
        ok().body(
            Mono.just(worklogService.getByCollaboratorId(collaboratorAuthService.getCollaboratorId(req))
                .map { it.intervalId }
                .distinct())
        )

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

    open fun logByLocation(req: ServerRequest): Mono<ServerResponse> =
        req.bodyToMono<Location>()
            .doOnNext { workLogLocationService.logWork(collaboratorAuthService.getCollaborator(req), it) }
            .flatMap { ok().build() }

    open fun getIntervalEndpointsByIntervalId(req: ServerRequest) =
        ok().body(
            Mono.just(
                mapOf(
                    "workLogs" to worklogService.getIntervalEndpoints(req.pathVariable("intervalId")),
                    "workDuration" to workLogDurationService.measureDuration(req.pathVariable("intervalId"))
                )
            )
        )

    open fun getDurationsSumByIntervalIds(req: ServerRequest) =
        ok().body(
            Mono.just(workLogDurationService.sumWorkDurations(req.pathVariable("intervalIds").split(",")))
        )
}