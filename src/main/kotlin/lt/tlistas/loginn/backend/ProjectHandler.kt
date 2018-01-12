package lt.tlistas.loginn.backend

import lt.tlistas.core.factory.CustomerFactory
import lt.tlistas.core.service.CustomerService
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Suppress("UNUSED_PARAMETER")
class ProjectHandler (private var customerFactory: CustomerFactory,
					  private var customerService: CustomerService) {
	
	fun findAll(req: ServerRequest) = ok().body(Flux.just(
			customerFactory.createWithDefaults()
	))

	fun save(req: ServerRequest): Mono<ServerResponse> {

		return ok().body(fromObject(customerService.getById("5a4e38a050de905d033a3b8a")))
	}

}
