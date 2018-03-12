package lt.tlistas.loginn.backend.handler

import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.crowbar.service.RequestService
import lt.tlistas.crowbar.service.TokenService
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

open class IdentityConfirmationHandler(private val requestService: RequestService,
                                       private val tokenService: TokenService,
                                       private val collaboratorService: CollaboratorService) {

    open fun requestCode(req: ServerRequest): Mono<ServerResponse> =
            Mono.just(req.pathVariable("mobileNumber"))
                    .doOnNext { requestService.sendConfirmation(getCollaborator(it).id!!, it) }
                    .flatMap { ok().build() }


    open fun confirmCode(req: ServerRequest): Mono<ServerResponse> =
            Mono.just(req.pathVariable("code"))
                    .map { tokenService.confirmCode(it) }
                    .flatMap { ok().body(fromObject(it)) }

    private fun getCollaborator(mobileNumber: String) = collaboratorService.getByMobileNumber(mobileNumber)

}