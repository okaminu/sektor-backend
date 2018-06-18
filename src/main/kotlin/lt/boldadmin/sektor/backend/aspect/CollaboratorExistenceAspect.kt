package lt.boldadmin.sektor.backend.aspect

import lt.boldadmin.nexus.service.CollaboratorService
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.sektor.backend.exception.CollaboratorNotFoundException
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order
import org.springframework.web.reactive.function.server.ServerRequest

@Aspect
@Order(1)
class CollaboratorExistenceAspect(private val collaboratorService: CollaboratorService,
                                  private val identityConfirmation: IdentityConfirmation) {

    @Before("execution(* lt.boldadmin.sektor.backend.IdentityConfirmationHandler.requestCode(..)) && args(req)")
    fun collaboratorExistsByMobileNumberAdvice(req: ServerRequest) {
        if (!collaboratorService.existsByMobileNumber(req.pathVariable("mobileNumber")))
            throw CollaboratorNotFoundException()
    }

    @Before("execution(* lt.boldadmin.sektor.backend.handler.identityconfirmed.*.*(..)) && args(req)")
    fun collaboratorExistsByIdAdvice(req: ServerRequest) {
        if (!collaboratorService.existsById(getUserId(req)))
            throw CollaboratorNotFoundException()
    }

    private fun getUserId(req: ServerRequest) = identityConfirmation.getUserIdByToken(getToken(req))

    private fun getToken(req: ServerRequest) = req.headers().header("auth-token")[0]
}