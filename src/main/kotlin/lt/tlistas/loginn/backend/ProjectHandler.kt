package lt.tlistas.loginn.backend

import lt.tlistas.core.factory.CustomerFactory
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Flux

@Suppress("UNUSED_PARAMETER")
class ProjectHandler (private var customerFactory: CustomerFactory) {
	
	fun findAll(req: ServerRequest) = ok().body(Flux.just(
			customerFactory.createWithDefaults()
	))

}
