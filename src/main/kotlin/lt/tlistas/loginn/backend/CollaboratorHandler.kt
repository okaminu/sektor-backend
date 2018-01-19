package lt.tlistas.loginn.backend

import lt.tlistas.core.exception.LocationByAddressNotFoundException
import lt.tlistas.core.exception.LocationGatewayException
import lt.tlistas.core.service.LocationLoggingService
import lt.tlistas.core.service.UserService
import lt.tlistas.core.type.Location
import lt.tlistas.core.type.entity.Collaborator
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

@Suppress("UNUSED_PARAMETER")
class CollaboratorHandler(userService: UserService,
						  private var locationLoggingService: LocationLoggingService) {

	private val collaborator: Collaborator = userService.getByEmail("test@test.com")!!.company.collaborators.first()

	fun getWorkTime(req: ServerRequest): Mono<ServerResponse> = ok().body(fromObject(collaborator.workTime))

	@Throws(LocationGatewayException::class, LocationByAddressNotFoundException::class)
	fun logWorkByLocation(req: ServerRequest): Mono<ServerResponse> {
		try {
			req.bodyToMono<Location>()
					.subscribe({locationLoggingService.logWorkByLocation(collaborator, it)})
		} catch (e: LocationByAddressNotFoundException) {
			return notFound().build()
		} catch (e: LocationGatewayException) {
			return status(HttpStatus.SERVICE_UNAVAILABLE).build()
		}
		return ok().build()
	}
}
