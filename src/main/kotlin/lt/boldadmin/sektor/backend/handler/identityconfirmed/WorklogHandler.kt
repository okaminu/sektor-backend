package lt.boldadmin.sektor.backend.handler.identityconfirmed

import lt.boldadmin.nexus.api.service.worklog.WorklogStatusService
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

open class WorklogHandler(
    private val collaboratorAuthService: CollaboratorAuthenticationService,
    private val worklogStatusService: WorklogStatusService
) {

    open fun getProjectNameOfStartedWork(req: ServerRequest): Mono<ServerResponse> =
        ok().body(
            fromObject(
                worklogStatusService.getProjectOfStartedWork(
                    collaboratorAuthService.getCollaboratorId(req)
                ).name
            )
        )

    open fun hasWorkStarted(req: ServerRequest): Mono<ServerResponse> =
        ok().body(
            fromObject(
                worklogStatusService.hasWorkStarted(
                    collaboratorAuthService.getCollaboratorId(req)
                )
            )
        )
}