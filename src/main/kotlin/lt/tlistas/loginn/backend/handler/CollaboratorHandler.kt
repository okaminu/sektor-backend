package lt.tlistas.loginn.backend.handler

import lt.tlistas.core.api.type.Location
import lt.tlistas.core.service.LocationWorkLogService
import lt.tlistas.core.service.UserService
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Suppress("UNUSED_PARAMETER")
class CollaboratorHandler(private val userService: UserService,
                          private val locationWorkLogService: LocationWorkLogService) {

    fun getWorkTime(req: ServerRequest): Mono<ServerResponse> = ok().body(fromObject(getCollaborator().workTime))

    fun logWorkByLocation(req: ServerRequest): Mono<ServerResponse> {
        return req.bodyToMono<Location>()
                .doOnNext { locationWorkLogService.logWork(getCollaborator(), it) }
                .flatMap { ok().build() }
    }

    private fun getCollaborator() = userService.getByEmail("test@test.com")!!.company.collaborators.first()
}
