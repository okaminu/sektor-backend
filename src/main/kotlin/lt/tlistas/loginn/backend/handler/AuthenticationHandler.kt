package lt.tlistas.loginn.backend.handler

import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.crowbar.service.ConfirmationService
import lt.tlistas.crowbar.service.RequestService
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

open class AuthenticationHandler(private val requestService: RequestService,
                                 private val confirmationService: ConfirmationService,
                                 private val collaboratorService: CollaboratorService) {

    open fun requestConfirmationCode(req: ServerRequest): Mono<ServerResponse> =
            Mono.just(req.pathVariable("mobileNumber"))
                    .doOnNext { requestService.sendConfirmation(it, getCollaborator(it).id!!) }
                    .flatMap { ok().build() }


    open fun confirmCode(req: ServerRequest): Mono<ServerResponse> =
            Mono.just(req.pathVariable("code"))
                    .map { confirmationService.confirmCode(it) }
                    .flatMap { ok().body(fromObject(it)) }

    private fun getCollaborator(mobileNumber: String) = collaboratorService.getByMobileNumber(mobileNumber)

}