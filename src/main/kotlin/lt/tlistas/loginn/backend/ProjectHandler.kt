package lt.tlistas.loginn.backend

import lt.tlistas.core.type.entity.Project
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Flux

@Suppress("UNUSED_PARAMETER")
class ProjectHandler {
	
	private val users = Flux.just(
            Project(name = "Hello there"),
            Project(name = "Nice meeting you")
    )

	fun findAll(req: ServerRequest) = ok().body(users)

}
