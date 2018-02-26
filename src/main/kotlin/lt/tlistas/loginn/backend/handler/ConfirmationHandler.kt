package lt.tlistas.loginn.backend.handler

import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.mobile.number.confirmation.service.ConfirmationService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

open class ConfirmationHandler(private val collaboratorService: CollaboratorService,
                          private val confirmationCodeService: ConfirmationService) {

    open fun sendConfirmationCode(req: ServerRequest): Mono<ServerResponse> {
        val mobileNumber = req.pathVariable("mobileNumber")
        return Mono.just(confirmationCodeService
                .sendConfirmation(mobileNumber, getCollaborator(mobileNumber).id!!))
                .flatMap { ok().build() }
    }

    private fun getCollaborator(mobileNumber: String) = collaboratorService.getByMobileNumber(mobileNumber)
}