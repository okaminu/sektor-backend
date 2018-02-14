package lt.tlistas.loginn.backend.handler

import lt.tlistas.core.api.type.Location
import lt.tlistas.core.service.LocationWorkLogService
import lt.tlistas.core.service.confirmation.AuthenticationService
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

class CollaboratorHandler(private val locationWorkLogService: LocationWorkLogService,
                          private val authenticationService: AuthenticationService) {

    fun getWorkTime(req: ServerRequest): Mono<ServerResponse> = ok().body(fromObject(getCollaborator(req).workTime))

    fun logWorkByLocation(req: ServerRequest): Mono<ServerResponse> {
        return req.bodyToMono<Location>()
                .doOnNext { locationWorkLogService.logWork(getCollaborator(req), it) }
                .flatMap { ok().build() }
    }

    private fun getCollaborator(req: ServerRequest) = authenticationService.getCollaboratorByToken(getToken(req))

    private fun getToken(req: ServerRequest) = req.headers().header("auth-token")[0]
}
