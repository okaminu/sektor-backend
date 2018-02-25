package lt.tlistas.loginn.backend.handler

import lt.tlistas.mobile.number.confirmation.service.AuthenticationService
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

class AuthenticationHandler(private val authService: AuthenticationService) {

    fun authenticate(req: ServerRequest): Mono<ServerResponse> =
            Mono.just(authService.getAuthenticationToken(req.pathVariable("confirmationCode")))
                    .flatMap { ok().body(BodyInserters.fromObject(it)) }

}