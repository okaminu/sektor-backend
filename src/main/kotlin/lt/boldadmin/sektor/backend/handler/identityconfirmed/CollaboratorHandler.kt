package lt.boldadmin.sektor.backend.handler.identityconfirmed

import lt.boldadmin.nexus.api.service.worklog.status.location.WorklogLocationService
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

open class CollaboratorHandler(
    private val collaboratorAuthService: CollaboratorAuthenticationService,
    private val worklogLocationService: WorklogLocationService
) {

    open fun getWorkTime(req: ServerRequest): Mono<ServerResponse> =
        ok().body(fromObject(collaboratorAuthService.getCollaborator(req).workTime))

    open fun updateLocationByCoordinates(req: ServerRequest): Mono<ServerResponse> =
        req.bodyToMono<Coordinates>()
            .doOnNext { worklogLocationService.logWork(collaboratorAuthService.getCollaboratorId(req), it) }
            .flatMap { ok().build() }
}