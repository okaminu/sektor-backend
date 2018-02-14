package lt.tlistas.loginn.backend

import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.core.service.confirmation.ConfirmationCodeService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

class ConfirmationHandler(private val collaboratorService: CollaboratorService,
                          private val confirmationCodeService: ConfirmationCodeService) {

    fun sendConfirmationCode(req: ServerRequest): Mono<ServerResponse> {
        return Mono.just(confirmationCodeService
                .sendCodeToCollaborator(req.pathVariable("mobileNumber")))
                .flatMap { ok().build() }
    }

    fun sendToken(req: ServerRequest): Mono<ServerResponse> {
        return Mono.just(confirmationCodeService.removeIfCodeIsValid(req.pathVariable("confirmationCode")))
                /*confirmationCodeService
                .sendTokenToCollaborator(confirmationCodeService
                        .getByConfirmationCode(req.pathVariable("confirmationCode"))?.collaborator))*/
                .flatMap { ok().build() }
    }
}