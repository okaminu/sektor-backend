package lt.boldadmin.sektor.backend.handler.identityconfirmed

import lt.boldadmin.nexus.api.type.valueobject.Location
import lt.boldadmin.nexus.service.location.LocationWorkLogService
import lt.boldadmin.nexus.service.worklog.WorkLogService
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

class WorkLogHandler(
    private val locationWorkLogService: LocationWorkLogService,
    private val collaboratorAuthService: CollaboratorAuthenticationService,
    private val workLogService: WorkLogService
) {
    fun logByLocation(req: ServerRequest): Mono<ServerResponse> =
        req.bodyToMono<Location>()
            .doOnNext { locationWorkLogService.logWork(collaboratorAuthService.getCollaborator(req), it) }
            .flatMap { ok().build() }

    fun getIntervalEndpoints(req: ServerRequest) =
        ok().body(
            Mono.just(
                mapOf(
                    "workLogs" to workLogService.getIntervalEndpoints(req.pathVariable("intervalId")),
                    "workDuration" to workLogService.measureDuration(req.pathVariable("intervalId"))
                )
            )
        )

    fun getIntervalIdsByCollaborator(req: ServerRequest) =
        ok().body(
            Mono.just(workLogService.getByCollaboratorId(collaboratorAuthService.getCollaboratorId(req))
                .map { it.intervalId }
                .distinct())
        )

    fun getProjectNameOfStartedWork(req: ServerRequest): Mono<ServerResponse> =
        ok().body(
            BodyInserters.fromObject(
                workLogService.getProjectNameOfStartedWork(collaboratorAuthService.getCollaboratorId(req))
            )
        )

    fun getDescription(req: ServerRequest) =
        ok().body(
            Mono.just(workLogService.getDescription(req.pathVariable("intervalId")))
        )

    fun getDurationsSum(req: ServerRequest) =
        ok().body(
            Mono.just(workLogService.sumWorkDurations(req.pathVariable("intervalIds").split(",")))
        )

    fun updateDescription(req: ServerRequest): Mono<ServerResponse> =
        req.bodyToMono<String>()
            .doOnNext { workLogService.updateDescription(req.pathVariable("intervalId"), it) }
            .flatMap { ok().build() }

    fun hasWorkStarted(req: ServerRequest): Mono<ServerResponse> =
        ok().body(
            BodyInserters.fromObject(
                workLogService.hasWorkStarted(collaboratorAuthService.getCollaboratorId(req))
            )
        )

}