package lt.boldadmin.sektor.backend.handler.identityconfirmed

import lt.boldadmin.nexus.api.type.valueobject.Location
import lt.boldadmin.nexus.service.worklog.WorkLogService
import lt.boldadmin.nexus.service.worklog.duration.WorkLogDurationService
import lt.boldadmin.nexus.service.worklog.status.WorkLogDescriptionService
import lt.boldadmin.nexus.service.worklog.status.WorkLogStartEndService
import lt.boldadmin.nexus.service.worklog.status.location.WorkLogLocationService
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

open class WorkLogHandler(
    private val workLogLocationService: WorkLogLocationService,
    private val collaboratorAuthService: CollaboratorAuthenticationService,
    private val workLogService: WorkLogService,
    private val workLogStartEndService: WorkLogStartEndService,
    private val workLogDescriptionService: WorkLogDescriptionService,
    private val workLogDurationService: WorkLogDurationService
) {
    open fun getIntervalIdsByCollaborator(req: ServerRequest) =
        ok().body(
            Mono.just(workLogService.getByCollaboratorId(collaboratorAuthService.getCollaboratorId(req))
                .map { it.intervalId }
                .distinct())
        )

    open fun getProjectNameOfStartedWork(req: ServerRequest): Mono<ServerResponse> =
        ok().body(
            fromObject(
                workLogStartEndService.getProjectNameOfStartedWork(
                    collaboratorAuthService.getCollaboratorId(req)
                )
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
                    "workLogs" to workLogService.getIntervalEndpoints(req.pathVariable("intervalId")),
                    "workDuration" to workLogDurationService.measureDuration(req.pathVariable("intervalId"))
                )
            )
        )

    open fun getDescriptionByIntervalId(req: ServerRequest) =
        ok().body(
            Mono.just(workLogDescriptionService.getDescription(req.pathVariable("intervalId")))
        )

    open fun getDurationsSumByIntervalIds(req: ServerRequest) =
        ok().body(
            Mono.just(workLogDurationService.sumWorkDurations(req.pathVariable("intervalIds").split(",")))
        )

    open fun updateDescriptionByIntervalId(req: ServerRequest): Mono<ServerResponse> =
        req.bodyToMono<String>()
            .doOnNext { workLogDescriptionService.updateDescription(req.pathVariable("intervalId"), it) }
            .flatMap { ok().build() }

}