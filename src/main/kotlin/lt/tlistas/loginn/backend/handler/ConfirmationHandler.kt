package lt.tlistas.loginn.backend.handler

import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.loginn.backend.exception.CollaboratorDoesntExistException
import lt.tlistas.mobile.number.confirmation.service.ConfirmationService
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

class ConfirmationHandler(private val collaboratorService: CollaboratorService,
                          private val confirmationCodeService: ConfirmationService) {

    fun sendConfirmationCode(req: ServerRequest): Mono<ServerResponse> {
        val mobileNumber = req.pathVariable("mobileNumber")
        return Mono.just(confirmationCodeService
                .sendConfirmation(mobileNumber, getCollaboratorIfExists(mobileNumber).id!!))
                .flatMap { ok().build() }
    }

    internal fun getCollaboratorIfExists(number: String): Collaborator {
        if (!collaboratorService.existsByMobileNumber(number))
            throw CollaboratorDoesntExistException()

        return collaboratorService.getByMobileNumber(number)
    }
}