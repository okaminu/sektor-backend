package lt.tlistas.loginn.backend.handler

import lt.tlistas.mobile.number.confirmation.service.ConfirmationService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

class ConfirmationHandler(private val confirmationCodeService: ConfirmationService) {

    fun sendConfirmationCode(req: ServerRequest): Mono<ServerResponse> {
        return Mono.just(confirmationCodeService
                .sendConfirmation(req.pathVariable("mobileNumber")))
                .flatMap { ok().build() }
    }
}