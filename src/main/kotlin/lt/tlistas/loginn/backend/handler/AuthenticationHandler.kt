package lt.tlistas.loginn.backend.handler

import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.crowbar.service.ConfirmationService
import lt.tlistas.crowbar.service.RequestService
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

open class AuthenticationHandler(private val requestService: RequestService,
                                 private val confirmationService: ConfirmationService,
                                 private val collaboratorService: CollaboratorService) {

    open fun requestConfirmationCode(req: ServerRequest): Mono<ServerResponse> {
        val mobileNumber = req.pathVariable("mobileNumber")
        return Mono.just(requestService
                .sendConfirmation(getCollaborator(mobileNumber).id!!, mobileNumber))
                .flatMap { ok().build() }
    }

    open fun confirmCode(req: ServerRequest): Mono<ServerResponse> =
            Mono.just(confirmationService.confirmCode(req.pathVariable("code")))
                    .flatMap { ok().body(BodyInserters.fromObject(it)) }

    private fun getCollaborator(mobileNumber: String) = collaboratorService.getByMobileNumber(mobileNumber)

}