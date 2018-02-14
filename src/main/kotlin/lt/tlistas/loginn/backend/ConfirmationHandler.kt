package lt.tlistas.loginn.backend

import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

class ConfirmationHandler(private val confirmationCodeService: ConfirmationCodeService,
                          private val tokenService: AuthenticationTokenService) {

    fun sendConfirmationCode(req: ServerRequest): Mono<ServerResponse> {
        return Mono.just(confirmationCodeService
                .sendCodeToCollaborator(req.pathVariable("mobileNumber")))
                .flatMap { ok().build() }
    }

    fun authorize(req: ServerRequest): Mono<ServerResponse> {
        return Mono.just(tokenService.getAuthorizationToken(req.pathVariable("code")))
                .flatMap { ok().body(fromObject(it)) }
    }
}