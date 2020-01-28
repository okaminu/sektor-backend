package lt.boldadmin.sektor.backend.handler.identityconfirmed

import lt.boldadmin.nexus.api.event.publisher.CollaboratorCoordinatesPublisher
import lt.boldadmin.nexus.api.type.valueobject.Coordinates
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

open class CollaboratorHandler(
    private val service: CollaboratorAuthenticationService,
    private val publisher: CollaboratorCoordinatesPublisher
) {

    open fun getWorkTime(req: ServerRequest): Mono<ServerResponse> =
        ok().body(fromObject(service.getCollaborator(req).workWeek))

    open fun updateLocationByCoordinates(req: ServerRequest): Mono<ServerResponse> =
        req.bodyToMono<Coordinates>()
            .doOnNext { publisher.publish(service.getCollaboratorId(req), it) }
            .flatMap { ok().build() }
}
