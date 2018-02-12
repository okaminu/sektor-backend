package lt.tlistas.loginn.backend

import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.core.service.confirmation.ConfirmationCodeService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class ConfirmationHandler(private val collaboratorService: CollaboratorService,
                          private val confirmationCodeService: ConfirmationCodeService) {

    fun sendConfirmationCode(req: ServerRequest): Mono<ServerResponse> {
        return Mono.just(req.pathVariable("mobileNumber"))
                .doOnNext { confirmationCodeService.sendCodeToCollaborator(collaboratorService.getByMobileNumber(it)) }
                .flatMap { ServerResponse.ok().build() }
    }
}